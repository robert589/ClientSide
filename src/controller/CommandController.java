package controller;

import client.MessageType;
import entity.ServerResponse;
import marshalling.Marshaller;
import marshalling.UnMarshaller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import entity.FileServerThread;
/**
 * This controller is the primary controller that sends and receives command from the server
 * Created by user on 1/4/2016.
 */
public class CommandController {
    /**
     * Port of the server
     */
    private int port;

    /**
     * Address of the server
     */
    private InetAddress address;

    /**
     * socket to send and receive data
     */
    private static DatagramSocket socket = null;

    /**
     * sequence number of the client side
     */
    private int sequenceNum = 0;

    /**
     * Control the cache list
     */
    private CacheController cacheController = new CacheController();

    /**
     * if it does not get response in the socket after this period of time, it will timeout and halt the request
     */
    private final int TIMEOUT = 10000;

    public CommandController(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {

        this.address = address;

    }

    /**
     * Waiting for response after sending data, if it is timeout, it will return null
     * @return ServerResponse
     */
    private ServerResponse waitingForResponse(){
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < TIMEOUT) {

            System.out.println("Waiting for response");
            byte[] recvBuf = new byte[FileServerThread.MAX_PACKET_BYTES];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

            ServerResponse response = receive(packet);
            System.out.println(response);
            if(response != null){
                return response;
            }

        }
        return null;
    }

    /**
     * Monitor whether there is a message from the server
     * @param intervalInMillis
     */
    private void monitorResponse(int intervalInMillis){
        System.out.println("Waiting for "  + intervalInMillis);
        long startTime = System.currentTimeMillis(); //fetch starting time
        while((System.currentTimeMillis()-startTime)<10000)
        {
            byte[] recvBuf = new byte[FileServerThread.MAX_PACKET_BYTES];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

            ServerResponse response = receive(packet);
            System.out.println(response.getTemplate());
            // do something
        }
    }

    /**
     * First the function will read the cache, if it does not exist, it will return null,
     * if it exists, but it is outdated, it will check with server whether the file is uptodate
     * if it is uptodate, read from the cache
     * it is is not up-to-date, update the cache with the new fresh data from the server
     * else read the cache
     * @param filePath
     * @param offset
     * @param numOfBytes
     * @throws Exception
     */
    public void readFileContent(String filePath, int offset, int numOfBytes) throws Exception {
        //set the operation to be read

        socket = new DatagramSocket();

        byte[] readFromCache = cacheController.readFile(filePath, offset, numOfBytes);
        //if cache exist
        if(readFromCache != null){
            //if cache is outdated
            if(Arrays.equals(readFromCache, CacheController.DATA_IS_OUTDATED)){
                send(new Marshaller((byte) MessageType.GET_ATTRIBUTES, filePath, sequenceNum++).getBytes());
                ServerResponse response = waitingForResponse();
                Long timeServer = Long.parseLong(response.getStatus());

                readFromCache = cacheController.CheckUptodate(filePath, timeServer);
                //cache is not up to date with the server
                if(readFromCache == null){
                    System.out.println("The server has new updated file");
                    // Parameters for READ_FILE - PathName, Offset, Length (set this to -1 if you want to read to end), SequenceNumber
                    send(new Marshaller((byte) MessageType.READ_FILE, filePath, offset, numOfBytes, sequenceNum++).getBytes());
                    response =  waitingForResponse();
                    System.out.println(response.getTemplate());
                    cacheController.addNew(filePath, offset, numOfBytes, response.getStatus().getBytes());
                }
                //cache is uptodate
                else{
                    System.out.println("The client file is up to date in the server");
                    System.out.println("Read from cache" + new String(readFromCache));
                }
            }
            else{
                System.out.println("Read from cache" + new String(readFromCache));
            }
        }
        //empty cache
        else{
            // Parameters for READ_FILE - PathName, Offset, Length (set this to -1 if you want to read to end), SequenceNumber
            send(new Marshaller((byte) MessageType.READ_FILE, filePath, offset, numOfBytes, sequenceNum++).getBytes());
            ServerResponse response =  waitingForResponse();
            if(response != null){

                System.out.println(response.getTemplate());
                cacheController.addNew(filePath, offset, numOfBytes, response.getStatus().getBytes());

            }
            // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
        }
    }

    /**
     * Insert content to file , it can be at least once or at most once mode
     * @param filePath
     * @param offset
     * @param bytesToWrite
     * @param at_most_once
     * @throws Exception
     */
    public void writeFileContent(String filePath, int offset, byte[] bytesToWrite, boolean at_most_once) throws Exception {

        socket = new DatagramSocket();

        if(at_most_once){

            //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
            send(new Marshaller((byte) MessageType.INSERT_FILE, filePath, offset, bytesToWrite, sequenceNum++).getBytes());
        }
        else{
            send(new Marshaller((byte) MessageType.AT_LEAST_ONCE_DEMO_INSERT_FILE, filePath, offset, bytesToWrite, -1).getBytes());
        }

        ServerResponse response = waitingForResponse();
        System.out.println(response.getTemplate());
    }

    /**
     * User this one will not increase the sequest number, the server will notice this as duplicate
     * @param filePath
     * @param offset
     * @param bytesToWrite
     * @return
     * @throws Exception
     */
    public void writeDuplicateFileContent(String filePath, int offset, byte[] bytesToWrite, boolean at_most_once) throws  Exception{

        socket = new DatagramSocket();

        if(at_most_once){
            //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
            send(new Marshaller((byte) MessageType.INSERT_FILE, filePath, offset, bytesToWrite, sequenceNum).getBytes());
            send(new Marshaller((byte) MessageType.INSERT_FILE, filePath, offset, bytesToWrite, sequenceNum).getBytes());

        }
        else{
            send(new Marshaller((byte) MessageType.useAtLeastOnce(MessageType.AT_LEAST_ONCE_DEMO_INSERT_FILE), filePath, offset, bytesToWrite, -1).getBytes());
            send(new Marshaller((byte) MessageType.useAtLeastOnce(MessageType.AT_LEAST_ONCE_DEMO_INSERT_FILE), filePath, offset, bytesToWrite, -1).getBytes());
        }

        ServerResponse response = waitingForResponse();
        System.out.println(response.getTemplate());
    }

    /**
     * Monitor file for specific amount of time
     * @param filePath
     * @param intervalMilliSeconds
     * @throws Exception
     */
    public void monitorFile(String filePath, int intervalMilliSeconds) throws Exception {

        socket = new DatagramSocket();

        //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
        send(new Marshaller((byte) MessageType.MONITOR_FILE, filePath, intervalMilliSeconds, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
     monitorResponse(intervalMilliSeconds);
    }


    /**
     * This function is to test idempotent operation
     * @param filePath
     * @param at_most_once
     * @throws Exception
     */
    public void deleteFileDuplicateRequest(String filePath, boolean at_most_once) throws  Exception{
        socket = new DatagramSocket();

        if(at_most_once){
            send(new Marshaller((byte) MessageType.DELETE_FILE, filePath, sequenceNum).getBytes());
            send(new Marshaller((byte) MessageType.DELETE_FILE, filePath, sequenceNum).getBytes());
        }
        else{
            send(new Marshaller((byte)MessageType.useAtLeastOnce( MessageType.DELETE_FILE), filePath, sequenceNum).getBytes());
            send(new Marshaller((byte) MessageType.useAtLeastOnce( MessageType.DELETE_FILE), filePath, sequenceNum).getBytes());
        }

        ServerResponse response = waitingForResponse();
        System.out.println(response.getTemplate());
    }

    /**
     * This function is used to send command to the server
     * @param buf
     * @throws IOException
     */
    private void send(byte[] buf) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                this.address, this.port);
        System.out.println("Sending command to server.....");
        socket.send(packet);
    }

    /**
     * Use for receiving data/command from the server
     * @param packet
     * @return
     */
    private ServerResponse receive(DatagramPacket packet) {
        try {
            socket.setSoTimeout(TIMEOUT);
            socket.receive(packet);
            UnMarshaller um = new UnMarshaller(packet.getData());
            int resType = (byte) um.getNextByte();
            String response;
            String seq_num;
            String template;
            // Handles responses
            switch (resType) {
                case MessageType.RESPONSE_MSG:
                    response = (String)um.getNext();
                    seq_num = (String) um.getNext();
                    template = "Response Received: " + response + " seq num = " + seq_num;
                    // Only normal responses have the sequence numbers embedded

                    return new ServerResponse(response, seq_num, template, MessageType.RESPONSE_MSG);
                case MessageType.RESPONSE_BYTES:

                    response = new String((byte[]) um.getNext());
                    seq_num = Integer.toString((int)(um.getNext()));
                    template = "Bytes received - length: " + response + " seq num = " + um.getNext();
                    // Only normal responses have the sequence numbers embedded
                    return new ServerResponse(response, seq_num, template, MessageType.RESPONSE_BYTES);

                case MessageType.CALLBACK:
                    response = (String) um.getNext();
                    seq_num = Integer.toString(((byte[]) um.getNext()).length);
                    template = "Successfully read it from server: " + response + ",seq num = " + um.getNext();
                    return new ServerResponse(response, seq_num, template, MessageType.CALLBACK);

                case MessageType.ERROR:
                    response = String.valueOf( um.getNext());
                    seq_num = (String) um.getNext();
                    template = "Error occured - code " + response + ": " + seq_num;
                    return new ServerResponse(response, seq_num,template, MessageType.ERROR);

                case MessageType.RESPONSE_PATH:
                    // Only normal responses have the sequence numbers embedded
                    String path = (String) um.getNext();
                    send(new Marshaller((byte) MessageType.DELETE_FILE, path, sequenceNum++).getBytes());
                    seq_num = (String)um.getNext();
                    template = "Duplicated file path recvd: " + path + " seq num = " + seq_num;
                    return new ServerResponse(path,seq_num, template, MessageType.RESPONSE_PATH);

                case MessageType.RESPONSE_SUCCESS:
                    response = (String) um.getNext();
                    seq_num = Integer.toString((int)um.getNext());
                    template = response + " seq num = " + seq_num;
                    return new ServerResponse(response, seq_num,template, MessageType.RESPONSE_SUCCESS);

                case MessageType.RESPONSE_ATTRIBUTES:
                    response = (String) um.getNext();
                    template = "From Server: Last modification at: " + response ;
                    return new ServerResponse(response,template, MessageType.RESPONSE_ATTRIBUTES);

            }
        }
        catch (SocketTimeoutException e){
            System.out.println("Timeout");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }
}

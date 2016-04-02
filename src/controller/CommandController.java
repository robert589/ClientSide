package controller;

import client.MessageType;
import marshalling.Marshaller;
import marshalling.UnMarshaller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by user on 1/4/2016.
 */
public class CommandController {
    private int port;

    private InetAddress address;

    private static DatagramSocket socket = null;

    private int sequenceNum = 0;

    private int operation;

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


    public void readFileContent(String filePath, int offset, int numOfBytes) throws Exception {
        this.operation = MessageType.READ_COMMAND;

        socket = new DatagramSocket();

        // Parameters for READ_FILE - PathName, Offset, Length (set this to -1 if you want to read to end), SequenceNumber
        send(new Marshaller((byte) MessageType.READ_FILE, filePath, offset, numOfBytes, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
    }


    public void writeFileContent(String filePath, int offset, byte[] bytesToWrite) throws Exception {
        this.operation = MessageType.INSERT_COMMAND;

        socket = new DatagramSocket();

        //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
        send(new Marshaller((byte) MessageType.INSERT_FILE, filePath, offset, bytesToWrite, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
    }

    public void monitorFile(String filePath, int intervalMilliSeconds) throws Exception {
        this.operation = MessageType.MONITOR_COMMAND;

        socket = new DatagramSocket();

        //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
        send(new Marshaller((byte) MessageType.MONITOR_FILE, filePath, intervalMilliSeconds, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
    }


    private void send(byte[] buf) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                this.address, this.port);
        socket.send(packet);
    }

    public String receive(DatagramPacket packet) {
        try {
            socket.receive(packet);

            UnMarshaller um = new UnMarshaller(packet.getData());
            int resType = (byte) um.getNextByte();

            // Handles responses
            switch (resType) {
                case MessageType.RESPONSE_MSG:
                    // Only normal responses have the sequence numbers embedded
                    return "Response Received: " + (String) um.getNext() + " seq num = " + um.getNext();

                case MessageType.RESPONSE_BYTES:
                    // Only normal responses have the sequence numbers embedded
                    return "Bytes received - length: " + new String((byte[]) um.getNext()) + " seq num = " + um.getNext();

                case MessageType.CALLBACK:
                    return "Callback received for " + um.getNext() + " - update length: " + ((byte[]) um.getNext()).length;
                case MessageType.ERROR:
                    return "Error occured - code " + (int) um.getNext() + ": " + um.getNext();

                case MessageType.RESPONSE_PATH:
                    // Only normal responses have the sequence numbers embedded
                    String path = (String) um.getNext();
                    send(new Marshaller((byte) MessageType.DELETE_FILE, path, sequenceNum++).getBytes());
                    return "Duplicated file path recvd: " + path + " seq num = " + um.getNext();
                case MessageType.RESPONSE_SUCCESS:
                    return um.getNext() + " seq num = " + um.getNext();
                default:
                    return "Strange request received" + um.getNext();

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;

    }
}

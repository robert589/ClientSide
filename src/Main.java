import client.MessageType;
import marshalling.Marshaller;
import marshalling.UnMarshaller;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by user on 24/3/2016.
 */
public class Main {
    static DatagramSocket socket = null;

    static int port;
    static InetAddress address;

    byte[] sendBuf = new byte[256];

    static int sequenceNum = 0;


    static void send(byte[] buf) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                Main.address, Main.port);
        socket.send(packet);
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);


        System.out.println("Welcome to the client side");

        System.out.print("Specify server port: ");
        Main.port = sc.nextInt();

        System.out.print("Specify server IP: ");
        String address = sc.next();

        Main.address = InetAddress.getByName(address);

        System.out.println();

        while(true) {
            try {

                System.out.println("What do you want to do?");
                System.out.println("1. Read the content of the file.");
                System.out.println("2. Insert content into a file.");
                System.out.println("3. Monitor the client. ");
                System.out.println("4. Simulate loss transmission.");
                System.out.println("0. Exit");
                System.out.print("Input the choice:");
                int choice = sc.nextInt();

                if(choice == 0){
                        break;
                }
                switch (choice) {
                    case 1:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        String filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        int offset = sc.nextInt();


                        System.out.print("Please input the number of bytes:");
                        sc = new Scanner(System.in);
                        int numOfBytes = sc.nextInt();

                        readFileContent(filePath, offset, numOfBytes);
                        break;

                    case 2:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        offset = sc.nextInt();


                        System.out.print("Please input the bytes to write into the file:");
                        sc = new Scanner(System.in);
                        String bytesToWrite = sc.nextLine();

                        writeFileContent(filePath, offset, bytesToWrite.getBytes());
                        break;

                    case 3:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        int intervalMilli = sc.nextInt();

                        monitorFile(filePath, intervalMilli);
                        break;

                }

                while (true) {
                    byte[] recvBuf = new byte[FileServerThread.MAX_PACKET_BYTES];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(packet);

                    UnMarshaller um = new UnMarshaller(packet.getData());
                    int resType = (byte) um.getNextByte();

                    // Handles responses
                    switch (resType) {
                        case MessageType.RESPONSE_MSG:
                            // Only normal responses have the sequence numbers embedded
                            System.out.println("Response Received: " + (String) um.getNext() + " seq num = " + um.getNext());
                            break;
                        case MessageType.RESPONSE_BYTES:
                            // Only normal responses have the sequence numbers embedded
                            System.out.println("Bytes received - length: " + new String((byte[]) um.getNext()) + " seq num = " + um.getNext());
                            break;
                        case MessageType.CALLBACK:
                            System.out.println("Callback received for " + um.getNext() + " - update length: " + ((byte[]) um.getNext()).length);
                            break;
                        case MessageType.ERROR:
                            System.err.println("Error occured - code " + (int) um.getNext() + ": " + um.getNext());
                            break;
                        case MessageType.RESPONSE_PATH:
                            // Only normal responses have the sequence numbers embedded
                            String path = (String) um.getNext();
                            System.out.println("Duplicated file path recvd: " + path + " seq num = " + um.getNext());
                            // Sends a delete request with the newly gotten file name as the parameter
                            send(new Marshaller((byte) MessageType.DELETE_FILE, path, sequenceNum++).getBytes());
                            break;
                        case MessageType.RESPONSE_SUCCESS:
                            System.out.println(um.getNext() + " seq num = " + um.getNext());
                            break;
                        default:
                            System.err.println("Strange request received" + um.getNext());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static private void readFileContent(String filePath, int offset, int numOfBytes) throws  Exception{
        socket = new DatagramSocket();

        // Parameters for READ_FILE - PathName, Offset, Length (set this to -1 if you want to read to end), SequenceNumber
        send(new Marshaller((byte) MessageType.READ_FILE, filePath, offset, numOfBytes, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
    }


    static private void writeFileContent(String filePath, int offset, byte[] bytesToWrite) throws  Exception{
        socket = new DatagramSocket();

        //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
        send(new Marshaller((byte) MessageType.INSERT_FILE, filePath, offset, bytesToWrite, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
    }

    static private void monitorFile(String filePath, int intervalMilliSeconds) throws Exception{
        socket = new DatagramSocket();

        //send(new Marshaller((byte) MessageType.INSERT_FILE, "test", 1, "b".getBytes(), sequenceNum).getBytes());
        send(new Marshaller((byte) MessageType.MONITOR_FILE, filePath, intervalMilliSeconds, sequenceNum++).getBytes());
        // Parameters for MONITOR_FILE - PathName, IntervalMilliseconds, SequenceNumber
    }
}
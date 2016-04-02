import client.ClientResponse;
import client.MessageType;
import controller.CommandController;
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
    byte[] sendBuf = new byte[256];


    /**
     * Current Command or operation
     */
    //private int operation;

    static    ClientResponse responseClient = new ClientResponse();



    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the client side");

        System.out.print("Specify server port: ");
        int port = sc.nextInt();

        System.out.print("Specify server IP: ");
        String address = sc.next();

        CommandController controller = new CommandController(port, InetAddress.getByName(address));

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
                    case MessageType.READ_COMMAND:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        String filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        int offset = sc.nextInt();


                        System.out.print("Please input the number of bytes:");
                        sc = new Scanner(System.in);
                        int numOfBytes = sc.nextInt();

                        controller.readFileContent(filePath, offset, numOfBytes);
                        break;

                    case MessageType.INSERT_COMMAND:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        offset = sc.nextInt();


                        System.out.print("Please input the bytes to write into the file:");
                        sc = new Scanner(System.in);
                        String bytesToWrite = sc.nextLine();

                        controller.writeFileContent(filePath, offset, bytesToWrite.getBytes());
                        break;

                    case MessageType.MONITOR_COMMAND:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        int intervalMilli = sc.nextInt();

                        controller.monitorFile(filePath, intervalMilli);
                        break;

                }
                while (true) {

                    byte[] recvBuf = new byte[FileServerThread.MAX_PACKET_BYTES];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

                    String response = controller.receive(packet);
                    if(response != null){
                        System.out.println(response);
                        break;
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
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
        boolean at_most_invocation = true;

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the client side");

        System.out.print("Specify server port: ");
        int port = sc.nextInt();

        System.out.print("Specify server IP: ");
        String address = sc.next();

        String result;

        CommandController controller = new CommandController(port, InetAddress.getByName(address));

        while(true) {
            try {
                if(at_most_invocation){
                    System.out.println("Invocation: AT MOST ONCE INVOCATION");
                }
                else{
                    System.out.println("Invocation: AT LEAST ONCE INVOCATION");
                }
                System.out.println("What do you want to do?");
                System.out.println("1. Read the content of the file.");
                System.out.println("2. Insert content into a file.");
                System.out.println("3. Monitor the client. ");
                System.out.println("4. Simulate duplicate request.");

                if(at_most_invocation){
                    System.out.println("5. Change to at least invocation.");

                }
                else{
                    System.out.println("5. Change to at most invocation.");
                }

                System.out.println("0. Exit");
                System.out.print("Input the choice:");
                int choice = sc.nextInt();

                if(choice == 0){
                        break;
                }

                String filePath;
                int offset;
                int numOfBytes;

                switch (choice) {
                    case MessageType.READ_COMMAND:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        offset = sc.nextInt();


                        System.out.print("Please input the number of bytes:");
                        sc = new Scanner(System.in);
                        numOfBytes = sc.nextInt();

                         result = controller.readFileContent(filePath, offset, numOfBytes);
                        System.out.println(result);
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

                         result = controller.writeFileContent(filePath, offset, bytesToWrite.getBytes(), at_most_invocation);
                        System.out.println(result);
                        break;

                    case MessageType.MONITOR_COMMAND:
                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the millisecond:");
                        sc = new Scanner(System.in);
                        int intervalMilli = sc.nextInt();

                        String monitor = controller.monitorFile(filePath, intervalMilli);
                        System.out.println(monitor);
                        break;

                    case MessageType.DUPLICATE_REQUEST:
                        System.out.println("Duplicate request for writing, it will submit request twice without increasing request number");

                        System.out.print("Please input the file path:");
                        sc = new Scanner(System.in);
                        filePath = sc.nextLine();

                        System.out.print("Please input the offset:");
                        sc = new Scanner(System.in);
                        offset = sc.nextInt();


                        System.out.print("Please input the bytes to write into the file:");
                        sc = new Scanner(System.in);
                        bytesToWrite = sc.nextLine();

                        controller.writeDuplicateFileContent(filePath, offset, bytesToWrite.getBytes(),at_most_invocation);
                        break;

                    case MessageType.CHANGE_INVOCATION:
                        if(at_most_invocation){
                            at_most_invocation = false;
                        }
                        else{
                            at_most_invocation = true;
                        }
                        break;

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
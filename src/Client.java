import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    int readernum;
    int writernum;
    int accessnum;
    public Client(int readernum,int writernum,int accessnum) {}
    public static void main(String[] args) {
        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(8011);

            // Looking up the registry for the remote object
            Hello stub = (Hello) registry.lookup("Hello");

            // Calling the remote method using the obtained object
            stub.printMsg();

            // System.out.println("Remote method invoked");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;

public class Server extends Thread {
   private static int rmiPort;
   private Registry registry;
   int totalconnection;
   public ArrayList<ImplExample.ReadingOperation> serverReadingOperations=new ArrayList<>();
   public ArrayList<ImplExample.WritingOperation> serverWritingOperations=new ArrayList<>();
   public Server(int port,int totalconnection) {
      rmiPort=port;
      this.totalconnection=totalconnection;
      this.start();
   }
   public void run() {
      connect();
   }
   void connect(){
      try {
         // Instantiating the implementation class
         ImplExample obj = new ImplExample();

         // Exporting the object of implementation class
         // (here we are exporting the remote object to the stub)
         Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, rmiPort);

         // Binding the remote object (stub) in the registry
         registry = LocateRegistry.createRegistry(rmiPort);

         registry.bind("Hello", stub);
         System.err.println("Server ready");

         while(obj.getTotalserved()!=totalconnection){
            System.out.print("");
         }
         serverWritingOperations=obj.serverWritingOperations;
         serverReadingOperations=obj.serverReadingOperations;
         writeresulttofile();
         System.out.println("finish");
      } catch (Exception e) {
         System.err.println("Server exception: " + e.toString());
         e.printStackTrace();
      }
   }
   private void writeresulttofile() {
      Collections.sort(serverWritingOperations,
              (o1, o2) -> Integer.valueOf(o1.sSeq).compareTo(o2.sSeq));
      Collections.sort(serverReadingOperations,
              (o1, o2) -> Integer.valueOf(o1.sSeq).compareTo(o2.sSeq));
      String fileName = "ServerOutput.txt";
      try {
         // Assume default encoding.
         FileWriter fileWriter =
                 new FileWriter(fileName);

         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
         bufferedWriter.write("sSeq oVal rID rNum\n");
         for (ImplExample.ReadingOperation x : serverReadingOperations) {
            bufferedWriter.write(x.sSeq + "    " + x.oVal +"    " + x.rID + "    " + x.rNum + "\n");
            //    System.out.println(x.sSeq + " " + x.oVal + " " + x.rID + " " + x.rNum + "\n");
         }
         bufferedWriter.write("Writers\n");
         bufferedWriter.write("sSeq oVal rID\n");
         for (ImplExample.WritingOperation x : serverWritingOperations) {
            bufferedWriter.write(x.sSeq +"    "+ x.oVal + "    " + x.wID + "\n");
         }
         bufferedWriter.close();
      } catch (IOException ex) {
         System.out.println(
                 "Error writing to file '"
                         + fileName + "'");
         // Or we could just do this:
         ex.printStackTrace();
      }
   }
   public static void main(String args[]) {
    int port=Integer.valueOf(args[0]);
    int tot=Integer.valueOf(args[1]);
    new Server(port,tot);
   }
}
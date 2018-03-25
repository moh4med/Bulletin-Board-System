import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.Thread.sleep;

public class Reader extends Thread{
   private  BufferedWriter bufferedWriter=null;
   int rmiport;
   int accessnum;
   int id;
   public Reader(int port,int num,int id) {
      rmiport=port;
      accessnum=num;
      this.id=id;
      writerFile();
      this.start();
   }
   void writerFile(){
      String fileName = "log"+"reader_"+id+".txt";
      try {
         // Assume default encoding.
         FileWriter fileWriter =
                 new FileWriter(fileName);

         bufferedWriter =
                 new BufferedWriter(fileWriter);
         bufferedWriter.write(" Client type: " +"Reader"+"\n"+
                 "Client Name: "+(id+1)+"\n"+
                 "rSeq sSeq " +"oVal\n");
      }
      catch(IOException ex) {
         System.out.println(
                 "Error writing to file '"
                         + fileName + "'");
         // Or we could just do this:
         // ex.printStackTrace();
      }
   }
   public void run() {
      connect();
   }
   void connect(){
      try {
         // Getting the registry
         Registry registry = LocateRegistry.getRegistry(rmiport);

         // Looking up the registry for the remote object
         Hello stub = (Hello) registry.lookup("Hello");

         // Calling the remote method using the obtained object
         for (int i = 0; i < accessnum; i++) {
            ImplExample.ReadingOperation x =stub.read(id);
            appendtofile(x);
            try {
               long sleeptime = (long) (Math.random() * 5000);
               sleep(sleeptime);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         /* LOG FILES */
         bufferedWriter.close();
      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   }
   void appendtofile(ImplExample.ReadingOperation a){
      try {
         bufferedWriter.append(a.rSeq+"    "+a.sSeq+"    "+a.oVal+"\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   void appendtofile(ImplExample.WritingOperation a){
      try {
         bufferedWriter.append(a.rSeq+"    "+a.sSeq+"\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   public static void main(String[] args) {
     int port=Integer.valueOf(args[0]);
     int accessnum=Integer.valueOf(args[1]);
     int id=Integer.valueOf(args[2]);
     new Reader(port,accessnum,id);
   }
}
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.Thread.sleep;

public class Writer extends Thread{
   private BufferedWriter bufferedWriter=null;
   int rmiport;
   int accessnum;
   int id;
   public Writer(int port,int num,int id) {
      rmiport=port;
      accessnum=num;
      this.id=id;
      writerFile();
      this.start();

   }
   void writerFile(){
      String fileName = "log"+"writer_"+""+id+".txt";
      try {
         // Assume default encoding.
         FileWriter fileWriter =
                 new FileWriter(fileName);

         bufferedWriter =
                 new BufferedWriter(fileWriter);
         bufferedWriter.write(" Client type: " +"Writer"+"\n"+
                 "Client Name: "+(id+1)+"\n"+
                 "rSeq sSeq " +"\n");
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
         stub.printMsg();
         // Calling the remote method using the obtained object
         for (int i = 0; i < accessnum; i++) {
               if (Math.random() < 0) {
                  ImplExample.ReadingOperation read = stub.read(id);
                  appendtofile(read);
               } else {
                  ImplExample.WritingOperation write = stub.write(id);
                  appendtofile(write);
               }
            try {
               long sleeptime = (long) (Math.random() * 5000);
               sleep(sleeptime);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         bufferedWriter.close();
         /* LOG FILES */

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
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
   void appendtofile(ImplExample.ReadingOperation a){
      try {
         bufferedWriter.append(a.rSeq+"    "+a.sSeq+"    "+a.oVal+"\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   public static void main(String[] args) {
      int port=Integer.valueOf(args[0]);
      int accessnum=Integer.valueOf(args[1]);
      int id=Integer.valueOf(args[2]);
      new Writer(port,accessnum,id);
   }
}
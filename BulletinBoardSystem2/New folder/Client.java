import java.net.*;
import java.io.*;

public class Client extends Thread {
    private  BufferedWriter bufferedWriter=null;
    int id;
    String ip;
    int port;
    int type;
    int accessNum;
    private Socket client;

    public Client(int type, int id, String ip, int port, int accessNum) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.type = type;
        this.accessNum = accessNum;
        writerFile();
        this.start();
    }
    void writerFile(){
        String fileName = "log"+type+""+id+".txt";
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            bufferedWriter =
                    new BufferedWriter(fileWriter);
            bufferedWriter.write(" Client type: " +(type==0?"Reader":"Writer")+"\n"+
                    "Client Name: "+(id+1)+"\n"+
                    "rSeq sSeq " +(type==0?"oVal\n":"\n"));
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
        for (int i = 0; i < accessNum; i++) {
            if (type == 0) {           //read
                readData();
            } else {                 //write
                if (Math.random() < 0) {
                    readData();
                } else {
                    writeData();
                }
            }
            try {
                long sleeptime = (long) (Math.random() * 10000);
                sleep(sleeptime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            client.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void connect() {

        try {
            //   System.out.println("Client:" + id + " ,  " + type + " Connecting to " + ip + " on port " + port);
            client = new Socket(ip, port);
            //System.out.println("Client:" + id + " ,  " + type + "  connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(id + "," + type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readData() {

        try {
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            out.writeUTF("0");
            String message=in.readUTF();
            String []a=message.split(",");
            int rseq=Integer.valueOf(a[0]);
            int sseq=Integer.valueOf(a[1]);
            int data=Integer.valueOf(a[2]);
            appendtofile(a);
            System.out.println("Client: " + id + " data in server " + data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void writeData() {
        try {
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            out.writeUTF("1");
            String message=in.readUTF();
            String []a=message.split(",");
            int rseq=Integer.valueOf(a[0]);
            int sseq=Integer.valueOf(a[1]);
            appendtofile(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void appendtofile(String []a){
        try {
        for (int i=0;i<a.length;i++){

                bufferedWriter.append(a[i]+"    ");
              //  System.out.println(a[i]);

        }
        bufferedWriter.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
            int type=Integer.valueOf(args[0]);
            int id=Integer.valueOf(args[1]);
            String ip=args[2];
            int port=Integer.valueOf(args[3]);
            int accessNum=Integer.valueOf(args[4]);
            Thread t = new Client(type, id, "ip", port, accessNum);
            t.start();
    }
}

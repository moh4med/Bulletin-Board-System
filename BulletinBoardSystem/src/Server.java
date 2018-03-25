import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Server extends Thread {
    class ReadingOperation {
        int sSeq;
        int oVal;
        int rID;
        int rNum;

        ReadingOperation(int sSeq, int oVal, int rID, int rNum) {
            this.sSeq = sSeq;
            this.oVal = oVal;
            this.rID = rID;
            this.rNum = rNum;
        }


    }

    class WritingOperation {
        int sSeq;
        int oVal;
        int wID;

        WritingOperation(int sSeq, int oVal, int wID) {
            this.sSeq = sSeq;
            this.oVal = oVal;
            this.wID = wID;
        }
    }

    int data = -1;
    int accessnum;
    int rseq = 0;
    int sseq = 0;
    int rnum = 0;
    int totalconnection;
    int stoppedconnection=0;
    ArrayList<ReadingOperation> serverReadingOperations;
    ArrayList<WritingOperation> serverWritingOperations;
    boolean writingExist = false;
    private ServerSocket serverSocket;

    public Server(int port, int accessnum, int totalconnection) throws IOException {
        this.accessnum = accessnum;
        this.totalconnection = totalconnection;
        serverSocket = new ServerSocket(port);
        serverReadingOperations = new ArrayList<>();
        serverWritingOperations = new ArrayList<>();
    }

    public synchronized void setVal(clientService x, int val) {
        incsseq(x);
        System.out.println("set data value from " + data + " to " + val);
        this.data = val;
        x.val = data;
    }

    public int getVal(clientService x) {
        incsseq(x);
        System.out.println("get data value " + data);
        x.val = data;
        return data;
    }

    public synchronized void addReadingOperation(clientService clientService) {
        serverReadingOperations.add(new ReadingOperation(clientService.cssq, clientService.val, clientService.id, clientService.crnum));
    }

    public synchronized void addwritingOperation(clientService clientService) {
        serverWritingOperations.add(new WritingOperation(clientService.cssq, clientService.val, clientService.id));
    }

    public void run() {
        for (int i = 0; i < totalconnection; i++) {
            try {
                /*System.out.println("Server:Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");*/
                Socket server = serverSocket.accept();
               /* System.out.println("Server:serving this client " +
                        serverSocket.getLocalPort() + "...");*/
                new clientService(server).start();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        while(stoppedconnection<totalconnection){
            System.out.print("");
        }
        writeresulttofile();
        System.out.println("finish");
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
            for (ReadingOperation x : serverReadingOperations) {
                bufferedWriter.write(x.sSeq + "    " + x.oVal +"    " + x.rID + "    " + x.rNum + "\n");
            //    System.out.println(x.sSeq + " " + x.oVal + " " + x.rID + " " + x.rNum + "\n");
            }
            bufferedWriter.write("Writers\n");
            bufferedWriter.write("sSeq oVal rID\n");
            for (WritingOperation x : serverWritingOperations) {
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

    public synchronized void incrseq(clientService x) {
        rseq++;
        x.crseq = rseq;
    }

    public synchronized void incsseq(clientService x) {
        sseq++;
        x.cssq = sseq;
    }


    class clientService extends Thread {
        Socket server;
        int crseq;
        int cssq;
        int crnum;
        int id;
        int type;
        int val;
        boolean isreading = false;

        clientService(Socket server) {
            this.server = server;
        }

        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                DataInputStream in = new DataInputStream(server.getInputStream());
                String clientmessage = in.readUTF();
                int id = Integer.valueOf(clientmessage.substring(0, clientmessage.indexOf(',')));
                int type = Integer.valueOf(clientmessage.substring(clientmessage.indexOf(',') + 1));
                this.id = id;
                this.type = type;
                for (int i = 0; i < accessnum; i++) {
                    clientmessage = in.readUTF();
                    System.out.println("Server:getting command from " + (type == 1 ? "writer: " : "reader: ") + id + " " + "and he needs to " + (type == 1 ? "write: " : "read: ") + " ,data =" + data);
                    int messagetype = Integer.valueOf(clientmessage);
                    incrseq(this);
                    if (messagetype == 0) {     //read
                        if (!isreading) {
                            rnum++;
                            crnum=rnum;
                            isreading=true;
                        }
                        getVal(this);
                        addReadingOperation(this);
                        out.writeUTF(crseq + "," + cssq + "," + val);
                    } else {                  //write
                        if (isreading) {
                            rnum--;
                            crnum=rnum;
                            isreading = false;
                        }
                        setVal(this, id);
                        addwritingOperation(this);
                        out.writeUTF(crseq + "," + cssq);
                    }
                }
                if (isreading) {
                    rnum--;
                }
                server.close();
                stoppedconnection++;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public static void main(String[] args) {
        int port = 16740;
        try {
            Thread t = new Server(port, 1, 2);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
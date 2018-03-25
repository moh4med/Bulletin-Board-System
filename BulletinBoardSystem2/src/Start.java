import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Start {
    String serverIP;
    int serverPort;
    int readernumbers;
    ArrayList<String> readers;
    int writernumbers;
    ArrayList<String> writers;
    int accessNum;
    int rmiPort;


    public static void remoteconnect() {
        String host = "ssh.journaldev.com";
        String user = "sshuser";
        String password = "sshpwd";
        String command1 = "java Client";
        try {

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connecttoremote(String hostname, int type, int id, String ip, int port, int accessNum) {
        try {
            String command = "ssh " + hostname + " java Client " + type + " " + id + " " + ip + " " + port + " " + accessNum;
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", command);
            builder.start();
            // Runtime.getRuntime().exec(command);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void runprocess() {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "dir");
       /* builder.redirectErrorStream(true);
        Process p = null;
        try {
            p = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line="";
        while (true) {
            try {
                line = r.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) { break; }
            System.out.println(line);
        }*/
    }

    public static void main(String[] args) {
        Start main = new Start();
        main.initconfig();
        Server mserver = null;
        mserver = new Server(main.rmiPort,(main.readernumbers+main.writernumbers)*main.accessNum);
        for (int i = 0; i < main.readernumbers; i++) {
            //     main.connecttoremote(main.readers.get(i),0,i,main.serverIP,main.serverPort,main.accessNum);
            Reader mclient = new Reader(main.rmiPort, main.accessNum,i);
        }
        for (int i = 0; i < main.writernumbers; i++) {
            //       main.connecttoremote(main.writers.get(i),1,i,main.serverIP,main.serverPort,main.accessNum);
            Writer mclient = new Writer(main.rmiPort, main.accessNum,i);
        }

    }

    void initconfig() {
        // The name of the file to open.
        String fileName = "system.properties";
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            line = bufferedReader.readLine();           //reading ip
            line = line.substring(line.indexOf('=') + 1);
            serverIP = line;


            line = bufferedReader.readLine();           //reading port
            line = line.substring(line.indexOf('=') + 1);
            serverPort = Integer.valueOf(line);

            line = bufferedReader.readLine();           //reading rmi port
            line = line.substring(line.indexOf('=') + 1);
            rmiPort = Integer.valueOf(line);

            line = bufferedReader.readLine();           //reading reader_numbers
            line = line.substring(line.indexOf('=') + 1);
            readernumbers = Integer.valueOf(line);
            readers = new ArrayList<>();
            for (int i = 0; i < readernumbers; i++) {              //reading readers data

                line = bufferedReader.readLine();
                line = line.substring(line.indexOf('=') + 1);
                readers.add(line);
            }

            line = bufferedReader.readLine();           //reading writer_numbers
            line = line.substring(line.indexOf('=') + 1);
            writernumbers = Integer.valueOf(line);
            writers = new ArrayList<>();
            for (int i = 0; i < writernumbers; i++) {              //reading writers data

                line = bufferedReader.readLine();
                line = line.substring(line.indexOf('=') + 1);
                writers.add(line);
            }

            line = bufferedReader.readLine();           //reading number of access
            line = line.substring(line.indexOf('=') + 1);
            accessNum = Integer.valueOf(line);

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }

}

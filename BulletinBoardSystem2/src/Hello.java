import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Hello extends Remote
{
    class ReadingOperation implements Serializable{
        int sSeq;
        int rSeq;
        int oVal;
        int rID;
        int rNum;

        ReadingOperation(int sSeq,int rSeq, int oVal, int rID, int rNum) {
            this.sSeq = sSeq;
            this.rSeq=rSeq;
            this.oVal = oVal;
            this.rID = rID;
            this.rNum = rNum;
        }


    }

    class WritingOperation implements Serializable{
        int sSeq;
        int rSeq;
        int oVal;
        int wID;

        WritingOperation(int sSeq, int rSeq,int oVal, int wID) {
            this.sSeq = sSeq;
            this.rSeq = rSeq;
            this.oVal = oVal;
            this.wID = wID;
        }
    }
    void printMsg() throws RemoteException;
    ImplExample.WritingOperation write(int id) throws RemoteException;
    ImplExample.ReadingOperation read(int id) throws RemoteException;
} 
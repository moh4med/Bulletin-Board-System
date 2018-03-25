import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class ImplExample implements Hello {

    private int rseq;
    private int sseq;
    private int oval=-1;
    private int rnum;
    private int totalserved;
    private Lock readinglock = new ReentrantLock();
    private Lock writinglock = new ReentrantLock();
    public ArrayList<ReadingOperation> serverReadingOperations=new ArrayList<>();
    public ArrayList<WritingOperation> serverWritingOperations=new ArrayList<>();

    public int getTotalserved() {
        return totalserved;
    }
    private synchronized void incTotalserved(){
        totalserved++;
    }
    @Override
    public void printMsg() throws RemoteException {
        System.out.println("This is an example RMI program");
    }

    public synchronized void addReadingOperation(ReadingOperation readingOperation) {
        serverReadingOperations.add(readingOperation);
    }
    public synchronized void addwritingOperation(WritingOperation writingOperation) {
        serverWritingOperations.add(writingOperation);
    }
    public synchronized int incrseq(int id) {
        rseq++;
        //System.out.println("client: " + id + " rseq: "+rseq);
        return rseq;
    }

    public synchronized int incsseq(int id) {
        sseq++;
        //System.out.println("client: " + id + " sseq: "+sseq);
        return sseq;
    }
    public synchronized int incrnum(int id,int add) {
        rnum+=add;
        //System.out.println("client: " + id + " runm: "+runm);
        return rnum;
    }
    public WritingOperation write(int id) throws RemoteException {
        //Lock
        int lrseq;
        int lsseq;
        lrseq = incrseq(id);
        System.out.println("Writer: " + id + " trying to write");

        synchronized (this) {
            System.out.println("Writer: " + id + " writing ");
            //YOUR CODE FOR CLIENT WRITER
            long sleeptime = (long) (Math.random() * 10000);
            for (int i = 0; i < 100; i += 25) {
                System.out.println("Writer: " + id + " writing : " + i + " %");
                try {
                    sleep(sleeptime / 4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            oval = id;
            lsseq = incsseq(id);
        }
        WritingOperation writingOperation = new WritingOperation(lsseq,lrseq, oval, id);
        addwritingOperation(writingOperation);
        System.out.println("Writer: " + id + " finished writing rseq= " + lrseq + " sseq= " + lsseq);
        incTotalserved();
        return writingOperation;
    }


    public ReadingOperation read(int id) throws RemoteException {

        //Lock
        int lrseq;
        int lsseq;
        int lrnum;
        System.out.println("Reader: " + id + " trying to read");
        lrseq=incrseq(id);
        lrnum=incrnum(id,1);
        long sleeptime = (long) (Math.random() * 5000);
        for (int i = 0; i < 100; i += 25) {
            System.out.println("Reader: " + id + " reading : " + i + " %");
            try {
                sleep(sleeptime / 4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lsseq=incsseq(id);
        System.out.println("Reader: " + id + " finished reading rseq= " + lrseq + " sseq= " + lsseq);
        ReadingOperation readingOperation = new ReadingOperation(lsseq,lrseq, oval, id, lrnum);
        addReadingOperation(readingOperation);
        incrnum(id,-1);
        incTotalserved();
        return readingOperation;
    }


} 
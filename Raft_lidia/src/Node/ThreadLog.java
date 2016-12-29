
package Node;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadLog extends Thread{
    
    private final ConcurrentLinkedQueue<Pair> queueLOG;    
    private final Log log;
    
    public ThreadLog (ConcurrentLinkedQueue<Pair> queueLog, Log log){
        this.queueLOG = queueLog;
        this.log = log;
    }
    
    public void run() {
        
        while(true){
            
            Pair tmp = this.queueLOG.poll();
            
            if(tmp == null)
                continue;
            
            else{
                int[] info = new int[2];
                try {
                    info = this.log.getInfoLastEntry();
                } catch (IOException ex) {
                    Logger.getLogger(ThreadLog.class.getName()).log(Level.SEVERE, null, ex);
                }
                int prevLogIndex = info[0];
                int prevLogTerm = info[1];
                if( (prevLogIndex==tmp.getPrevLogIndex()) && (prevLogTerm==tmp.getPrevLogTerm())){
                    System.out.println("preparing to write in log");
                    String message=tmp.getMessage();
                    String parts[]=message.split(":");
                    String command=parts[1];
                    int term=tmp.getTerm();
                    try {
                        this.log.writeLog(term,command);
                    } catch (IOException ex) {
                        Logger.getLogger(ThreadLog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                    
                    
            }
        }
    }
}

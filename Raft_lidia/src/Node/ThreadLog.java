
package Node;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadLog extends Thread{
    
    private final ConcurrentLinkedQueue<Pair> queueLOG;    
    private final Log log;
    public final ComunicationUDP comModule;
    
    public ThreadLog (ConcurrentLinkedQueue<Pair> queueLog, Log log, ComunicationUDP comModule){
        this.queueLOG = queueLog;
        this.log = log;
        this.comModule = comModule;
    }
    
    public void run() {
        
        while(true){
            
            Pair tmp = this.queueLOG.poll();
            
            if(tmp == null)
                continue;
            
            else{
                
                int[] info = new int[2];
                
                try {
                    //get LastLogEntry 
                    info = this.log.getLogLastEntry();
                    int prevLogIndex = info[0];
                    int prevLogTerm = info[1];
                    
                    //get MsgContents
                    //Message Formate-> AppendEntry:term1:command1:term2:command2:term3:command3:....
                    String message = tmp.getMessage(); 
                    String newEntries[] = message.split(":"); 
                    int nNewEntries = (newEntries.length-1)/2; //newEntries.length-1 tem de dar sempre um n.o par
                    int[] newEntryTerms = new int[nNewEntries];
                    int[] newEntryCommands = new int[nNewEntries];
                    
                    int i, j=0; //começa em 1 para ignorar o newEntris[0]=AppendEntry
                    for(i=1; i<newEntries.length-1; i+=2){
                        newEntryTerms[j] = Integer.parseInt(newEntries[i]);
                        newEntryCommands[j] = Integer.parseInt(newEntries[i+1]);
                        j++;
                    }
                    //Message PrevLogIndex and msgPrevLogTerm
                    int MsgPrevLogIndex = tmp.getPrevLogIndex();
                    int msgPrevLogTerm = tmp.getPrevLogTerm();
                    int msgTerm = tmp.getTerm();
                    
                    //test conditions to write on the log
                    if (msgTerm < States.term){
                        //reply false - ver no FOLLOWER  //TO DO !!!!
                    }
                    
                    //
                    else if( this.log.lookForTerm(MsgPrevLogIndex) != msgPrevLogTerm){
                        //return false   //TO DO!!!!
                    }
                    
                    //test if a new entry conflicts with an existing one. Conflict = same index but different term
                    else if( MsgPrevLogIndex < prevLogIndex ){
                        int termWithConflict = this.log.checkForConflicts(newEntryTerms, MsgPrevLogIndex); // =0 se n houver conflitos
                        if(termWithConflict != 0)
                            //if yes, delete the existing entry and all the ones that follow it
                            this.log.deleteEntries(termWithConflict);
                    }
                    //if no problem, write new entries on the log                   
                    else;
                        //this.log.writeLog(newEntryTerms,newEntryCommands);

                    
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(ThreadLog.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
            }
        }
    }
}

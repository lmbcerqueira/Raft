
package Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
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
            
            boolean checkIsOk = true;
            
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
                    String[] newEntryCommands = new String[nNewEntries];
                    
                    int i, j=0; //começa em 1 para ignorar o newEntris[0]=AppendEntry
                    for(i=1; i<newEntries.length-1; i+=2){
                        newEntryTerms[j] = Integer.parseInt(newEntries[i]);
                        newEntryCommands[j] = newEntries[i+1];
                        j++;
                    }
                    
                    //Message PrevLogIndex and msgPrevLogTerm
                    int leadPrevLogIndex = tmp.getPrevLogIndex();
                    int leadPrevLogTerm = tmp.getPrevLogTerm();
                    int leadTerm = tmp.getTerm();  
                    
                    //test conditions before writting on the log
                    
                    if (leadTerm < States.term){
                        //reply false 
                        InetAddress inet = tmp.getInet();
                        String msgToSend = "LEADNOTUPD@" + Integer.toString(States.term);
                        this.comModule.sendMessage(msgToSend, inet); //msg vai ser processada pelo lider juntamente com as msg "normais"
                        System.out.println("[THREADLOG]: TERMO MENOR DO LIDER");
                        checkIsOk = false;
                    }
                    
                    //se o ficheiro não estiver vazio
                    BufferedReader br = new BufferedReader(new FileReader(this.log.file));     
                    if (br.readLine() != null) {
                        //test if a new entry conflicts with an existing one. Conflict = same index but different term
                        int termWithConflict = this.log.checkForConflicts(newEntryTerms, leadPrevLogIndex); // verificar a partir do inicio do log, A MELHORAR
                        boolean alreadyERROR=false;
                        int logTerm=this.log.lookForTerm(leadPrevLogIndex);
                        System.out.println(" leadPrevLogIndex="+leadPrevLogIndex+" leadPrevLogTerm="+leadPrevLogTerm+" log Term="+logTerm);
                        if(logTerm != leadPrevLogTerm){
                            
                            //reply false
                            InetAddress inet = tmp.getInet();
                            String msgToSend = "ERROR_LOG@" + Integer.toString(States.term);
                            System.out.println("[THREADLOG]: Log Matching Property failed, MSG to send: "+msgToSend);
                            
                            
                            this.comModule.sendMessage(msgToSend, inet);  
                            alreadyERROR=true;
                            checkIsOk = false;
                        }

                        if(termWithConflict != 0 && alreadyERROR==false){ //assim so manda uma mensagem de erro 
                            //if yes, delete the existing entry and all the ones that follow it
                            //this.log.deleteEntries(termWithConflict); TO DO
                            InetAddress inet = tmp.getInet();
                            String msgToSend = "ERROR_LOG@" + Integer.toString(States.term);
                            this.comModule.sendMessage(msgToSend, inet);  
                            System.out.println("[THREADLOG]: Conflicts detectedMSG to send: "+msgToSend);
                            checkIsOk = false;
                        }                        
                    }
                    
                    //if no problem, write new entries on the log                   
                    if(checkIsOk){
                        int lastIndexWritten = this.log.writeLog(newEntryTerms,newEntryCommands);
                        System.out.println("[THREADLOG]: writing on the log");
                        
                        //send aknowledge ao líder
                        String acknowledge = "ACK:" + Integer.toString(lastIndexWritten) + "@" + Integer.toString(States.term);
                        System.out.println("[ThreadLog] message:  "+acknowledge);
                        this.comModule.sendMessage(acknowledge, tmp.getInet());
                    }

                    //if leader commit > commitIndex , set commitIndex = min(kleaderCommit, index of last new entry
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(ThreadLog.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
            }
        }
    }
}

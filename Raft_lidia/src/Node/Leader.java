package Node;

import static Node.States.writtenIndex;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Leader {
    
    public final ComunicationUDP comModule;
    private final long timeout;
    private final DataProcessing dataProcessing;
    private final ConcurrentLinkedQueue<Pair> queue;
    private final Log log;
    public int prevLogIndex;
    public int prevLogTerm;
    
    public Leader(ConcurrentLinkedQueue<Pair> queue, Log log) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getHeartBeat();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.queue);
        this.log = log;
        
    }
    
    private final int getHeartBeat(){
                         
        int min_value = 1400;
        int max_value = 1450;
        
        return (min_value + (int)(Math.random() * ((max_value - min_value) + 1)))/100;
    }
    
    public int cycle(int term) throws IOException {
        
        int[] info = new int[2];
        info = this.log.getLogLastEntry();
        this.prevLogIndex = info[0]+1;
        this.prevLogTerm = info[1]+1;
        
        //AO ENTRAR NO CICLO DO LEADER ATUALIZA OS INDEX DE TODOS, ASSUME QUE A MAIORIA VOTOU NELE, SABENDO
        //OS INDEX E TERM ANTIGOS, POR ISSO TB TEM
        if( this.prevLogIndex ==1)
            Arrays.fill(States.writtenIndex, 0);
        else
            Arrays.fill(States.writtenIndex, this.prevLogIndex);
        // creating timer task and schedule
        Timer timer = new Timer();
        timer.schedule(new sendHeartBeatTimer(term, this.log, this.prevLogIndex, this.prevLogTerm),100, 10000);  //heartbeatfreq >>>>>>> timeoutsfollowers
       
        int newTerm = checkIncomingLeaderMsg(term); //retorna qd tiver de mudar para FOLLOWER
        
        System.out.println("LEADER : Vou sair do Leadercycle");
        timer.cancel();
        return newTerm;
    }
    
    
    public int checkIncomingLeaderMsg(int term) throws IOException{
        
        int receivedTerm;
        String pair;
        String message;
        
        while(true){
            //atualiza os commits
                  
            checkCommitedIndex();
            
            
            Pair tmp = this.queue.poll();
            
            if (tmp == null)
                continue;
            else{
                receivedTerm = tmp.getTerm();
                
                if(receivedTerm > term)
                    break;   
                
                InetAddress address = tmp.getInet();
                message = tmp.getMessage();
                
                if(message.contains("ERROR_LOG")){                    
                    //decrementar a variavel writtenIndex desse follower
                    String sourceMsgIP = tmp.getInet().getHostAddress();
                    int id = Integer.parseInt(sourceMsgIP.substring(sourceMsgIP.length() - 1));
                    
                    //////////////////////////////////////////
                    
                    /*
                    E PARA TIRAR NO DA LIDIA ISTO
                    */
                    if(id==8)
                        id=1;
                    else if(id==9)
                        id=2;
                    else if(id==0)
                        id=3;
                    else if(id==7)
                        id=4;
                    else if(id==2)
                        id=5;
                    ///TEM HAVER COM O MEU INDEREÇO IP, O PRIMEIRO E 78
                    String msgLog;
                    System.out.println("[LEADER] received error_log." );
                    ////////////////////////////////
                    if(States.writtenIndex[id-1]==0){
                        String logForUPdate = this.log.getLogContents(0);
                        msgLog = "RefreshLog@" +  Integer.toString(term) + "@" +logForUPdate ;
                        this.comModule.sendMessage(msgLog, address);
                    }
                    else{
                        States.writtenIndex[id-1] = States.writtenIndex[id-1] -1;

                        //mandar entry do index anterior anterior
                        String log = this.log.getLogEntry(States.writtenIndex[id-1]);

                        msgLog = "CHECK_PREVIOSENTRY" + ":" + log + "@" +Integer.toString(term);
                        this.comModule.sendMessage(msgLog, address);
                        
                    }
                    System.out.println(" message sent: " + msgLog + "   INDEX VISTO DO LOG->"+States.writtenIndex[id-1]);
                
                }
                else if (message.contains("ACK")){
                    //atualizar a variavel writtenIndex
                    String contents[] = message.split(":");
                    int lastIndexWritten = Integer.parseInt(contents[1]);
                    String sourceMsgIP = tmp.getInet().getHostAddress();
                    int id = Integer.parseInt(sourceMsgIP.substring(sourceMsgIP.length() - 1));
                    
                    if(lastIndexWritten<this.prevLogIndex){
                        String logForUPdate = this.log.getLogContents(lastIndexWritten+1);
                        String msgLog = "RefreshLog@" +  Integer.toString(term) + "@" +logForUPdate ;
                        this.comModule.sendMessage(msgLog, address);
                    }
                    //////////////////////////////////////////
                    
                    /*
                    E PARA TIRAR NO DA LIDIA ISTO
                    */
                    if(id==8)
                        id=1;
                    else if(id==9)
                        id=2;
                    else if(id==0)
                        id=3;
                    else if(id==7)
                        id=4;
                    else if(id==2)
                        id=5;
                    ///TEM HAVER COM O MEU INDEREÇO IP, O PRIMEIRO E 78
                    
                    System.out.println("[LEADER] IN ACK ID: "+id +".....lastIndexWritten: "+ lastIndexWritten);
                    States.writtenIndex[id-1] = lastIndexWritten; //o valor do nó 1 fica na pos 0, do nó 2 na pos 1, ...
                    System.out.println("[LEADER SUMARIO] ACK recvd writtenIndex: " + States.writtenIndex[0] + ":" + 
                                                                            + States.writtenIndex[1] + ":" +   
                                                                            + States.writtenIndex[2] + ":" +
                                                                             + States.writtenIndex[3] + ":" +
                                                                              + States.writtenIndex[4]);    
                }

                else if (message.contains("COMMAND")){

                    int[] info = new int[2];
                    info = this.log.getLogLastEntry();
                    this.prevLogIndex = info[0];
                    this.prevLogTerm = info[1];
        
                    String new_parts[] = message.split(":");
                    String command = new_parts[1];
                    System.out.println("[LEADER] : Received command: " + command);
                    int[] termLog = new int[1]; 
                    termLog[0] = term;
                    String[] commandLog = new String[1];
                    commandLog[0] = command;
                    this.log.writeLog(termLog, commandLog);
                    // SEE COMMITED INDEX
                    System.out.println("[LEADER] COMMITED INDEX="+States.commitIndex);
                    //send APPENDENTRY TO ALL FOLLOWERS                
                    String entry = "AppendEntry" + ":" + Integer.toString(term) //este term aqui parece useless mas faz sentido qd se manda varios comandos de termos dif
                            + ":" + command + "@" + Integer.toString(term) + "@" + Integer.toString(this.prevLogTerm) + "@" + Integer.toString(this.prevLogIndex) ;
                    //System.out.println("Sent AppendEntry with prevLogIndex = " + this.prevLogIndex);
                    System.out.println(" [LEADER] : Sent = Entry:term:command@term@prevTerm@prevIndex " + entry);
                    this.comModule.sendMessageBroadcast(entry);
                } 
                
                else if(message.contains("LEDNOTUPD")){
                    break;
                }
            }  
        }
        return receivedTerm;
    }   

    private void checkCommitedIndex() {
        int [] votes= new int[States.nNodes];
        Arrays.fill(votes , 0);
        
        for(int i=0;i<States.nNodes;i++){
               for(int j=0;j<States.nNodes;j++){
                   if(States.writtenIndex[i]>=States.writtenIndex[j]){
                       votes[i]++;
                   }
                }
        }
        
        for(int k=0;k<States.nNodes;k++){
           // System.out.println("written index votes for "+k+"="+votes[k]);
            if(votes[k]>(States.nNodes/2)){
                if(votes[k]>States.commitIndex)
                    States.commitIndex=States.writtenIndex[k];
            }
                
        }
        
       // System.out.println("[LEADER] COMMITED INDEX="+States.commitIndex);
    }
    
    //TIMER
    class sendHeartBeatTimer extends TimerTask  {
        
        private int term;
        private Log log;
        private int prevLogIndex;
        private int prevLogTerm;
        
        sendHeartBeatTimer (int term, Log log,int prevLogIndex,int prevLogTerm){
            this.term = term;
            this.log = log;
            this.prevLogIndex=prevLogIndex;
            this.prevLogTerm=prevLogTerm;
            
        }

        @Override
        public void run() {
            
            
            
            String heartBeatString = "HELLO" + "@" + Integer.toString(this.term) + "@" + Integer.toString(this.prevLogTerm) + "@" + Integer.toString(this.prevLogIndex) ;
            //System.out.println("LEADER: send" + heartBeatString);
            try {
                Leader.this.comModule.sendMessageBroadcast(heartBeatString);
            } catch (IOException ex) {
                Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

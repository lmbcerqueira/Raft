package Node;

import java.io.IOException;
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
    
    public Leader(ConcurrentLinkedQueue<Pair> queue, Log log) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getHeartBeat();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.queue);
        this.log = log;
    }
    
    private final int getHeartBeat(){
                         
        int min_value = 1000;
        int max_value = 1100;
        
        return (min_value + (int)(Math.random() * ((max_value - min_value) + 1)))/100;
    }
    
    public int cycle(int term) throws IOException {
        
        int[] info = new int[2];
        info = this.log.getLogLastEntry();
        int prevLogIndex = info[0];
        int prevLogTerm = info[1];
        
        // creating timer task and schedule
        Timer timer = new Timer();
        timer.schedule(new sendHeartBeatTimer(term, this.log,prevLogIndex,prevLogTerm),100, 10000);  //heartbeatfreq >>>>>>> timeoutsfollowers
       
        int newTerm = checkIncomingLeaderMsg(term, prevLogIndex,prevLogTerm); //retorna qd tiver de mudar para FOLLOWER
        
        System.out.println("LEADER : Vou sair do Leadercycle");
        timer.cancel();
        return newTerm;
    }
    
    
    public int checkIncomingLeaderMsg(int term, int prevLogIndex, int prevLogTerm) throws IOException{
        
        int receivedTerm;
        String pair;
        String message;
        
        while(true){
            if (this.queue.isEmpty())
                continue;
            else{
                receivedTerm = this.queue.peek().getTerm();
                
                if(receivedTerm > term)
                    break;   
                
                message = this.queue.poll().getMessage();
                
                if (message.contains("COMMAND")){
                    
                    String new_parts[] = message.split(":");
                    String command = new_parts[1];
                    System.out.println("[LEADER] : Received command: " + command);
                    this.log.writeLog(term, command);
                    
                    //send APPENDENTRY TO ALL FOLLOWERS
                    String entry = "AppendEntry" + ":" + Integer.toString(term) //este term aqui parece useless mas faz sentido qd se manda varios comandos de termos dif
                            + ":" + command + "@" + Integer.toString(term) + "@" + Integer.toString(prevLogTerm) + "@" + Integer.toString(prevLogIndex) ;
                    this.comModule.sendMessageBroadcast(entry);
                }   
                else if(message.contains("LEDNOTUPD")){
                    break;
                }
            }  
        }
        return receivedTerm;
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
            System.out.println("LEADER: send" + heartBeatString);
            try {
                Leader.this.comModule.sendMessageBroadcast(heartBeatString);
            } catch (IOException ex) {
                Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

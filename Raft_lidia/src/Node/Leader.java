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
        
        // creating timer task and schedule
        Timer timer = new Timer();
        timer.schedule(new sendHeartBeatTimer(term),100, 10000);  //heartbeatfreq >>>>>>> timeoutsfollowers
       
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
                }                 
            }  
        }
        return receivedTerm;
    }   
    
    //TIMER
    class sendHeartBeatTimer extends TimerTask  {
        
        private int term;
        
        sendHeartBeatTimer (int term){
            this.term = term;
        }

        @Override
        public void run() {
            
            String heartBeatString = "HELLO" + "@" + Integer.toString(this.term);
            System.out.println("LEADER: send" + heartBeatString);
            try {
                Leader.this.comModule.sendMessageBroadcast(heartBeatString);
            } catch (IOException ex) {
                Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

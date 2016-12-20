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
    
    public Leader(ConcurrentLinkedQueue<Pair> queue) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getHeartBeat();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.timeout, this.queue);
        
    }
    
    private final int getHeartBeat(){
                         
        int min_value = 1000;
        int max_value = 1100;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
    }
    
    public void sendHeartBeat(int term) throws IOException{
        
        String heartBeatString;
        heartBeatString = "HELLO@"+Integer.toString(term);
        
        this.comModule.sendMessageBroadcast(heartBeatString); //com que frequência?????
    }

    public void cycle(int term) {
        
        // creating timer task and schedule
        Timer timer = new Timer();
        timer.schedule(new sendHeartBeatTimer(term),100, 10000);  //heartbeatfreq >>>>>>> timeoutsfollowers
       
        this.dataProcessing.checkIncomingLeaderMsg(term); //retorna qd tiver de mudar para FOLLOWER
        
        System.out.println("LEADER : Vou sair do Leadercycle");
        timer.cancel();
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

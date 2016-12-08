
package Node;

import java.io.IOException;


public class Follower extends Thread {
    
    public final ComunicationUDP comModule;
    private final long timeout;
    private int term;

    public Follower() throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout=this.getTimeout();
        this.term =0;
    }
    
    public long getTimeout(){
                 
        int min_value = 1500;
        int max_value = 3000;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
       
    }
    
    private void answerElection() throws IOException{
        this.term++;
        String message="ACCEPTED";
        this.comModule.sendData(message);
    }
   
    
}

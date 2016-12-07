
package Node;

import java.io.IOException;


public class Follower  {
    
    public final ComunicationUDP comModule;
    private final long timeout;

    public Follower() throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout=this.getTimeout();
        
  
    }
    
    public long getTimeout(){
                 
        int min_value = 1500;
        int max_value = 3000;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
       
    }
   
    
}

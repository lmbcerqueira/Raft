
package Node;

import java.io.IOException;


public class Follower  {
    
    private final ComunicationUDP comModule;

    public Follower() throws IOException {
        this.comModule = new ComunicationUDP();
  
    }
    
    public int getTimeout(){
                 
        int min_value = 1500;
        int max_value = 3000;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
       
    }
    
    public String receiver(int timeout) throws IOException{
        
        String receive;
        
        while(true){
            
            receive = this.comModule.receiveData(timeout);
           
            if(!receive.contains("HELLO"))
                return  "ERROR";

        }

    }
    
}

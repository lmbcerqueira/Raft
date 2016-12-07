
package Node;

import java.io.IOException;

public class Candidate {
    
    private final ComunicationUDP comModule;
     private final long timeout;
     
    public Candidate() throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout=this.getTimeout();
    }
    
    public long getTimeout(){
        
        int min_value = 1000; //?????????
        int max_value = 1100; //????????????
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
    }
      
    public void startElection() throws IOException{
        
        String electionString;
        electionString = "ELECTION";

        this.comModule.sendData(electionString); 
    }
    
    public String resultsElection() throws IOException{
        
        String receivedElection;
        
        return "ola";
        
    }
}

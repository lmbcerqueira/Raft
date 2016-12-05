
package Node;

import java.io.IOException;

public class Candidate {
    
    private final ComunicationUDP comModule;

    public Candidate() throws IOException {
        this.comModule = new ComunicationUDP();
    }
    
    public int getTimeout(){
        
        int min_value = 1000; //?????????
        int max_value = 1100; //????????????
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
    }
      
    public void startElection() throws IOException{
        
        String electionString;
        electionString = "ELECTION";

        this.comModule.sendData(electionString); 
    }
    
    public String resultsElection(int timeout) throws IOException{
        
        String receivedElection;
        
        //while(true){
            
            receivedElection = this.comModule.receiveData(timeout);
            System.out.println(receivedElection);
            
            return receivedElection;
            
//            if(receivedElection.contains("ACCEPTED"))
//                return "ACCEPTED";
//            else if(receivedElection.contains("becomeFOLLOWER"))
//                return "FOLLOWER";
//            else
            
        //}
    }
}

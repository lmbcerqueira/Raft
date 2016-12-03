/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

import java.io.IOException;

/**
 *
 * @author joaqu
 */
public class Candidate {
    private final ComunicationUDP comModule;

    public Candidate() throws IOException {
        this.comModule = new ComunicationUDP();
      
    }
    
  
    public void startElection() throws IOException{
        String electionString;
        electionString="ELECTION";
        this.comModule.sendData(electionString);
    }
    
    public String resultsElection() throws IOException{
        String receivedElection;
        while(true){
            receivedElection=this.comModule.receiveData();
            if(receivedElection.contains("ACCEPTED"))
                return "ACCEPTED";
            else if(receivedElection.contains("becomeFOLLOWER"))
                return "FOLLOWER";
            else
                continue;
        }
    }
}

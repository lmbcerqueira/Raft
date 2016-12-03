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



public class States {
     
    public void States() throws IOException{
       Follower follower = new Follower();
       Candidate candidate = new Candidate();
       Leader leader = new Leader();
       
       String received,electionsResult;
       received=follower.receiver();
       
       while(true){
        if(received.contains("ERROR"))
            candidate.startElection();

        electionsResult=candidate.resultsElection();
        if(electionsResult.contains("ACCEPTED"))
            leader.sendHeartBeat();
        else if(electionsResult.contains("becomeFOLLOWER"))
            received=follower.receiver();
        electionsResult=null;     
       }      

    }
}

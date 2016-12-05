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
       FlowStateMachine flowSM= new FlowStateMachine();
       
       
       
       while(true){
        if(flowSM.getStateMachine()==flowSM.follower){
            String received;
            received=follower.receiver();
            
            if(received.contains("ERROR")){
                flowSM.fsm=flowSM.candidate;
            }
        }
        
        if(flowSM.getStateMachine()==flowSM.candidate){
            candidate.startElection();
            String electionsResult = candidate.resultsElection();
            
            if(electionsResult.contains("ACCEPTED")){
                flowSM.fsm=flowSM.leader;
                
            }
            else if(electionsResult.contains("becomeFOLLOWER")){
                flowSM.fsm=flowSM.follower;
            }
                       
        }
        if(flowSM.getStateMachine()==flowSM.leader){
            Thread t= new Thread();
            t.start();
            
            
        }
        
      
    }      
       
       
    }

 
}

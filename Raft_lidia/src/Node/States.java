
package Node;

import java.io.IOException;

public class States {
     
    public void States() throws IOException{
        
       Follower follower = new Follower();
       Candidate candidate = new Candidate();
       Leader leader = new Leader();
         
       FlowStateMachine flowSM = new FlowStateMachine();
       flowSM.setFollower();
       
       int timeoutFollower = follower.getTimeout();  
       int heartBeatPeriod = leader.getHeartBeat();
       int timeoutCandidate = candidate.getTimeout();
       
       //Iniciar thread receive
       ThreadReceive receiverThread = new ThreadReceive();
       Thread receiver = new Thread(receiverThread);
       receiver.start();
       
       System.out.println(flowSM.getStateMachine());
       System.out.println(timeoutCandidate);
            
       while(true){
           
           int state = flowSM.getStateMachine();
           
          switch (state){
                case 1: //FOLLOWER
                                    
                    String received;
                    received = follower.receiver(timeoutFollower);

                    if(received.contains("ERROR")){
                        flowSM.fsm = flowSM.candidate;
                        System.out.println("SOU CANDIDATO");
                    }
                    
                    break;
                    
                case 2: //CANDIDATE
                    
                    candidate.startElection();
            
                    String electionsResult = candidate.resultsElection(timeoutCandidate);

                    if(electionsResult.contains("ACCEPTED")){
                        flowSM.fsm = flowSM.leader;
                        System.out.println("I'M LEADER");
                    }
 
                    else{ //(electionsResult.contains("becomeFOLLOWER")){
                        flowSM.fsm = flowSM.follower;      
                        System.out.println("I'M FOLLOWER");
                    }
                    
                    break;
//                    
//                case 3: //LEADER
//                    
//                    Thread t= new Thread();
//                    t.start();
//                    
//                    break;
//                
                default: 
                    
                    System.out.println("UNKNOWN STATE");
                    break;        
         }
      
       }      

    }
}


package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class States {
    
     
    public void States() throws IOException{
        
       Follower follower = new Follower();
       Candidate candidate = new Candidate();
       Leader leader = new Leader();
         
       FlowStateMachine flowSM = new FlowStateMachine();
       
       //Processamento do FIFO
       ConcurrentLinkedQueue<Pair> queue = new ConcurrentLinkedQueue<>();
       DataProcessing dataProcessing = new DataProcessing(follower.getTimeout(),candidate.getTimeout(),queue);
       
       //Parametros da comunicaçao UDP
       int port = follower.comModule.port;
       InetAddress groupIP = InetAddress.getByName(follower.comModule.group);
       
       //Iniciar thread receive
       ThreadReceive receiverThread = new ThreadReceive(port, groupIP, queue);
       Thread receiver = new Thread(receiverThread);
       receiver.start();
       
       //INICIO DA STATE MACHINE
       flowSM.setFollower();
       
       int state;
       
       while(true){
           
        state = flowSM.getStateMachine();
           //Guardar o valor do time start
        long timeStart = System.currentTimeMillis();
        
        switch (state){
            case 1: //FOLLOWER
                System.out.println("SOU Follower");

                boolean received;
                
                received=dataProcessing.checkHeartBeats(timeStart);
               
                if(!received){
                    flowSM.fsm = flowSM.candidate;
                    System.out.println("SOU CANDIDATO");
                }

                break;

            case 2: //CANDIDATE

                candidate.startElection();

                String electionsResult = candidate.resultsElection();

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


package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class States {
    
    public int term = 0;
    private int nNodes = 5;
    
    public void States() throws IOException{
       
       //FIFO
       ConcurrentLinkedQueue<Pair> queue = new ConcurrentLinkedQueue<>();   
       
       //STATE MACHINE
       FlowStateMachine flowSM = new FlowStateMachine();
       flowSM.setFollower();
       
       Follower follower = new Follower(queue);
       Candidate candidate = new Candidate(queue, this.nNodes);
       Leader leader = new Leader(queue);
       
       int state;
       String nextState;       
            
       //COMM UDP
       int port = follower.comModule.port;
       InetAddress groupIP = InetAddress.getByName(follower.comModule.group);
       
       //Thread receive
       ThreadReceive receiverThread = new ThreadReceive(port, groupIP, queue);
       Thread receiver = new Thread(receiverThread);
       receiver.start();

       
       while(true){
           
        state = flowSM.getStateMachine();
        System.out.println("TERMO:" + this.term);
        
        //Guardar o valor do time start
        long timeStart = System.currentTimeMillis();
        
        switch (state){
            case 1: //FOLLOWER
                
                String info = follower.cycle(timeStart,this.term);
                
                String[] parts = info.split("@");
                nextState = parts[0];
                this.term = Integer.parseInt(parts[1]); //atualizar termo
                
                switch (nextState){
                    case "FOLLOWER":
                        flowSM.setFollower();
                    break;
                    case "CANDIDATE":
                        flowSM.setCandidate(); 
                    break; 
                    case "newLeaderAccepted":
                        flowSM.setFollower();
                    break;
                }
                
                break;

            case 2: //CANDIDATE
                
                String[] updates = new String[2];
                
                updates = candidate.cycle(timeStart, ++this.term); //sempre que alguem se torna candidato, aumentar termo
                
                this.term = Integer.parseInt(updates[0]);
                nextState = updates[1];                
                
                switch(nextState){
                    case "FOLLOWER":
                        flowSM.setFollower();
                        break;
                    case "CANDIDATE":
                        flowSM.setCandidate();
                        break;
                    case "LEADER":
                        flowSM.setLeader();
                        break;
                }

                break;
                
            case 3: //LEADER    
                
                this.term=leader.cycle(this.term); // só retorna de cycle quando tiver de mudar para follower
                flowSM.setFollower();
                break;
              
            default: 
                
                System.out.println("UNKNOWN STATE");
                break;        
         }
      
       }      

    }
}

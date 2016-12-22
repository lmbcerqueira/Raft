
package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class States {
    
    private int term = 0;
    private int nNodes = 5;
    
    public void States() throws IOException{
   
       FlowStateMachine flowSM = new FlowStateMachine();
       
       //Processamento do FIFO
       ConcurrentLinkedQueue<Pair> queue = new ConcurrentLinkedQueue<>();
       
       Follower follower = new Follower(queue);
       Candidate candidate = new Candidate(queue, this.nNodes);
       Leader leader = new Leader(queue);
       
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
       String nextState,info;
       
       while(true){
           
        state = flowSM.getStateMachine();
        System.out.println("TERMO:" + this.term);
        
        //Guardar o valor do time start
        long timeStart = System.currentTimeMillis();
        
        switch (state){
            case 1: //FOLLOWER
                
                info = follower.cycle(timeStart,this.term);
                
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
                
                nextState = candidate.cycle(timeStart, ++this.term);
                
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

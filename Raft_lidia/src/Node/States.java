
package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class States {
    
    public static int term = 0;
    public static int nNodes =5;
    public static int[] nextFollowersIndex; //variável que guarda o indice a partir do qual se manda newEntry ao follower
    public static int[] writtenIndex; //variável para decidir qual o commitedCommand
    public static int commitIndex; //commitedCommand = segundo menor dos writtenIndex
    
    public States(){
        
        nextFollowersIndex = new int[nNodes]; 
        writtenIndex = new int[nNodes];
        Arrays.fill(writtenIndex , 0);
        commitIndex = 0;
    }
    
    public void mainCycle(String id) throws IOException{
           
       // log
       String filename = "log_" + id + ".txt";
       Log log = new Log(filename);
       
       //ATUALIZA TERMO SE NECESSARIO
       int[] logInfo = new int[2];
       logInfo = log.getLogLastEntry();
       term = logInfo[1];
       System.out.println("TERMO DO LOG:" + term);

       //when a leader first comes to power, 
       //it initializes all nextIndex values to the index just after the last one in its log
       Arrays.fill(nextFollowersIndex, term - 1);
        
       //FIFO
       ConcurrentLinkedQueue<Pair> queue = new ConcurrentLinkedQueue<>();
       ConcurrentLinkedQueue<Pair> queueLOG = new ConcurrentLinkedQueue<>();
       
       //STATE MACHINE
       FlowStateMachine flowSM = new FlowStateMachine();
       flowSM.setFollower();
       
       Follower follower = new Follower(queue, log);
       Candidate candidate = new Candidate(queue, this.nNodes, log);
       Leader leader = new Leader(queue,log);
       
       int state;
       String nextState;       
            
       //COMM UDP
       int port = follower.comModule.port;
       InetAddress groupIP = InetAddress.getByName(follower.comModule.group);
       
       //Thread receive
       ThreadReceive receiverThread = new ThreadReceive(port, groupIP, queue, queueLOG, log, follower.comModule);
       Thread receiver = new Thread(receiverThread);
       receiver.start();
       
       //Thread log
       ThreadLog logThread = new ThreadLog(queueLOG, log, follower.comModule);
       Thread logWriter = new Thread(logThread);
       logWriter.start();       
       
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

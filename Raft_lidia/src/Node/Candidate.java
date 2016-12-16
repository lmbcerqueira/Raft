
package Node;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Candidate {
    
    private final ComunicationUDP comModule;
    private final long timeout;
    private final ConcurrentLinkedQueue<Pair> queue;
    private final DataProcessing dataProcessing;
     
    public Candidate(ConcurrentLinkedQueue<Pair> queue) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getTimeout();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.timeout, this.queue);
    }
    
    public long getTimeout(){
        
        int min_value = 1000; //?????????
        int max_value = 1100; //????????????
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
    }
      
    public void startElection(int term) throws IOException{
        
        String electionString= "ELECTION@"+Integer.toString(term);

        this.comModule.sendMessageBroadcast(electionString); 
    }
    
    public String cycle(long timeStart, int term) throws IOException{
        
        String received;
        String nextState = "CANDIDATE";
        startElection(term);

        received = dataProcessing.resultElections(timeStart);

        switch(received){
            case "tryAGAIN":
                nextState = "CANDIDATE";
                System.out.println("tentar de NOVO");
                break;
            case "ACCEPTED":
                nextState = "LEADER";
                System.out.println("I'M LEADER");
                break;
            case "REJECTED":
                nextState = "FOLLOWER";
                System.out.println("I'M FOLLOWER");
                break;
        }
        
        return nextState;
        
    }
}

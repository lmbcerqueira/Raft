
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
        
        int min_value = 2000; //?????????
        int max_value = 2100; //????????????
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
    }
      
    public void startElection(int term) throws IOException{
        
        String electionString= "ELECTION@"+Integer.toString(term);
        System.out.println("candidato: START ELECTION");
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
                System.out.println("candidato: tenta de novo, TIMEOUT ");
                break;
            case "ACCEPTED":
                nextState = "LEADER";
                System.out.println("candidato: fUI ACEITE");
                break;
            case "REJECTED":
                nextState = "FOLLOWER";
                System.out.println("candidato: fui REJEITADO");
                break;
        }
        
        return nextState;
        
    }
}

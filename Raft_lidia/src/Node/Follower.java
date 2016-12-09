
package Node;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Follower extends Thread {
    
    public final ComunicationUDP comModule;
    private final long timeout;
    private final DataProcessing dataProcessing;
    private final ConcurrentLinkedQueue<Pair> queue;

    public Follower(ConcurrentLinkedQueue<Pair> queue) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout=this.getTimeout();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.timeout, this.queue);
    }
    
    public String cycle(long timeStart, int term){
        
        String received;
        String nextState = "FOLLOWER";
        
        received = dataProcessing.checkHeartBeatsCandidate(timeStart);
                
        switch(received){
            case "HEARTBEATS":
                System.out.println("RECEBI UM HeartBeat");
                break;
            case "REQUESTVOTE":
                String answer = null;
                System.out.println("RECEBI UM RequestVote");  
                //answer = follower.vote(term, receivedTerm); //receivedTermMissing
                switch (answer) {
                    case "REJECTED":
                        
                        break;
                    case "ACCEPTED":
                        nextState="newLeaderAccepted";
                        //this.term = receivedTerm;
                        break;
                }
                break;
            case "TIMEOUT":
                nextState = "CANDIDATE";
                System.out.println("SOU CANDIDATO");
                break;
        }
        
        return nextState;
    }
    
    public long getTimeout(){
                 
        int min_value = 500;
        int max_value = 1000;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
       
    }
    
    public String vote(int term, int receivedTerm ){
        
        String answer = null;
        
        if (receivedTerm > term){
            answer = "ACCEPTED";
            
        }
        else
            answer = "REJECTED"; 
        
        answer = answer+"@"+ Integer.toString(term);
            
        this.comModule.sendMessage(answer); 
        return answer;
    }
   
    
}

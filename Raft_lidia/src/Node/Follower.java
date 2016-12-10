
package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    
    public String cycle(long timeStart, int term) throws UnknownHostException, IOException{
        
        String received;
        String nextState = "FOLLOWER";
        
        String receivedAndInetAndTerm = dataProcessing.checkHeartBeatsandCandidate(timeStart);
        String[] parts = receivedAndInetAndTerm.split("@");

        received=parts[0];
        String stringInet=parts[1];
        int receivedTerm=Integer.valueOf(parts[2]);
        System.out.println(received+"\n"+stringInet+"\n"+receivedTerm);
        
       
        switch(received){
            case "HEARTBEATS":
                System.out.println("RECEBI UM HeartBeat");
                break;
            case "REQUESTVOTE":
                
                InetAddress inet=InetAddress.getByName(stringInet);
                
                String answer = null;
                System.out.println("RECEBI UM RequestVote");  
                answer = vote(term, receivedTerm); 
                switch (answer) {
                    case "REJECTED":
                        String message="FOLLOWER@"+Integer.toString(term);
                        comModule.sendMessage(message, inet);
                        break;
                    case "ACCEPTED":
                        nextState="newLeaderAccepted";
                        term = receivedTerm;
                        break;
                }
                break;
            case "TIMEOUT":
                nextState = "CANDIDATE";
                System.out.println("SOU CANDIDATO");
                break;
        }
        
        return nextState+"@"+Integer.toString(term);
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
        return answer;
    }
   
}

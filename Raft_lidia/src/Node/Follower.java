
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
        this.timeout = this.getTimeout();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.timeout, this.queue);
    }
    
    public String cycle(long timeStart, int term) throws UnknownHostException, IOException{
        
        String msgReceived;
        String msgToSend;
        String nextState = "FOLLOWER";
        
        String receivedInetAndTerm = dataProcessing.checkHeartBeatsandCandidate(timeStart);
        String[] parts = receivedInetAndTerm.split("@");

        msgReceived = parts[0];
        String stringInet = parts[1];
        int receivedTerm = Integer.valueOf(parts[2]);
        System.out.println("FOLLOWER: message received: " + msgReceived + ";" + stringInet + ";" + receivedTerm);
        
       
        switch(msgReceived){
            case "HEARTBEATS":
                System.out.println("FOLLOWER: RECEBI UM HeartBeat");
                break;
                
            case "REQUESTVOTE":
                InetAddress inet = InetAddress.getByName(stringInet); //given the host name 
                //System.out.println(inet.getHostName()); 
                String answer = vote(term, receivedTerm); 
                switch (answer) {
                    case "REJECTED":
                        msgToSend = "FOLLOWER@" + Integer.toString(term);
                        comModule.sendMessage(msgToSend, inet);
                        System.out.println("FOLLOWER: RECEBI UM RequestVote - Rejeitei");
                        break;
                    case "ACCEPTED":
                        nextState = "newLeaderAccepted";
                        term = receivedTerm;
                        msgToSend = "ACCEPTED@" + Integer.toString(term);
                        comModule.sendMessage(msgToSend, inet);
                        System.out.println("FOLLOWER: RECEBI UM RequestVote - ACEITEI");
                        break;
                }
                break;
            case "TIMEOUT":
                nextState = "CANDIDATE";
                break;
        }
        
        return nextState + "@" + Integer.toString(term);
    }
    
    public long getTimeout(){
                 
        int min_value = 500;
        int max_value = 1000;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
       
    }
    
    public String vote(int term, int receivedTerm ){
        
        String answer = null;
        
        if (receivedTerm > term)
            answer = "ACCEPTED";
            
        else
            answer = "REJECTED"; 
        return answer;
    }
   
}

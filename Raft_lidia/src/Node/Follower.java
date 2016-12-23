
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
        this.dataProcessing = new DataProcessing(this.queue);
    }
    
    public long getTimeout(){
                 
        int min_value = 3000;
        int max_value = 4000;
        
        return (min_value + (int)(Math.random() * ((max_value - min_value) + 1)))/100;   
    }
    

    
    public String cycle(long timeStart, int term) throws UnknownHostException, IOException{
        
        String msgReceived;
        String msgToSend;
        String nextState = "FOLLOWER";
        
        String receivedInetAndTerm = checkHeartBeatsandElections(timeStart, term);
        String[] parts = receivedInetAndTerm.split("@");

        msgReceived = parts[0];
        String stringInet = parts[1];
        int receivedTerm = Integer.valueOf(parts[2].trim());
        System.out.println("FOLLOWER: message received: " + msgReceived + "; INET:" + stringInet + ";TERM:" + receivedTerm);
        
        InetAddress inet;
        
        switch(msgReceived){
            
            case "HEARTBEATS":
                System.out.println("FOLLOWER: RECEBI UM HeartBeat");
                term = receivedTerm;
                break;
                
            case "REQUESTVOTE":
                inet = InetAddress.getByName(stringInet); 
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
                        System.out.println("FOLLOWER: Update term: "+ term);
                        msgToSend = "ACCEPTED@" + Integer.toString(term);
                        System.out.println("DEBUG FOLLOWER: msgToSend " + msgToSend);
                        comModule.sendMessage(msgToSend, inet);
                        System.out.println("FOLLOWER: RECEBI UM RequestVote - ACEITEI");
                        break;
                }
                break;
                
            case "TIMEOUT":
                nextState = "CANDIDATE";
                break;
                
            case "ERROR": //Term received not updated
                inet = InetAddress.getByName(stringInet); 
                msgToSend = "ERROR@" + Integer.toString(term);   
                comModule.sendMessage(msgToSend, inet);
                System.out.println("FOLLOWER: RECEBI UM TERMO MENOR QUE O MEU");
                break;
        }
        
        return nextState + "@" + Integer.toString(term);
    }
    
    public String checkHeartBeatsandElections(long timeStart, int term){ //ver questão de 1970
        //A ESPERA DE HEARTBEATS: RECEBE HEART BEATS OU NAO E ELECTION
        //RECEBE HEART BEATS RESPONDE COM HEARTBEATS SENAO MANDA ELECTION
        //RECEBE ELECTION MANDA ANSWER
        
        InetAddress inet;
        int receivedTerm;
        String IPsender;
        
        while(true){
            
            long x = System.currentTimeMillis() - timeStart;
            float xSeconds=x/1000F; //time in seconds
            
            //System.out.println("TIMEOUT: " + this.timeout + "; Ja passaram: " + xSeconds);
            
            if(xSeconds > this.timeout)
                return "TIMEOUT@" + "TIMEOUT" + "@1"; // dois ultimos elementos don't care
             
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime() < timeStart)
                this.queue.poll();
            
            else if(!dataProcessing.isReceivedTermUPdated(term)){
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                return "ERROR@" + IPsender + "@" + Integer.toString(term);
            }    
            
            else if(dataProcessing.contains("HELLO")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                System.out.println("Data Processing - received IP - " + IPsender);
                this.queue.poll();
                return "HEARTBEATS@" + IPsender + "@" + Integer.toString(receivedTerm);
            }
            
            else if(dataProcessing.contains("ELECTION")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                System.out.println("Data Processing - received IP - " + IPsender);
                this.queue.poll();
                return "REQUESTVOTE@" + IPsender + "@" + Integer.toString(receivedTerm);
            }
            
            else if(!(dataProcessing.contains("HELLO") || dataProcessing.contains("ELECTION"))){
                this.queue.poll();
            }     
            
        }

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

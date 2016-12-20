package Node;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

class DataProcessing {
    private final int numeroNos=5;
    private final long timeOut;
    private final ConcurrentLinkedQueue<Pair> queue;

    public DataProcessing(long timeOut, ConcurrentLinkedQueue<Pair> queue) {
        this.timeOut = timeOut/100; //PQ?
        this.queue = queue;
    }
    
    private boolean contains(String message){
        return this.queue.peek().getMessage().contains(message);
    } 
    
    public String checkHeartBeatsandCandidate(long timeStart){ //ver questão de 1970
        //A ESPERA DE HEARTBEATS: RECEBE HEART BEATS OU NAO E ELECTION
        //RECEBE HEART BEATS RESPONDE COM HEARTBEATS SENAO MANDA ELECTION
        //RECEBE ELECTION MANDA ANSWER
        
        //System.out.println("TIMEOUT="+timeOut);
        InetAddress inet;
        int receivedTerm;
        String IPsender;
        
        while(true){
            
            long x = System.currentTimeMillis() - timeStart;
            float xSeconds=x/1000F; //time in seconds
            
            if(xSeconds > timeOut)
                return "TIMEOUT@" + "TIMEOUT" + "@1"; // dois ultimos elementos don't care
             
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime() < timeStart)
                this.queue.poll();
            
            else if(contains("HELLO")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                //System.out.println("Data Processing - received IP - " + IPsender);
                this.queue.poll();
                return "HEARTBEATS@" + IPsender + "@" + Integer.toString(receivedTerm);
            }
            
            else if(contains("ELECTION")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                System.out.println("Data Processing - received IP - " + IPsender);
                this.queue.poll();
                return "REQUESTVOTE@" + IPsender + "@" + Integer.toString(receivedTerm);
            }
            
            else if(!(contains("HELLO") || contains("ELECTION"))){
                this.queue.poll();
            }     
            
        }

    }

    public String resultElections(long timeStart) {
        
        int votes = 0;
        
        while(true){
            
            long x = System.currentTimeMillis()-timeStart;
            float xSeconds=x/1000F; //time in seconds
            
            if(xSeconds > timeOut){
                
                return "tryAGAIN";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            
            else if(contains("ACCEPTED")){
                System.out.println("CANDIDATO: recebi um voto");
                votes++;
                this.queue.poll();
                if(votes > (this.numeroNos/2)) 
                    return "ACCEPTED";
            }
            
            else if(contains("REJECTED")){
                this.queue.poll();
                return "REJECTED";
            }
            else if(!(contains("ACCEPTED")||contains("REJECTED"))){
                this.queue.poll();
            }  
            
            
        }
        

    }
    
    public void checkIncomingLeaderMsg(int term){
        
        int receivedTerm;
        String pair;
        
        while(true){
            if (this.queue.isEmpty())
                continue;
            else{
                receivedTerm = this.queue.poll().getTerm();
                System.out.println("DEBUG LEADER: " + receivedTerm);
                
                if(receivedTerm > term)
                    break;   
            }  
        }
        
    }
    
}


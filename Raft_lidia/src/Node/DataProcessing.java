package Node;

import java.util.concurrent.ConcurrentLinkedQueue;

class DataProcessing {
    private final int numeroNos=3;
    private final long timeOut;
    private final ConcurrentLinkedQueue<Pair> queue;

    public DataProcessing(long timeOut, ConcurrentLinkedQueue<Pair> queue) {
        this.timeOut = timeOut/100;
        this.queue = queue;
    }
    
    private boolean contains(String message){
        return this.queue.peek().getMessage().contains(message);
    } 
    
    public String checkHeartBeatsCandidate(long timeStart){ //ver questão de 1970
        //A ESPERA DE HEARTBEATS: RECEBE HEART BEATS OU NAO E ELECTION
        //RECEBE HEART BEATS RESPONDE COM HEARTBEATS SENAO MANDA ELECTION
        //RECEBE ELECTION MANDA ANSWER
        
        String message="HELLO";
        System.out.println("TIMEOUT="+timeOut);
        
        while(true){
            long x = System.currentTimeMillis() - timeStart;
            float xSeconds=x/1000F; //time in seconds
            if(xSeconds > timeOut){
                System.out.println("Ja passou :" + xSeconds);
                return "TIMEOUT";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            else if(contains("HELLO")){
                this.queue.poll();
                return "HEARTBEATS";
            }
            else if(contains("ELECTION")){
                this.queue.poll();
                return "REQUESTVOTE";
            }
            else if(!(contains("HELLO")||contains("ELECTION"))){
                this.queue.poll();
            }     
            

        }

    }

    public String resultElections(long timeStart) {
        int votes = 0;
        System.out.println("TIMEOUT="+timeOut);
        
        while(true){
            
            long x = System.currentTimeMillis()-timeStart;
            float xSeconds=x/1000F; //time in seconds
            if(xSeconds > timeOut){
                System.out.println("Ja passou :"+xSeconds);
                votes=0; //useless
                return "tryAGAIN";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            
            else if(contains("ACCEPTED")){
                votes++;
                this.queue.poll();
                if(votes > (this.numeroNos/2)){
                    votes=0; //useless
                    return "ACCEPTED";
                }
            }
            
            else if(contains("REJECTED")){
                this.queue.poll();
                votes=0; //useless
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
                pair = this.queue.poll().getMessage();
                String[] parts = pair.split("@");
                receivedTerm = Integer.parseInt(parts[1]);
                
                if(receivedTerm > term)
                    break;   
            }  
        }
        
    }
    
}


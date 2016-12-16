package Node;

import java.util.concurrent.ConcurrentLinkedQueue;

class DataProcessing {
    private final int numeroNos=3;
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
        
        //String message="HELLO";
        System.out.println("TIMEOUT="+timeOut);
        String inet;
        int receivedTerm;
        
        while(true){
            long x = System.currentTimeMillis() - timeStart;
            float xSeconds=x/1000F; //time in seconds
            if(xSeconds > timeOut){
                inet = "NADA";
                //System.out.println("Ja passou :" + xSeconds);
                return "TIMEOUT@" + inet + "@-1";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            else if(contains("HELLO")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet().toString();
                this.queue.poll();
                return "HEARTBEATS@"+inet+"@"+Integer.toString(receivedTerm);
            }
            else if(contains("ELECTION")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet().toString();
                this.queue.poll();
                System.out.println("dataPROCESSING:"+inet);
                return "REQUESTVOTE@"+inet+"@"+Integer.toString(receivedTerm);
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
                return "tryAGAIN";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            
            else if(contains("ACCEPTED")){
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
                pair = this.queue.poll().getMessage();
                String[] parts = pair.split("@");
                receivedTerm = Integer.parseInt(parts[1]);
                
                if(receivedTerm > term)
                    break;   
            }  
        }
        
    }
    
}


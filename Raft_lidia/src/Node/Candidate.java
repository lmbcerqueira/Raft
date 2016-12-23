
package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Candidate {
    
    private final ComunicationUDP comModule;
    private final long timeout;
    private final ConcurrentLinkedQueue<Pair> queue;
    private final DataProcessing dataProcessing;
    private final int nNodes;
     
    public Candidate(ConcurrentLinkedQueue<Pair> queue, int nNodes) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getTimeout();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.queue);
        this.nNodes = nNodes;
    }
    
    public long getTimeout(){
        
        int min_value = 2000; //?????????
        int max_value = 2100; //????????????
        
        return (min_value + (int)(Math.random() * ((max_value - min_value) + 1)))/100;
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

        received = resultElections(timeStart, term);

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

    public String resultElections(long timeStart, int term) throws IOException {
        
        int votes = 0;
        InetAddress inet;
        String IPsender;        
        
        while(true){
            
            long x = System.currentTimeMillis()-timeStart;
            float xSeconds=x/1000F; //time in seconds
            
            if(xSeconds > this.timeout)
                return "tryAGAIN";
            
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            
            else if(this.dataProcessing.contains("ERROR")){
                System.out.println("CANDIDATO: recebi error - nao estou updated");
                //atualizar term
                //this.term =       BUG HERE          
                this.queue.poll();
                return "REJECTED";
            }
            else if(this.dataProcessing.contains("ACCEPTED")){
                System.out.println("CANDIDATO: recebi um voto");
                votes++;
                this.queue.poll();
                if(votes > (this.nNodes/2)) 
                    return "ACCEPTED";
            }
            
            else if(this.dataProcessing.contains("REJECTED")){
                this.queue.poll();
                return "REJECTED";
            }
            
            else if(this.dataProcessing.contains("HELLO")){
                
                // se houver um líder com termo inferior enviar erro
                if(!this.dataProcessing.isReceivedTermUPdated(term)){
                    inet = this.queue.peek().getInet();
                    IPsender = inet.getHostAddress();
                    this.queue.poll();  
                    String msgToSend = "ERROR@" + IPsender + "@" + Integer.toString(term);
                    this.comModule.sendMessage(msgToSend, inet);
                }
                // se houver um lider com um termo maior passa a follower
                else{
                    this.queue.poll();  
                    return "REJECTED";
                }     
            }
            
            else if(!(this.dataProcessing.contains("ACCEPTED") || this.dataProcessing.contains("REJECTED"))){
                this.queue.poll();
            }  
        }
    }
    
}
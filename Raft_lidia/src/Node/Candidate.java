
package Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Candidate {
    
    private final ComunicationUDP comModule;
    private final long timeout;
    private final ConcurrentLinkedQueue<Pair> queue;
    private final DataProcessing dataProcessing;
    private final int nNodes;
    private final Log log;
     
    public Candidate(ConcurrentLinkedQueue<Pair> queue, int nNodes, Log log) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getTimeout();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.queue);
        this.nNodes = nNodes;
        this.log = log;
    }
    
    public long getTimeout(){
        
        int min_value = 2000; //?????????
        int max_value = 2100; //????????????
        
        return (min_value + (int)(Math.random() * ((max_value - min_value) + 1)))/100;
    }
      
    public void startElection(int term) throws IOException{
        
        int[] info = new int[2];
        info = this.log.getLogLastEntry();
        
        int prevLogIndex = info[0];
        int prevLogTerm = info[1];
        
        String electionString= "ELECTION@" + Integer.toString(term) + "@" + Integer.toString(prevLogTerm) + "@" + Integer.toString(prevLogIndex);
        
        System.out.println("candidato: START ELECTION");
        
        this.comModule.sendMessageBroadcast(electionString); 
    }
    
    public String[] cycle(long timeStart, int term) throws IOException{
        
        String[] ret = new String[2];
        
        String[] received = new String[2];
        String nextState = "CANDIDATE";
        startElection(term);

        received = resultElections(timeStart, term);
        
        String result = received[1];

        switch(result){
            case "tryAGAIN":
                nextState = "CANDIDATE";
                //System.out.println("candidato: tenta de novo, TIMEOUT ");
                break;
            case "ACCEPTED":
                nextState = "LEADER";
                //System.out.println("candidato: fUI ACEITE");
                break;
            case "REJECTED":
                nextState = "FOLLOWER";
                //System.out.println("candidato: fui REJEITADO");
                break;
        }
        
        ret[0] = received[0]; //term updated
        ret[1] = nextState;
        return ret;
        
    }

    public String[] resultElections(long timeStart, int term) throws IOException {
        
        int votes = 1; //ja votou em si
        InetAddress inet;
        String IPsender;   
        String[] ret = new String[2];
        ret[0] = Integer.toString(term);
        
        while(true){
            
            long x = System.currentTimeMillis()-timeStart;
            float xSeconds=x/1000F; //time in seconds
            
            if(xSeconds > this.timeout){
                ret[1] = "tryAGAIN";
                return ret;
            }
            
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            
            // rejeitar mensagens do cliente
            else if(this.queue.peek().getTerm() < 0)
                this.queue.poll();           
            
            else if(this.dataProcessing.contains("CANDNOTUPD")){
                //System.out.println("CANDIDATO: recebi error - nao estou updated");
                //atualizar term
                ret[0] = Integer.toString(this.queue.peek().getTerm());   
                ret[1] = "REJECTED";
                this.queue.poll();
                return ret;
            }
            else if(this.dataProcessing.contains("ACCEPTED")){
                //System.out.println("CANDIDATO: recebi um voto");
                votes++;
                this.queue.poll();
                if(votes > (this.nNodes/2)){
                    ret[1] = "ACCEPTED";
                    return ret; 
                }
            }
            
            else if(this.dataProcessing.contains("REJECTED")){
                this.queue.poll();
                ret[1] = "REJECTED";
                return ret;
            }
            
            else if(this.dataProcessing.contains("HELLO")){
                
                // se houver um líder com termo inferior enviar erro
                if(!this.dataProcessing.isReceivedTermUPdated(term)){
                    inet = this.queue.peek().getInet();
                    this.queue.poll();  
                    String msgToSend = "LEDNOTUPD@" + Integer.toString(term);
                    this.comModule.sendMessage(msgToSend, inet);
                }
                // se houver um lider com um termo maior passa a follower
                else{
                    this.queue.poll();  
                    ret[1] = "REJECTED";
                    return ret;
                }     
            }
            
            else if(!(this.dataProcessing.contains("ACCEPTED") || this.dataProcessing.contains("REJECTED"))){
                this.queue.poll();
            }  
        }
    }
    
}
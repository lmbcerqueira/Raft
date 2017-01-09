
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
    private final Log log;
   

    public Follower(ConcurrentLinkedQueue<Pair> queue, Log log) throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout = this.getTimeout();
        this.queue = queue;
        this.dataProcessing = new DataProcessing(this.queue);
        this.log = log;
  
    }
    
    public long getTimeout(){
                 
        int min_value = 2000;//3
        int max_value = 3500;//4
        
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
        //mesage@ipsender@term@prevIndex@prevTerm 
        int receivedLastIndex = Integer.valueOf(parts[3].trim());
        int receivedLastTerm = Integer.valueOf(parts[4].trim());
        InetAddress inet;
        
        switch(msgReceived){
            
            case "HEARTBEATS":
                System.out.println("FOLLOWER: RECEBI UM HeartBeat");
                term = receivedTerm;
                break;
                
            case "REQUESTVOTE":
               
                inet = InetAddress.getByName(stringInet); 
                String answer=null;
                int logTerm=this.log.lookForTerm(receivedLastIndex);
                
                if(logTerm==-1){ //FICEHIRO AINDA NAO EXISTE....
                   answer = vote(term, receivedTerm); 
                }
                else if(receivedLastTerm==logTerm){
                    answer = vote(term, receivedTerm); 
                }
                else{
                    answer = "REJECTED"; 
                    System.out.println("FOLLOWER: RECEBI UM RequestVote - Rejeitei por causa do LOG INCONSISTENCY"+
                            "  receivedLastTerm="+receivedLastTerm+"  logTerm"+logTerm+ " FOR LAST INDEX="+receivedLastIndex);
                }
                    
                
                switch (answer) {
                    case "REJECTED":
                        msgToSend = "FOLLOWER@" + Integer.toString(term);
                        comModule.sendMessage(msgToSend, inet);
                        System.out.println("FOLLOWER: RECEBI UM RequestVote - Rejeitei");
                        break;
                    case "ACCEPTED":
                        nextState = "newLeaderAccepted";
                        term = receivedTerm;
                        //System.out.println("FOLLOWER: Update term: "+ term);
                        msgToSend = "ACCEPTED@" + Integer.toString(term);
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
                msgToSend = "CANDNOTUPD@" + Integer.toString(term);   
                comModule.sendMessage(msgToSend, inet);
                //System.out.println("FOLLOWER: RECEBI UM TERMO MENOR QUE O MEU");
                break;
        }
        
        return nextState + "@" + Integer.toString(term);
    }
    
    public String checkHeartBeatsandElections(long timeStart, int term) throws IOException{ //ver questão de 1970
        //A ESPERA DE HEARTBEATS: RECEBE HEART BEATS OU NAO E ELECTION
        //RECEBE HEART BEATS RESPONDE COM HEARTBEATS SENAO MANDA ELECTION
        //RECEBE ELECTION MANDA ANSWER
        
        InetAddress inet;
        int receivedTerm;
        String IPsender;
        
        
        while(true){
            
            long x = System.currentTimeMillis() - timeStart;
            float xSeconds=x/1000F; //time in seconds
            
            if(xSeconds > this.timeout)
                return "TIMEOUT@" + "TIMEOUT" + "@1"
                        + "@" + "1"+ "@" + "1"; // dois ultimos elementos don't care
             
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime() < timeStart)
                this.queue.poll();
            
            // rejeitar mensagens do cliente
            else if(this.queue.peek().getTerm() < 0)
                this.queue.poll();
            
            // A primeira coisa a verificar é se a msg que recebemos tem um termo < que o nosso; responder c/ Erro se sim
            else if(!dataProcessing.isReceivedTermUPdated(term)){
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                String message= "ERROR@" + IPsender + "@" + Integer.toString(term)
                        + "@" + Integer.toString(this.queue.peek().getPrevLogIndex())+ "@" + Integer.toString(this.queue.peek().getPrevLogTerm());
                this.queue.poll();
                return message;
            }    
            
            else if(dataProcessing.contains("HELLO")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                //System.out.println("Data Processing - received IP - " + IPsender);
                
                String message="HEARTBEATS@" + IPsender + "@" + Integer.toString(receivedTerm)
                        + "@" + Integer.toString(this.queue.peek().getPrevLogIndex())+ "@" + Integer.toString(this.queue.peek().getPrevLogTerm());
                this.queue.poll();
                return message;
            }
            
            else if(dataProcessing.contains("ELECTION")){
                receivedTerm = this.queue.peek().getTerm();
                inet = this.queue.peek().getInet();
                IPsender = inet.getHostAddress();
                
                //MESSAGE ELECTION="ELECTION@" + Integer.toString(term) + "@" + Integer.toString(prevLogTerm) + "@" + Integer.toString(prevLogIndex);
        
                //System.out.println("Data Processing - received IP - " + IPsender);
                
                String message= "REQUESTVOTE@" + IPsender + "@" + Integer.toString(receivedTerm)
                        + "@" + Integer.toString(this.queue.peek().getPrevLogIndex())+ "@" + Integer.toString(this.queue.peek().getPrevLogTerm());
                this.queue.poll();
                return message;
            }
                        
            else if(!(dataProcessing.contains("HELLO") || dataProcessing.contains("ELECTION"))){
                this.queue.poll();
            }     
            
        }

    }
    
    public String vote(int term, int receivedTerm ){
               
        if (receivedTerm > term)
            return "ACCEPTED";   
        else
            return "REJECTED"; 

    }
      
}

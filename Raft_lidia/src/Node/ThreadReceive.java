
package Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadReceive extends Thread {
    
    private final int port;
    private final InetAddress groupIP;
    private final ConcurrentLinkedQueue<Pair> queue;
    private final ConcurrentLinkedQueue<Pair> queueLOG;
    private final Log log;
    public final ComunicationUDP comModule;
    
    ThreadReceive(int port, InetAddress groupIP, ConcurrentLinkedQueue<Pair> queue, ConcurrentLinkedQueue<Pair> queueLOG, Log log, ComunicationUDP comModule){
        this.port = port;
        this.groupIP = groupIP;
        this.queue = queue;
        this.queueLOG = queueLOG;
        this.log = log;
        this.comModule = comModule;
    }
       
    public void run() {
        
        long time;
        MulticastSocket socket;
        InetAddress inet = null;
        //int term,prevLogTerm,prevLogIndex;
        
        //get IP
        try{
            String[] cmd = {
                "/bin/sh",
                "-c",
                "ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\\.){3}[0-9]*).*/\\2/p'"
            };
            
            final Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader brinput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for(;;){
                States.myIP = brinput.readLine();
                if (States.myIP != null)
                    break;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        
        //filter messages        
        try {
            
            System.out.println("MyIP:" + States.myIP);
            socket = new MulticastSocket(this.port);
            
            socket.joinGroup(groupIP);
            
            while(true){
                
                time = System.currentTimeMillis();
                
                byte[] buf = new byte[1024];
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);
                
                inet = pack.getAddress();
                String inetStr = inet.toString();
                String[] parts = inetStr.split("/");
                String senderIP = parts[1];
                
                byte[] bytes = pack.getData();
                String receivedPacket = new String(bytes); 
                parts = receivedPacket.split("@");
                
                String to = parts[0];
                String message = parts[1];
               
               
                int term = Integer.parseInt(parts[2].trim());
               
                
                
                //Logs
                if ( to.compareTo("BROADCAST")==0 && message.contains("AppendEntry")){ 
                    //Se for lider NAO ADICIONAR na queue
                    if(senderIP.compareTo(States.myIP)!=0){
                        int prevTerm=Integer.parseInt(parts[3].trim());
                        int prevIndex=Integer.parseInt(parts[4].trim());
                        Pair pair = new Pair(time, message, inet, term, prevIndex,prevTerm);
                        queueLOG.add(pair);
                    }   
                }
                
                //update_logs
                else if ( to.compareTo(States.myIP)==0 && message.contains("CHECK_PREVIOSENTRY")){
                    
                    //apagar linha anterior
                    //this.log.removeLastLine();
                                   
                    //verificar LogMatchingProperty para a Entry recebida
                    int prevIndex;
                    int prevTerm;
                    String prev[]=message.split(":");
                    String check="CHECK_PREVIOSENTRY:";
                    if(message.length()==check.length()){
                        prevIndex = 0;
                        prevTerm = 0;
                    }
                    else{    
                       
                        System.out.println("[ThreadReceive] CheckPrevisiosentry->"+parts[1]);
                        prevIndex = Integer.parseInt(prev[1].trim());
                        prevTerm = Integer.parseInt(prev[2].trim());
                    }
                    int termLOG= this.log.lookForTerm(prevIndex);
//                    System.out.println("[ThreadReceive]: \n LAST INDEX do Log-"+lastEntry[0]+" Index Received do Leader-"+prevIndex
//                                +"\n LAST TERM do Log-"+lastEntry[1]+" TERM Received do Leader-"+prevTerm);
                    if(termLOG != prevTerm){
                        System.out.println("[ThreadReceive]Log Matching Property failed: lastEntryLeader: "+ termLOG + "prevTerm: " + prevTerm);
                        //reply false
                        String msgToSend = "ERROR_LOG@" + Integer.toString(States.term);
                        this.comModule.sendMessage(msgToSend, inet); 
                    }
                    //se não falhou a LogMatching Property mandar ACK
                    else{
                        String acknowledge = "ACK:" + Integer.toString(prevIndex) + "@" + Integer.toString(States.term);
                        this.comModule.sendMessage(acknowledge, inet);   
                        System.out.println("[Thread Receive] mandei :" + acknowledge);
                        this.log.deleteAfterIndex(prevIndex);
                        
                    }
                    System.out.println("[Thread Receive] recvd CHECK_PREVIOSENTRY. Msg: "+ message);
                }
                
                //mensagens ACK para saber quais os comandos commited
                // so lider executa este pedaço de codigo
                else if ( to.compareTo(States.myIP)==0 && message.contains("ACK") ){
                    int prevTerm = 1; //not used - don't care
                    int prevIndex = 1; //not used - don't care
                    Pair pair = new Pair(time, message, inet, term, prevIndex,prevTerm);
                    queue.add(pair);                  
                }
                //VAI ATUALIZAR LOGO OS LOGS
                else if ( to.compareTo(States.myIP)==0 && message.contains("RefreshLog") ){
                     
                     System.out.println("[ThreadReceive]:REFRESHLOG->"+parts[3]);
                     String commands[] = parts[3].split(":");
                     ///vem termo:command
                     int length=commands.length;
                     length=length/2;
                     int[] par = new int[length];
                     String[] impar=new String[length];
                     for(int i=0;i<length;i++){
                         par[i]=Integer.parseInt(commands[i*2].trim());
                         impar[i]=commands[(i*2+1)];
                        }
                     int prevIndex=this.log.writeLog(par, impar);
                     String acknowledge = "ACK:" + Integer.toString(prevIndex) + "@" + Integer.toString(States.term);
                     this.comModule.sendMessage(acknowledge, inet);   
                     
                     
                }
                    
                //mensagens Leader Election 
                else if ( to.compareTo("BROADCAST")==0 || to.compareTo(States.myIP)==0){
                                        
                    term = Integer.parseInt(parts[2].trim());
                    if(message.contains("ELECTION")||message.contains("HELLO")){
                        int prevTerm=Integer.parseInt(parts[3].trim());
                        int prevIndex=Integer.parseInt(parts[4].trim());
                        Pair pair = new Pair(time, message, inet, term, prevIndex,prevTerm);
                        queue.add(pair);
                    
                    }
                    else{
                        Pair pair = new Pair(time, message, inet, term, -1,-1);
                        queue.add(pair);
                    } 
                }
    
            }
   
        } catch (IOException ex) {
            Logger.getLogger(ThreadReceive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

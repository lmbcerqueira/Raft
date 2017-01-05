
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
    
    ThreadReceive(int port, InetAddress groupIP, ConcurrentLinkedQueue<Pair> queue, ConcurrentLinkedQueue<Pair> queueLOG, Log log){
        this.port = port;
        this.groupIP = groupIP;
        this.queue = queue;
        this.queueLOG = queueLOG;
        this.log = log;
    }
       
    public void run() {
        
        long time;
        MulticastSocket socket;
        InetAddress inet = null;
        //int term,prevLogTerm,prevLogIndex;
        
        
        String myIP = null;
        
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
                myIP = brinput.readLine();
                if (myIP != null)
                    break;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        
        //filter messages        
        try {
            
            System.out.println("MyIP:" + myIP);
            socket = new MulticastSocket(this.port);
            
            socket.joinGroup(groupIP);
            
            while(true){
                time=System.currentTimeMillis();
                byte[] buf = new byte[1024];
                
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);
                
                inet = pack.getAddress();
                String inetStr = inet.toString();
                String[] parts = inetStr.split("/");
                String senderIP = parts[1];
                
                byte[] bytes = pack.getData();
                String receivedPacket = new String(bytes); 
                //System.out.println("receivedPacket: " + receivedPacket);
                parts = receivedPacket.split("@");
                String to = parts[0];
                String message = parts[1];
                int term = Integer.parseInt(parts[2].trim());
                
                //Logs
                if ( to.compareTo("BROADCAST")==0 && message.contains("AppendEntry")){ 
                    //Se for lider NAO ADICIONAR na queue
                    if(senderIP.compareTo(myIP)!=0){
                        int prevTerm=Integer.parseInt(parts[3].trim());
                        int prevIndex=Integer.parseInt(parts[4].trim());
                        Pair pair = new Pair(time, message, inet, term, prevIndex,prevTerm);
                        queueLOG.add(pair);
                    }   
                }
                
                //update_logs
                if ( to.compareTo(myIP)==0 && message.contains("UPDATE_LOG"))
                    this.log.updateLog(message);                
                    
                //mensagens normais    
                else if ( to.compareTo("BROADCAST")==0 || to.compareTo(myIP)==0){
                                        
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

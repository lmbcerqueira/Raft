
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
    
    ThreadReceive(int port, InetAddress groupIP, ConcurrentLinkedQueue<Pair> queue){
        this.port = port;
        this.groupIP = groupIP;
        this.queue = queue;
    }
       
    public void run() {
        
        long time;
        MulticastSocket socket;
        InetAddress inet = null;
        int term;
        
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
        
        try {
            
            System.out.println("MyIP:" + myIP);
            socket = new MulticastSocket(this.port);
            
            socket.joinGroup(groupIP);
            
            while(true){
                
                byte[] buf = new byte[1024];
                
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                socket.receive(pack);
                
                inet = pack.getAddress();
                String inetStr = inet.toString();
                String[] parts = inetStr.split("/");
                String senderIP = parts[1];
                
                
                byte[] bytes = pack.getData();
                String messageAndTerm = new String(bytes); 
                parts = messageAndTerm.split("@");
                String to = parts[0];
                if ( to.compareTo("BROADCAST")==0 || to.compareTo(myIP)==0){
                    
                    String message = parts[1];
                    term = Integer.parseInt(parts[2].trim());
                    time = System.currentTimeMillis();
                    System.out.println("term="+term+" ; message="+message);
                    Pair pair = new Pair(time, message, inet, term);
                    queue.add(pair);
                }
            }
   
        } catch (IOException ex) {
            Logger.getLogger(ThreadReceive.class.getName()).log(Level.SEVERE, null, ex);
        }
              
    
  }
    

}

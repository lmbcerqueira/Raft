
package Node;

import java.io.IOException;
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
        try {
            socket = new MulticastSocket(this.port);
            
            socket.joinGroup(groupIP);
            
            while(true){
                byte[] buf = new byte[1024];
                DatagramPacket pack = new DatagramPacket(buf, buf.length);
                
                socket.receive(pack);
                
                byte[] bytes = pack.getData();
                String message = new String(bytes); 
                time=System.currentTimeMillis();
               
                Pair pair=new Pair(time,message);
                queue.add(pair);
            }
   
        } catch (IOException ex) {
            Logger.getLogger(ThreadReceive.class.getName()).log(Level.SEVERE, null, ex);
        }
              
    
  }
    

}


package Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

public class ThreadReceive implements Runnable {
    
    public void run () {
        
        while(true){
            System.out.println("receiver thread");
  
            byte[] buf = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buf, buf.length);
                   
            try {    
                
                //receive
                
                //calcular tempo
                
                //guardar na fifo
                
                s.receive(pack);

                byte[] bytes = pack.getData();
                String str = new String(bytes);

                return str;

            }catch(SocketTimeoutException e){
                return "ERROR";
            }
                  
    }
  }
}

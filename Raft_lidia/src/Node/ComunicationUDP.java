
package Node;

import java.io.IOException;
import java.net.*;
import java.util.Random;


class ComunicationUDP  {
    
    int port = 5000;
    String group = "225.4.5.6"; // Which address
        
    InetAddress serverIPAddress = InetAddress.getByName(group);
  
    MulticastSocket s;

    ComunicationUDP() throws IOException {
        this.s = new MulticastSocket(this.port);
    }
        
    public void ComunicationUDP() throws UnknownHostException, IOException {
       
        s.joinGroup(serverIPAddress); 

    }
    
    public String receiveData(int timeout) throws IOException{
        
        System.out.println("\n\n\n VAI RECEBER DADOS\n");
        
        byte[] buf = new byte[1024];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
            
        s.setSoTimeout(timeout);   // set the timeout in millisecounds.
        try {     
            s.receive(pack);
            
            byte[] bytes = pack.getData();
            String str = new String(bytes);
        
            return str;
            
        }catch(SocketTimeoutException e){
            return "ERROR";
        }
                  
    }
    
     public void sendData(String message) throws IOException {
            
        System.out.println("\n\n\n VAI MANDAR DADOS\n");      
       
        //prepare buffer to send
        byte[] sendData = new byte[message.length()];
        sendData = message.getBytes();
              
        DatagramPacket pack = new DatagramPacket(sendData, sendData.length, serverIPAddress, this.port);
        
        s.send(pack);
        System.out.println("\n ENVIADO\n"); 
            
    }

     public void leaveGroup() throws IOException{
        s.leaveGroup(serverIPAddress);
        s.close();
     }

}

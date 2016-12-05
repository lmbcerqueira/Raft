
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
    

    
     public void sendData(String message) throws IOException {
            
        System.out.println("send::::::VAI MANDAR DADOS");      
       
        //prepare buffer to send
        byte[] sendData = new byte[message.length()];
        sendData = message.getBytes();
              
        DatagramPacket pack = new DatagramPacket(sendData, sendData.length, serverIPAddress, this.port);
        
        s.send(pack);
        System.out.println("send::::::ENVIADO"); 
            
    }

     public void leaveGroup() throws IOException{
        s.leaveGroup(serverIPAddress);
        s.close();
     }

}

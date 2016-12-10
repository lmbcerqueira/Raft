
package Node;

import java.io.IOException;
import java.net.*;
import java.util.Random;


class ComunicationUDP  {
    
    int port = 5000;
    String group = "225.4.5.6"; // Which address
        
    InetAddress serverIPAddress=InetAddress.getByName(group);; 
  
    MulticastSocket s;

    ComunicationUDP() throws IOException {
        this.s = new MulticastSocket(this.port);
    }
        
    public void ComunicationUDP() throws  IOException {
        s.joinGroup(serverIPAddress); 
        
    }
    

    
    public void sendMessageBroadcast(String message) throws IOException { //SUGESTÃo: alterar para sendMessageMulticast
            
        System.out.println("send::::::VAI MANDAR DADOS___BROADCAST");      
       
        //prepare buffer to send
        byte[] sendData = new byte[message.length()];
        sendData = message.getBytes();
              
        DatagramPacket pack = new DatagramPacket(sendData, sendData.length, serverIPAddress, this.port);
        
        s.send(pack);
        System.out.println("send::::::ENVIADO_BROADCAST  ->"+message); 
            
    }
    
    public void sendMessage(String message, InetAddress inet) throws SocketException, IOException{
        DatagramSocket sendMessage = new DatagramSocket(this.port);
        serverIPAddress=inet;
        
        byte[] sendData = new byte[message.length()*8];//
        sendData=message.getBytes();
        
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,serverIPAddress,this.port);
        sendMessage.send(sendPacket);
        
    }

    public void leaveGroup() throws IOException{
        s.leaveGroup(serverIPAddress);
        s.close();
     }

}

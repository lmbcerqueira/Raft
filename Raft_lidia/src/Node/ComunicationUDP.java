
package Node;

import java.io.IOException;
import java.net.*;
import java.util.Random;


class ComunicationUDP  {
    
    int port = 5000;
    String group = "225.4.5.6"; // Which address
        
    InetAddress serverIPAddress=InetAddress.getByName(group);
  
    MulticastSocket s;

    ComunicationUDP() throws IOException {
        this.s = new MulticastSocket(this.port);
    }
        
    public void ComunicationUDP() throws  IOException {
        this.s.joinGroup(serverIPAddress); 
        
    }
    

    
    public void sendMessageBroadcast(String message) throws IOException { //SUGESTÃo: alterar para sendMessageMulticast
        
        message = "BROADCAST@" + message;
        //prepare buffer to send
        byte[] sendData = new byte[message.length()];
        sendData = message.getBytes();
              
        DatagramPacket pack = new DatagramPacket(sendData, sendData.length, serverIPAddress, this.port);
        
        
        this.s.send(pack);
        if (message.contains("AppendEntry"))
            System.out.println("COMM send: BROADCAST  -> " + message); 
            
    }
    
    public void sendMessage(String message, InetAddress inet) throws SocketException, IOException{
        
        String inetStr = inet.toString();
        String[] parts = inetStr.split("/");
        String destinationIP = parts[1]; 
                
        message = destinationIP +  "@" + message;
        byte[] sendData = new byte[message.length()*8];
        sendData = message.getBytes();
        
        DatagramPacket pack = new DatagramPacket(sendData, sendData.length, serverIPAddress, this.port);
        this.s.send(pack);

        //System.out.println("COM send uni" + message);
        
    }

    public void leaveGroup() throws IOException{
        this.s.leaveGroup(serverIPAddress);
        this.s.close();
     }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

import java.io.IOException;
import java.net.*;
import java.util.Random;

/**
 *
 * @author joaqu
 */
class ComunicationUDP  {
    int port = 5000;
    // Which address
    String group = "225.4.5.6";
        
    InetAddress serverIPAddress;
  
    MulticastSocket s;

    ComunicationUDP() throws IOException {
        this.s = new MulticastSocket(this.port);
    }
        
    public void ComunicationUDP() throws UnknownHostException, IOException {
       this.serverIPAddress = InetAddress.getByName(this.group);
        
    }
    
    public String receiveData() throws IOException{
        System.out.println("\n\n\n VAI RECEBER DADOS\n");
        
        byte[] buf = new byte[1024];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        Random ran;
        ran = new Random(); 
        
        int randomNumber = ran.nextInt(6)+5;
        System.out.println(randomNumber);
        
        
        
        s.setSoTimeout(randomNumber);   // set the timeout in millisecounds.
        try {     
            s.receive(pack);
        }catch(SocketTimeoutException e){
            return "ERROR";
        }
            
        byte[] bytes = pack.getData();
        String str = new String(bytes);
        
        return str;
        
    }
    
     public void sendData(String message) throws IOException {
         
         
        s.joinGroup(serverIPAddress);
        System.out.println("\n\n\n VAI MANDAR DADOS\n");      
       
        //prepare buffer to send
        byte[] sendData = new byte[message.length()];//
        sendData=message.getBytes();
        
        DatagramPacket pack = new DatagramPacket(sendData,sendData.length,serverIPAddress,this.port);
        System.out.println("\n ENVIADO\n"); 
        s.send(pack);
    }

     public void leaveGroup() throws IOException{
        s.leaveGroup(serverIPAddress);
        s.close();
     }

}

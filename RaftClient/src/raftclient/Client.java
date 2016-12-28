
package raftclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    
    int port;
    String group;
    InetAddress serverIPAddress;
    MulticastSocket multicastSocket;
    
    public Client(int port, String group) throws UnknownHostException, IOException {
        this.port = port;
        this.group = group;
        this.serverIPAddress = InetAddress.getByName(this.group);
        this.multicastSocket = new MulticastSocket(this.port);
    } 
    
    public void execute() throws IOException{
        
        joinGroup();
        
        //init timer
        Timer timer = new Timer();
        timer.schedule(new Client.sendCommand(),100, 10000);         
        
        while(true){
            
        }
    }

    public void joinGroup() throws IOException{
        
        this.multicastSocket.joinGroup(this.serverIPAddress); 
        
    }

    public char getCommand(){
        
        Random r = new Random();
        return (char)(r.nextInt(26) + 'a');
        
    }
    
    public void sendCommandToLeader(char command) throws IOException{
        
        String message = "BROADCAST@COMMAND:" + command + "@1"; // 1 (fake term)
        
        //prepare buffer to send
        byte[] sendData = new byte[message.length()];
        sendData = message.getBytes();
              
        DatagramPacket pack = new DatagramPacket(sendData, sendData.length, this.serverIPAddress, this.port);
        
        this.multicastSocket.send(pack);
        
        System.out.println("[CLIENT] Command " + command + " sent");
        
    }
    
        //TIMER
    class sendCommand extends TimerTask  {

        @Override
        public void run() {
            
            char command = getCommand();
                        
            try {
                sendCommandToLeader(command);
            } catch (IOException ex) {
                Logger.getLogger(RaftClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}

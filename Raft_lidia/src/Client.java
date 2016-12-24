
import Node.Leader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    


    public void main(String[] args) throws UnknownHostException {
        

       
        Timer timer = new Timer();
        timer.schedule(new Client.sendCommand(),100, 1000000);  
       
    }
    
    
    public char getCommand(){
        
        Random r = new Random();
        return (char)(r.nextInt(26) + 'a');
        
    }
    
        //TIMER
    class sendCommand extends TimerTask  {

        @Override
        public void run() {
            
            getCommand();
            
        }
    }
    
}

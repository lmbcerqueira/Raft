/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

import java.io.IOException;

public class Leader {
    
    private final ComunicationUDP comModule;
    private final long timeout;
    

    public Leader() throws IOException {
        this.comModule = new ComunicationUDP();
        this.timeout=this.getHeartBeat();
    }
    
    public int getHeartBeat(){
                         
        int min_value = 1000;
        int max_value = 1100;
        
        return min_value + (int)(Math.random() * ((max_value - min_value) + 1));
    }
    
    public void sendHeartBeat(int term) throws IOException{
        
        String heartBeatString;
        heartBeatString = "HELLO@"+Integer.toString(term);
        
        this.comModule.sendMessageBroadcast(heartBeatString); //com que frequência?????
    }

    public String cycle(long timeStart, int term) {
        
        return "ola";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

import java.io.IOException;

/**
 *
 * @author joaqu
 */
public class Follower  {
    private final ComunicationUDP comModule;

    public Follower() throws IOException {
        this.comModule = new ComunicationUDP();
  
        
    }
    
    public String receiver() throws IOException{
        
        String receive;
        while(true){
            receive=this.comModule.receiveData();
            if(receive.contains("HELLO"))
                continue;
            else if(receive.contains("ERROR"))
                return "ERROR";
            else 
                return "ERROR";
        }
        
            
        
    }
    
}

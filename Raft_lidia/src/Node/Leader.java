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
public class Leader {
    private final ComunicationUDP comModule;

    public Leader() throws IOException {
        this.comModule = new ComunicationUDP();
    }
    public void sendHeartBeat() throws IOException{
        String heartBeatString;
        heartBeatString="HELLO";
        this.comModule.sendData(heartBeatString);
    }
}

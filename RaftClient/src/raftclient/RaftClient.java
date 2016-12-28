
package raftclient;

import java.io.IOException;


public class RaftClient {
    
    public static void main(String[] args) throws IOException{
        
        int port = 5000;
        String group = "225.4.5.6";
        
        Client client = new Client(port, group);
        client.execute();
    }
    
}


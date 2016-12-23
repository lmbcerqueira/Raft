package Node;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

class DataProcessing {
    private final ConcurrentLinkedQueue<Pair> queue;

    public DataProcessing(ConcurrentLinkedQueue<Pair> queue) {
        this.queue = queue;
    }
    
    public boolean contains(String message){
        return this.queue.peek().getMessage().contains(message);
    } 
    
    public boolean isReceivedTermUPdated(int term){
        if(this.queue.peek().getTerm()<term)
            return false;
        else
            return true;
    }    
}


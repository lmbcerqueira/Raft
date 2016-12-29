
package Node;

import java.net.InetAddress;

public class Pair {
    private final long time;
    private final String message;
    private final InetAddress inet;
    private final int term;
    private final int prevLogIndex;
    private final int prevLogTerm;
    //private final String command;

    public Pair(long time, String message, InetAddress inet, int term, int prevLogIndex, int prevLogTerm) {
        this.time = time;
        this.message = message;
        this.inet = inet;
        this.term = term;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
    }

    public int getPrevLogIndex() {
        return prevLogIndex;
    }

    public int getPrevLogTerm() {
        return prevLogTerm;
    }

    
    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public int getTerm() {
        return term;
    }

    public InetAddress getInet() {
        return inet;
    }
    
    /*public String getCommand() {
        return command;
    } */   
    
    
   
}

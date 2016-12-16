
package Node;

import java.net.InetAddress;

public class Pair {
    private final long time;
    private final String message;
    private final InetAddress inet;
    private final int term;

    public Pair(long time, String message, InetAddress inet, int term) {
        this.time = time;
        this.message = message;
        this.inet=inet;
        this.term=term;
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
    
    
   
}

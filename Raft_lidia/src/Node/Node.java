
package Node;

import java.io.IOException;

public class Node {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        String id = args[0];
        States state = new States();
        state.mainCycle(id);
        
    }
    
}

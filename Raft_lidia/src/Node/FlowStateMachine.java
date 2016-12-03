/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

/**
 *
 * @author joaqu
 */
public class FlowStateMachine {

    /**
     *
     */
    public final int follower=1;
    public final int candidate=2;
    public final int leader=3;
    public int fsm;
    
    public void setFollower(){
        this.fsm=this.follower;
       
    }
    public void setCandidate(){
        this.fsm=this.candidate;
    }
    public void setLeader(){
        this.fsm=this.leader;
    }
    public int getStateMachine(){
        return this.fsm;
    }
   
    
}

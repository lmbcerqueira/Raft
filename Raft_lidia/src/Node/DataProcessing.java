/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author joaqu
 */
class DataProcessing {
    private final int numeroNos=3;
    private final long timeOutFollower;
    private final long timeOutCandidate;
    private final ConcurrentLinkedQueue<Pair> queue;

    public DataProcessing(long timeOutFollower, long timeOutCandidate, ConcurrentLinkedQueue<Pair> queue) {
        this.timeOutFollower = timeOutFollower/100;
        this.timeOutCandidate = timeOutCandidate/100;
        this.queue = queue;
    }
    private boolean contains(String message){
        return this.queue.peek().getMessage().contains(message);
    } 
    
    public String checkHeartBeatsCandidate(long timeStart){
        //A ESPERA DE HEARTBEATS: RECEBE HEART BEATS OU NAO E ELECTION
        //RECEBE HEART BEATS RESPONDE COM HEARTBEATS SENAO MANDA ELECTION
        //RECEBE ELECTION MANDA ANSWER
        String message="HELLO";
        System.out.println("TIMEOUT="+timeOutFollower);
        while(true){
            long x=System.currentTimeMillis()-timeStart;
            float xSeconds=x/1000F; //time in seconds
            if(xSeconds>timeOutFollower){
                System.out.println("Ja passou :"+xSeconds);
                return "ELECTION";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            else if(contains("HELLO")){
                this.queue.poll();
                return "HEARTBEATS";
            }
            else if(contains("ELECTION")){
                this.queue.poll();
                return "ANSWER";
            }
            else if(!(contains("HELLO")||contains("ELECTION"))){
                this.queue.poll();
            }     
            

        }
        
//        while(this.queue.isEmpty()||this.queue.peek().getTime()<timeStart){
//            long x=System.currentTimeMillis()-timeStart;
//            if(x>timeOutFollower)
//                return false;
//            if(this.queue.isEmpty())
//                continue;
//               // System.out.println("FIFO VAZIA, timeout="+timeOutFollower+"  cuurentTime-timestart="+x);
//            else if(this.queue.peek().getTime()<timeStart){
//                this.queue.poll();
//               // System.out.println("FIFO VALORES ERRADOS, timeout="+timeOutFollower+"  cuurentTime-timestart="+x);
//            }  
//            else
//                continue;
//                //System.out.println("FIFO VALORES CERTOS, timeout="+timeOutFollower+"  cuurentTime-timestart="+x);
//        }
//        
////        do{
////            while(this.queue.peek().getTime()<timeStart)
////                this.queue.poll();
////            System.out.println("FIFO COM VALORES ERRADOS");
////        }while(!this.queue.isEmpty());
//        
//        
//        
//        
//        while(!this.queue.isEmpty()){
//           if(this.queue.poll().getMessage().contains(message)){
//               System.out.println("TINHA UM HELLO");
//               return true; 
//            }
//        }
//        return false;
//        
        
//        if(!this.queue.isEmpty()){
//            while(this.queue.peek().getTime()<timeStart){
//
//                this.queue.poll();
//                
//                if(this.queue.isEmpty())
//                    break;
//            }
//        }
//        
//        //{
//            do{
//                if(this.queue.isEmpty()){
//                    if(System.currentTimeMillis()-timeStart>timeOutFollower)
//                        return false;
//                    else
//                        continue;
//                }
//                else if(this.queue.poll().getMessage().contains(message)){
//                    
//                    return true;
//                }
//            }while(this.queue.peek().getTime()-timeStart<timeOutFollower);
        //}

    }

    String resultElections(long timeStart) {
        int votes = 0;
        System.out.println("TIMEOUT="+timeOutCandidate);
        while(true){
            long x=System.currentTimeMillis()-timeStart;
            float xSeconds=x/1000F; //time in seconds
            if(xSeconds>timeOutCandidate){
                System.out.println("Ja passou :"+xSeconds);
                votes=0;
                return "tryAGAIN";
            }
            else if(this.queue.isEmpty());
                
            else if(this.queue.peek().getTime()<timeStart)
                this.queue.poll();
            else if(contains("ACCEPTED")){
                votes++;
                this.queue.poll();
                if(votes>(this.numeroNos/2)){
                    votes=0;
                    return "ACCEPTED";
                }
            }
            else if(contains("REJECTED")){
                this.queue.poll();
                votes=0;
                return "REJECTED";
            }
            else if(!(contains("ACCEPTED")||contains("REJECTED"))){
                this.queue.poll();
            }  
            
            
        }
        

    }
    
}


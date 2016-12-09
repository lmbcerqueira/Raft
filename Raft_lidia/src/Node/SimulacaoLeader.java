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
public class SimulacaoLeader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Leader leader=new Leader();
        long tempoHeartBeat=leader.getHeartBeat();
        long tempoInicial=System.currentTimeMillis();
        while(true){
            long tempoAtual=System.currentTimeMillis();
            //System.out.println("tempo="+tempoInicial);
            if(tempoAtual-tempoInicial>tempoHeartBeat){
                tempoInicial=tempoAtual;
                leader.sendHeartBeat(1);//TERM 1---ALTERAR
            }
        }
            
    }
    
}

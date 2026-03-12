/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.yourcompany.bilheteria;

/**
 *
 * @author kelly
 */
public class Bilheteria {
    static int count = 30;

    public static void main(String[] args){
        // Runnable é a interface
        Runnable r = new Runnable(){
            @Override
            public void run(){
                System.out.println("Entrou na thread");
              
                    if (count > 0){
                        System.out.print("Comprou ingresso");
                        count--;
                    }
                    else{
                        System.out.print("Ingressos esgotados!");
                    }
                    count++;
                    
            }
            
        };
        System.out.println("Iniciando BIlheteria");
        var builder = Thread.ofPlatform().daemon().name("thread", 1).start(r);
        System.out.println("Terminou");
        System.out.println(count);
    }

}

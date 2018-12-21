/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utente
 */
public class GamestopApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Game g = new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human");
            System.out.println( g.toString() );
            
            g = new Game("https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition");
            System.out.println( g.toString() );
            
            // to implement "CONTENUTO DIGITALE"
            g = new Game("https://www.gamestop.it/XboxONE/Games/112463/forza-horizon-4-deluxe-edition");
            System.out.println( g.toString() );
            
            g = new Game("https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition");
            System.out.println( g.toString() );
            
            
        } catch (IOException ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

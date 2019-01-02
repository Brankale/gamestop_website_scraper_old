package gamestopapp;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamestopApp {

    public static void main(String[] args) {
        try {
            
            long start = System.currentTimeMillis();
            
            Game g = new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human");
            System.out.println( g.toString() );
            
            
            g = new Game("https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition");
            System.out.println( g.toString() );
            
            // to implement "CONTENUTO DIGITALE"
            g = new Game("https://www.gamestop.it/XboxONE/Games/112463/forza-horizon-4-deluxe-edition");
            System.out.println( g.toString() );
            
            g = new Game("https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition");
            System.out.println( g.toString() );
            
            g = new Game("https://www.gamestop.it/PS4/Games/99826");
            System.out.println( g.toString() );
            
            g = new Game("https://www.gamestop.it/PS4/Games/34052/gta-v");
            System.out.println( g.toString() );
            
            long finish = System.currentTimeMillis();
            
            System.out.println( (finish-start) + "ms" );
            
            
            
            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.print("\n\n\nInserisci nome gioco: ");
                String gameName = in.nextLine();
                
                for ( GamePreview game : Game.searchGame(gameName) ) {
                    System.out.println(game);
                }
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

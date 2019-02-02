package gamestopapp;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamestopApp {
    
    public static void main(String[] args) {
        try {
            
            long ms = System.currentTimeMillis();
            
            //Games wishlist = new Games();
            Games wishlist = Games.importXML("games.xml");
            /*
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human") );
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human") );    
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition") );
            wishlist.add( new Game("https://www.gamestop.it/XboxONE/Games/112463/forza-horizon-4-deluxe-edition") );
            wishlist.add( new Game("https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition") );
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/99826") );
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/34052/gta-v") );
            */
            
            
            
            ms = System.currentTimeMillis() - ms;
            System.out.println( ms + "ms" );
            
            System.out.println( wishlist.toString() );
            
            /*
            try{
                wishlist.exportXML("games.xml");
            }catch(Exception e){
                e.printStackTrace();
            }*/
            
            
            //System.out.println( wishlist.toString() );            
            //wishlist.exportToBinary();
            
            //Games temp = Games.importFromBinary();
            //System.out.println( temp.toString() );
            

            /*
            
            // this is the searchBar
            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.print("Inserisci nome gioco: ");
                String gameName = in.nextLine();
                
                System.out.println( GamePreview.toString( GamePreview.searchGame(gameName) ) );;
                System.out.println("\n\n\n");
            }

            */
            
        } catch (Exception ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

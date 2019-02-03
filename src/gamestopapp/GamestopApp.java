package gamestopapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamestopApp {

    public static void main(String[] args) {
        try {
            
            /*
            long ms = System.currentTimeMillis();
            
            Games wishlist = new Games();
            
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human") );
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human") );    
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition") );
            wishlist.add( new Game("https://www.gamestop.it/XboxONE/Games/112463/forza-horizon-4-deluxe-edition") );
            wishlist.add( new Game("https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition") );
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/99826") );
            wishlist.add( new Game("https://www.gamestop.it/PS4/Games/34052/gta-v") );
            
            ms = System.currentTimeMillis() - ms;
            System.out.println( ms + "ms" );
            */

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
            
            /*
            Game gh = new Game("https://www.gamestop.it/TELEFONIA/Games/32910/samsung-galaxy-tab-3-8-0-wi-fi-16gb");            
            return;
            */
            
            BufferedReader reader = new BufferedReader( new FileReader("gamelist.txt") );
            Games wishlist = new Games();
            Game g = null;
            
            String game = reader.readLine();
            while ( game != null )
            {
                
                Log.debug("Game searched", game);
                for ( GamePreview gp : GamePreview.searchGame(game) ) {
                    g = new Game( gp.getUrl() );
                    wishlist.add(g);
                }
                
                game = reader.readLine();
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

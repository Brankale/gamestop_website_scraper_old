package gamestopapp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

public class GamestopApp {

    public static void main(String[] args) throws IOException, Exception {        
        
        // WRITE HERE THE TEST CODE
        
        DirectoryManager.deleteAllGames();
        
        
        Games games = new Games();
        
        for ( int i=100000; i<100050; ++i ){
            Game game = downloadGame(""+i);
            //games.add(game);
            System.out.println("");
        }
        
        DirectoryManager.exportGames(games);
        //DirectoryManager.deleteTemporaryFiles(games);
    }
    
    /**
     * Scarica un gioco dal sito di Gamestop
     * @param id - id del gioco
     * @return
     * Game - se tutto è andato correttamente
     * null - se ci sono stati errori
     */
    public static Game downloadGame ( String id ) {
        
        try {
            Log.info("Main", "Downloading Game [ID=" + id + "]" );
            Game game = new Game( Game.getURLbyID(id) );
            return game;
        } catch ( SSLException ex ) {
            Log.error("Main", "Connection time expired");
        } catch ( IOException ex ) {
            Log.error("Main", "Error during connection");
        } catch ( IsNotAGameException ex ) {
            Log.warning("Main", ex.toString() );
        } catch ( GameException ex ) {
            Log.error("Main", "Game creation failed");
        } catch ( Exception ex ){
            System.out.println("Fatal Error");
            Log.crash(ex, "Game ID: " + id);
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }       
        
        return null;        
    }
    
}

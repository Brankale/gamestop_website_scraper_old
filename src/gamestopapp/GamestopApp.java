package gamestopapp;

import DataTypes.Game;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

public class GamestopApp {

    public static void main(String[] args) throws IOException {        
        // WRITE HERE THE TEST CODE
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
            Log.info("Main", "Downloading Game [ID=" + id + "] ..." );
            Game game = new Game( Game.getURLbyID(id) );
            return game;
        } catch ( SSLException ex ) {
            Log.error("Main", "Tempo di connessione scaduto");
        } catch ( IOException ex ) {
            Log.error("Main", "Errore durante la connessione");
        } catch ( IsNotAGameException ex ) {
            Log.warning("Main", ex.toString() );
        } catch ( GameException ex ) {
            Log.error("Main", "Creazione del gioco fallita");
        } catch ( Exception ex ){
            System.out.println("Fatal Error");
            Log.crash(ex, "Game ID: " + id);
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return null;        
    }
    
}

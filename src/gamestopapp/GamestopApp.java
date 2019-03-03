package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

public class GamestopApp {

    public static void main(String[] args) {       
        
        downloadAll();
        
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
            Game game = new Game( Game.getURLByID(id) );
            return game;
        } catch ( SSLException ex ) {
            Log.error("Main", "Tempo di connessione scaduto");
        } catch ( IOException ex ) {
            Log.error("Main", "Errore durante la connessione");
        } catch ( GameException ex ) {
            Log.error("Main", "Creazione del gioco fallita");
        } catch ( Exception ex ){
            System.out.println("Fatal Error");
            Log.crash(ex, "Game ID: " + id);
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return null;        
    }
    
    public static Games importAll () {
        
        Games games = new Games();
        
        File f = new File("userData/");
        
        if ( f.exists() ){
            File [] files = f.listFiles();
            
            for ( int i=0; i<files.length; ++i ){
                if ( files[i].isDirectory() ){
                    
                    File info = new File ( files[i].getPath() + "/data.dat" );
                    
                    if ( info.exists() ){
                        try {
                            System.out.println( info.getCanonicalPath() );
                            games.add( Game.importBinary( info.getPath() ) );
                        } catch (IOException ex) {
                            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
            }
        }
        
        return games;        
    }
    
    public static void downloadAll () {
                    
        for ( int id=100000; id<200000; ++id )
        {                
            Game game = downloadGame(""+id);
            
            /*
            try {
                if ( game != null )
                    game.exportBinary();
            } catch (IOException ex) {
                Log.error("Main", "failed to export to binary");
            }
            */
        }
        
    }
    
}

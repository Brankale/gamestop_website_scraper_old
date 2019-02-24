package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

public class GamestopApp {

    public static void main(String[] args) {       
        
        /*
        String url = "https://www.gamestop.it/XboxONE/Games/107940/code-vein#";

        try {
        Game game = new Game(url);
        System.out.println(game);
        } catch ( SSLException ex ) {
        System.out.println("Tempo di connessione scaduto");
        } catch ( IOException ex ) {
        System.out.println("Errore durante la connessione");
        } catch ( GameException ex ) {
        System.out.println("Creazione del gioco fallita");
        Log.crash(ex, url);
        }
        */
        
        // comment this to fix errors
        downloadAll();
        
        // use this to fix errors
        try {    
            Game g = new Game( Game.getURLByID(100130) );
        } catch (IOException ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
        
        Game game = null;
            
        for ( int id=100000; id<200000; ++id )
        {                
            try {
                Log.info("Main", "Downloading Game [ID=" + id + "] ..." );
                game = new Game( Game.getURLByID(id) );                    
            } catch ( SSLException ex ) {
                Log.error("Main", "Tempo di connessione scaduto");
            } catch ( IOException ex ) {
                Log.error("Main", "Errore durante la connessione");
            } catch ( GameException ex ) {
                Log.error("Main", "Creazione del gioco fallita");
            } catch ( Exception ex ){
                System.out.println("Fatal Error");
                Log.crash(ex, "Game ID: " + id);
            }

            try {
                if ( game != null )
                    game.exportBinary();
            } catch (IOException ex) {
                Log.error("Main", "failed to export to binary");
            }
        }
        
    }
    
}

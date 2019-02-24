/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author android
 */
public class GamestopApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*
        String url = "https://www.gamestop.it/PS4/Games/110143/detroit-become-human";
        url = "https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition";
        url = "https://www.gamestop.it/Switch/Games/103312/nintendo-switch-color-neon";
        
        try {
            Game g = new Game(url);
        } catch (IOException ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        //downloadAll();
        
        
        
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
        
        
        
        /*
        Games games = importAll();        
        System.out.println( games.toString() );
        */
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
        
        try {
            
            for ( int i=0; i<200000; ++i )
            {                
                try {
                    Log.info("GamestopApp", "Downloading Game [ID=" + i + "] ..." );
                    Game game = new Game( "http://www.gamestop.it/Platform/Games/"+i );
                    game.exportBinary();
                } catch (Exception ex) {
                    Log.error("GamestopApp", "Impossibile scaricare gioco");
                }
            }
            
                            
        } catch (Exception ex) {
            Log.crash(ex, "Fatal Error");
        }
        
    }
    
}

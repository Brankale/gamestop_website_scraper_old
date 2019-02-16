/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

/**
 *
 * @author android
 */
public class GamestopApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String url = "https://www.gamestop.it/PS4/Games/110143/detroit-become-human";
        url = "https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition";
        url = "https://www.gamestop.it/Switch/Games/103312/nintendo-switch-color-neon";
        
        try {
            
            for ( int i=0; i<200000; ++i ){
                
                try {
                    Log.info("GamestopApp", "Downloading Game [ID=" + i + "] ..." );
                    Game g1 = new Game( "http://www.gamestop.it/Platform/Games/"+i );
                } catch (Exception ex) {
                    Log.error("GamestopApp", "C'è stato un errore grave");
                }
            }
            
                            
        } catch (Exception ex) {
            Log.crash(ex, url);
        }
        
        
    }
    
}

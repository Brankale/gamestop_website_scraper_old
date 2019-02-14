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
            
            Game g1 = new Game(url);
            System.out.println( g1.toString() );
                            
        } catch (Exception ex) {
            Log.crash(ex, url);
        }
        
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

import java.util.Comparator;

/**
 *
 * @author android
 */
public class GamePlatformComparator implements Comparator<Game> {

    @Override
    public int compare(Game game1, Game game2) {
        return game1.getPlatform().compareTo( game2.getPlatform() );
    }
    
}

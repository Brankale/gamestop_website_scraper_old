package gamestopapp;

import java.util.Comparator;

public class GamePlatformComparator implements Comparator<Game> {

    @Override
    public int compare(Game game1, Game game2) {
        return game1.getPlatform().compareTo( game2.getPlatform() );
    }
    
}

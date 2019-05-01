package gamestopapp;

import java.util.Comparator;

public class GamePlatformComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {
        return game1.getPlatform().compareToIgnoreCase(game2.getPlatform());
    }
    
}

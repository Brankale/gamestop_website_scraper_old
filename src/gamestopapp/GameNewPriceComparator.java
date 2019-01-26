package gamestopapp;

import java.util.Comparator;

public class GameNewPriceComparator implements Comparator<Game> {

    @Override
    public int compare(Game game1, Game game2) {
        return ((Double) game1.getNewPrice() ).compareTo( game2.getNewPrice() );
    }
    
}

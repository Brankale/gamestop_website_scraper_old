package gamestopapp;

import java.util.Comparator;

public class GameUsedPriceComparator implements Comparator<Game> {

    @Override
    public int compare(Game game1, Game game2) {
        return ( (Double) game1.getUsedPrice() ).compareTo( game2.getUsedPrice() );
    }
    
}

package gamestopapp;

import java.util.Comparator;

public class GameNewPriceComparator implements Comparator<Game> {

    @Override
    public int compare(Game game1, Game game2) {
        
        // Games with no new price go at the bottom of the list
        if ( game1.getNewPrice() == -1 )
            return 1;
        
        // Games with no new price go at the bottom of the list
        if ( game2.getNewPrice() == -1 )
            return -1;
        
        return ( game1.getNewPrice() ).compareTo( game2.getNewPrice() );
    }
    
}

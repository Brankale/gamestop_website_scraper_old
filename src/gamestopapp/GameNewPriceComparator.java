package gamestopapp;

import java.util.Comparator;

public class GameNewPriceComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {
        
        // Games with no new price go at the bottom of the list
        if ( !game1.hasNewPrice() )
            return 1;
        
        // Games with no new price go at the bottom of the list
        if ( !game2.hasNewPrice() )
            return -1;
        
        return ( game1.getNewPrice() ).compareTo( game2.getNewPrice() );
    }
    
}

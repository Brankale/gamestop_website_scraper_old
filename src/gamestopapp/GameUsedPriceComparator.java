package gamestopapp;

import java.util.Comparator;

public class GameUsedPriceComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {
        
        // Games with no used price go at the bottom of the list
        if ( !game1.hasUsedPrice() )
            return 1;
        
        // Games with no used price go at the bottom of the list
        if ( !game2.hasUsedPrice() )
            return -1;
        
        return ( game1.getUsedPrice() ).compareTo( game2.getUsedPrice() );
    }
    
}

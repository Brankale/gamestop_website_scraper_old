package gamestopapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class GameReleaseDateComparator implements Comparator<GamePreview> {

    @Override
    public int compare(GamePreview game1, GamePreview game2) {
        
        if ( !game1.hasReleaseDate() ){
            return 1;
        }
        
        if ( !game2.hasReleaseDate() ){
            return -1;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
            
            Date d1 = sdf.parse( game1.getReleaseDate() );
            Date d2 = sdf.parse( game2.getReleaseDate() );
            return d1.compareTo(d2);
            
        } catch (ParseException pe) {
            Log.error( "GameReleaseDateComparator", "wrong date format", game1.getId() + " or " + game2.getId() );
            return 0;
        }        
        
    }
    
}

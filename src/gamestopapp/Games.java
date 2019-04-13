package gamestopapp;

import java.util.ArrayList;
import java.util.Collections;

public class Games extends ArrayList<Game> {
    
    @Override
    public boolean add ( Game game ) {
        
        if ( game == null ){
            Log.info("Games", "impossible to add the game to the list. The object is null");
            return false;
        }
        
        for ( Game g : this ){
            if ( g.equals(game) ){
                Log.warning("Games", "the game already exist", game.getTitle() );
                return false;
            }
        }
        
        super.add(game);
        Log.info("Games", "game added", game.getPlatform() + ": " + game.getTitle() );
        
        return true;
    }
    
    @Override
    public String toString () {
        String str = new String();
        for( int game=0; game<this.size(); ++game ){
            str += this.get(game).toString()+"\n\n";
        }
        return str;
    }
    
    public void sortbyName () {
        Collections.sort( this );
    }
    
    public void sortByPlatform () {
        Collections.sort( this, new GamePlatformComparator() );
    }
    
    public void sortByNewPrice () {
        Collections.sort( this, new GameNewPriceComparator() );
    }
    
    public void sortByUsedPrice () {
        Collections.sort( this, new GameUsedPriceComparator() );
    }
    
    public void sortByReleaseDate () {
        Collections.sort( this, new GameReleaseDateComparator() );
    }
    
}

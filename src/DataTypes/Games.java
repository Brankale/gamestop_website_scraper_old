package DataTypes;

import gamestopapp.DirectoryManager;
import gamestopapp.GameNewPriceComparator;
import gamestopapp.GamePlatformComparator;
import gamestopapp.GameReleaseDateComparator;
import gamestopapp.GameUsedPriceComparator;
import gamestopapp.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class Games extends ArrayList<Game> {
    
    @Override
    public boolean add ( Game game ) {
        
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
    
    public void exportGames() throws ParserConfigurationException, TransformerException, SAXException, IOException{
        File f = new File(DirectoryManager.WISHLIST_DIR+"data.csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        for(int i = 0; i<this.size(); i++){
            this.get(i).exportXML();
            bw.write(this.get(i).getId());
            if(i < (this.size()-1)){
                bw.write(",");
            }
        }
        bw.close();
    }
    
    public static Games importGames() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException{
        Games g = new Games();
        File f = new File(DirectoryManager.WISHLIST_DIR+"data.csv");
        BufferedReader br = new BufferedReader(new FileReader(f));
        
        String row = br.readLine();
        String[] ids = row.split(",");
        
        for(String id : ids){
            g.add(Game.importXML(id));
        }
        
        return g;
    }
    
}

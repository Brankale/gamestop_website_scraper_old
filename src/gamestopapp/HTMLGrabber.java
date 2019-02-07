package gamestopapp;

import java.io.IOException;
import java.util.List;

public class HTMLGrabber extends Thread {
    
    List<String> gamelist;
    int startIndex;
    int stopIndex;

    public HTMLGrabber(List<String> gamelist, int startIndex, int stopIndex) {
        this.gamelist = gamelist;
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
    }
    
    @Override
    public void run ()
    {        
        for ( int i=startIndex; i<stopIndex; ++i )
        {            
            try {
                List<GamePreview> gp = GamePreview.searchGame(gamelist.get(i));
                for ( GamePreview g : gp ){
                    GameTemp game = new GameTemp( g.getUrl() );
                }
            } catch (IOException ex) {
                Log.error("HTMLGrabber","Search failed", gamelist.get(i));
            }
        }
    }
    
}
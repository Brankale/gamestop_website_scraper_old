package gamestopapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GamestopApp {

    public static void main(String[] args) throws Exception {
        
        /*
        List<GameTemp> wishlist = new ArrayList<>();
        
        long ms = System.currentTimeMillis();
        
        wishlist.add( new GameTemp("https://www.gamestop.it/PS4/Games/110143/detroit-become-human") );
        wishlist.add( new GameTemp("https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition") );
        wishlist.add( new GameTemp("https://www.gamestop.it/XboxONE/Games/112463/forza-horizon-4-deluxe-edition") );
        wishlist.add( new GameTemp("https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition") );
        wishlist.add( new GameTemp("https://www.gamestop.it/PS4/Games/99826") );
        wishlist.add( new GameTemp("https://www.gamestop.it/PS4/Games/34052/gta-v") );
        
        System.out.println( System.currentTimeMillis()-ms + "ms" );
*/
        
        List<String> gamelist = new ArrayList<>();
            
            // Open the file
            BufferedReader reader = new BufferedReader( new FileReader("gamelist.txt") );
            
            // init gamelist
            String str = reader.readLine();
            while ( str != null ){
                gamelist.add(str);
                str = reader.readLine();
            }
            
            // close the file
            reader.close();
            
            
            int numThread = 20;            
            Thread[] threads = new Thread[numThread];   // brutto ma chi se ne frega
            
            for ( int i=0; i<threads.length; ++i ){
                int min = (gamelist.size()/numThread) * i;
                int max = (gamelist.size()/numThread) * (i+1);
                threads[i] = new HTMLGrabber(gamelist, min, max);
                threads[i].start();
            }
            
            for ( int i=0; i<threads.length; ++i ){
                threads[i].join();
}
        
    }
    
}

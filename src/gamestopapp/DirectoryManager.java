package gamestopapp;

import java.io.File;

public class DirectoryManager {

    public static final String TEMP_DIR = "tmp/";               // the temporary folder for the app
    public static final String WISHLIST_DIR = "userData/";      // the folder of the saved games
    
    /**
     * @param id id of the game
     * @return  TEMP_DIR if the game is not in the wishlsit
     *          WISHLIST_DIR if the game is in the wishlist
     */
    public static String getDirectory( String id ) {  
        
        File file = new File(WISHLIST_DIR);
        
        // check if the WISHLIST_DIR exists
        if ( file.exists() ) {
            
            File[] directories = file.listFiles();
            
            // search for the directory with the same ID
            for ( int i=0; i<directories.length; ++i ){
                if ( directories[i].getName().equals(id) )
                    return WISHLIST_DIR;
            }
        }
        
        return TEMP_DIR;
    }
    
}

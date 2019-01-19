package gamestopapp;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Games extends ArrayList<Game> {
    
    @Override
    public String toString ()
    {
        String str = new String();
        for( int game=0; game<this.size(); ++game ){
            str += this.get(game).toString()+"\n\n";
        }
        return str;
    }
    
    public void saveToFile() throws IOException
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userData/wishlist.dat"));
        
        for( int game=0; game<this.size(); ++game ){
            oos.writeObject( this.get(game) );
        }
        
        oos.close();
    }
    
    public static Games readFromFile() throws FileNotFoundException, IOException, ClassNotFoundException
    {        
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userData/wishlist.dat"));
        
        Games wishlist = new Games();        
        boolean eof = false;
        
        while(!eof){
            try{
                Game g = (Game)ois.readObject();
                wishlist.add(g);
            }catch(EOFException e){
                eof = true;
            }
        }
        
        return wishlist;
    }
    
}

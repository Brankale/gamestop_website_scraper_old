package gamestopapp;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamestopApp {

    public static void main(String[] args) {
        try {
            
            List<Game> wishlist = new ArrayList<>();
            
            
            wishlist.add(new Game("https://www.gamestop.it/PS4/Games/110143/detroit-become-human"));            
            wishlist.add(new Game("https://www.gamestop.it/PS4/Games/101550/persona-5-steelbook-launch-edition"));
            wishlist.add(new Game("https://www.gamestop.it/XboxONE/Games/112463/forza-horizon-4-deluxe-edition"));
            wishlist.add(new Game("https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition"));
            wishlist.add(new Game("https://www.gamestop.it/PS4/Games/99826"));
            wishlist.add(new Game("https://www.gamestop.it/PS4/Games/34052/gta-v"));
            
            saveToFile(wishlist);
            
            /*
            // this is the searchBar
            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.print("\n\n\nInserisci nome gioco: ");
                String gameName = in.nextLine();
                
                for ( GamePreview game : Game.searchGame(gameName) ) {
                    System.out.println(game);
                }
            }
            */
            
        } catch (Exception ex) {
            Logger.getLogger(GamestopApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printWishList(List<Game> wishlist){
        for(Game g : wishlist){
            System.out.println(g+"\n");
        }
    }
    
    public static void saveToFile(List<Game> wishlist) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userData/wishlist.dat"));
        
        for(Game g : wishlist){
            oos.writeObject(g);
        }
        
        oos.close();
    }
    
    public static List<Game> readFromFile() throws FileNotFoundException, IOException, ClassNotFoundException{
        
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userData/wishlist.dat"));
        
        List<Game> wishlist = new ArrayList<>();
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

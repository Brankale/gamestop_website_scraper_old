/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

// TRY JSOUP

/**
 *
 * @author Utente
 */
public class Game {
    
    private String title = null;
    private double newPrice = -1;
    private double oldNewPrice = -1;
    private double usedPrice = -1;
    private double oldUsedPrice = -1;
    private URL url = null;
    private Image image = null;

    public Game(URL url) throws IOException
    {
        this.setUrl(url);        // set the url
        
        // open the connection with the page to get the HTML
        BufferedReader in = new BufferedReader ( new InputStreamReader(url.openStream()) );
        String str = in.readLine();

        // Find the Title ------------------------------------------------
        while ( str!= null && !str.contains("<div class=\"prodTitle\">") ){
            str = in.readLine();
        }

        while ( str!= null && !str.contains("<span itemprop=\"name\">") ){
            str = in.readLine();
        }

        str = in.readLine();
        
        str = str.trim();                           // remove spaces before and after the title
        str = str.substring(0, str.length()-7);     // remove the &nbsp;
        this.setTitle(str);                         // set the title
        
        // Find the Image ------------------------------------------------
        
        while ( str!= null && !str.contains("3max.jpg") ){
            str = in.readLine();
        }        
        
        str = str.substring( str.lastIndexOf("https://"), str.lastIndexOf("3max.jpg")+8 );  // remove the part before and after the url 
        this.setImage( ImageIO.read( new URL(str) ) );                                      // set the image
        
        // Find the price ------------------------------------------------
        
        while ( str!= null && !str.contains("buySection") ){
            str = in.readLine();
        }
        
        boolean exit = false;
        
        do {
            while ( str!= null && !str.contains("singleVariantDetails") ){
                str = in.readLine();
            }

            while ( str!= null && !str.contains("variantName") ){
                str = in.readLine();
            }

            if ( str!= null && str.contains("Nuovo") )
            {
                while ( str!= null && !str.contains("valuteCont pricetext") ){
                    str = in.readLine();
                }

                str = str.substring( str.indexOf("€")+2 );
                str = str.substring( 0, str.indexOf("<") );                
                str = str.replace(',', '.');                
                this.setNewPrice( Double.parseDouble(str) );        // set the new price
                
                while ( str!= null && !str.contains("singleVariantDetails") && !str.contains("olderPrice") ){
                    str = in.readLine();
                }
                
                // if I read this, it means that I have found the Used Price
                if ( str!= null && str.contains("singleVariantDetails") )
                    continue;
                
                if ( str!= null ){
                    str = str.substring( str.indexOf("€")+2 );
                    str = str.substring( 0, str.indexOf("<") );
                    str = str.replace(',', '.');
                    this.setOldNewPrice( Double.parseDouble(str) );        // set the old new price
                }
            }

            if ( str!= null && str.contains("Usato") )
            {                
                while ( str!= null && !str.contains("valuteCont pricetext") ){
                    str = in.readLine();
                }

                str = str.substring( str.indexOf("€")+2 );
                str = str.substring( 0, str.indexOf("<") );
                str = str.replace(',', '.');
                this.setUsedPrice( Double.parseDouble(str) );        // set the old price
                
                while ( str!= null && !str.contains("olderPrice") ){
                    str = in.readLine();
                }
                
                if ( str!=null ) {
                    str = str.substring( str.indexOf("€")+2 );
                    str = str.substring( 0, str.indexOf("<") );
                    str = str.replace(',', '.');
                    this.setOldUsedPrice( Double.parseDouble(str) );        // set the old used price
                }                
                
                // I think every game has a Used Price, In the future I will change this
                exit = true;
            }
                
        } while ( !exit && str!=null );
        
        in.close();     // release resources  
    }
    
    public Game (String url) throws MalformedURLException, IOException {
        this( new URL(url) );
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setNewPrice(double newPrice) {
        if ( newPrice >= 0 )
            this.newPrice = newPrice;
    }

    private void setUsedPrice(double usedPrice) {
        if ( usedPrice >= 0 )
            this.usedPrice = usedPrice;
    }
    
    private void setOldNewPrice(double oldNewPrice) {
        if ( oldNewPrice >= 0 )
            this.oldNewPrice = oldNewPrice;
    }

    private void setOldUsedPrice(double oldUsedPrice) {
        if ( oldUsedPrice >= 0 )
            this.oldUsedPrice = oldUsedPrice;
    }

    private void setUrl(URL url) {
        this.url = url;
    }

    private void setImage(Image image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public double getUsedPrice() {
        return usedPrice;
    }

    public URL getUrl() {
        return url;
    }

    public Image getImage() {
        return image;
    }

    public double getOldNewPrice() {
        return oldNewPrice;
    }

    public double getOldUsedPrice() {
        return oldUsedPrice;
    }
    
    public void update () throws IOException {
        Game tmp = new Game(url);
        //this.title = tmp.getTitle();
        this.newPrice = tmp.getNewPrice();
        this.usedPrice = tmp.getUsedPrice();
        this.oldNewPrice = tmp.getOldNewPrice();
        this.oldUsedPrice = tmp.getOldUsedPrice();
        //this.image = tmp.getImage();
        //this.url = tmp.getUrl();
    }

    @Override
    public String toString()
    {
        String str = "";
        
        str += "Title: " + title + "\n";
        str += "URL: " + url + "\n";
        
        if ( newPrice > 0 )
            str += "New Price: " + newPrice;
        
        if ( oldNewPrice > 0 )
            str += "\tOld New Price: " + oldNewPrice + "\n";
        else
            str += "\n";
        
        if ( usedPrice > 0 )
            str += "Used Price: " + usedPrice;
        
        if ( oldUsedPrice > 0 )
            str += "\tOld Used Price: " + oldUsedPrice + "\n";
        else
            str += "\n";      
        
        return str;
    }
    
    
    
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


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
    private String url = null;
    private Image image = null;

    public Game(String url) throws IOException
    {
        Document doc = Jsoup.connect(url).get();        // return the HTML page        
        Element body = doc.body();                      // get the body
        
        Elements tmp;
        
        // Set the URL
        this.setUrl(url);
        
        // Find the title
        tmp = body.getElementsByClass("prodTitle");
        tmp = tmp.get(0).getElementsByTag("h1");
        this.setTitle( tmp.get(0).text() );
        
        // find the Image
        tmp = body.getElementsByClass("prodImg max");
        this.setImage( ImageIO.read( new URL(tmp.get(0).absUrl("href")) ) );
        
        // find the prices
        tmp = body.getElementsByClass("buySection");
        
        
        for ( Element i : tmp.get(0).getElementsByClass("singleVariantDetails") ){
            for ( Element j : i.getElementsByClass("singleVariantText") ){
                
                String prezzo = null;
                
                if ( j.getElementsByClass("variantName").get(0).text().equals("Nuovo") )
                {
                    prezzo = j.getElementsByClass("prodPriceCont").get(0).text();
                    prezzo = prezzo.substring( prezzo.indexOf(' ') );
                    prezzo = prezzo.replace(',', '.');
                    this.setNewPrice( Double.parseDouble(prezzo) );
                    
                    for ( Element k : j.getElementsByClass("olderPrice") ){
                        prezzo = k.text();
                        prezzo = prezzo.substring( prezzo.indexOf(' ') );
                        prezzo = prezzo.replace(',', '.');
                        this.setOldNewPrice( Double.parseDouble(prezzo) );
                    }
                }
                
                if ( j.getElementsByClass("variantName").get(0).text().equals("Usato") )
                {
                    prezzo = j.getElementsByClass("prodPriceCont").get(0).text();
                    prezzo = prezzo.substring( prezzo.indexOf(' ') );
                    prezzo = prezzo.replace(',', '.');
                    this.setUsedPrice( Double.parseDouble(prezzo) );
                    
                    for ( Element k : j.getElementsByClass("olderPrice") ){
                        prezzo = k.text();
                        prezzo = prezzo.substring( prezzo.indexOf(' ') );
                        prezzo = prezzo.replace(',', '.');
                        this.setOldUsedPrice( Double.parseDouble(prezzo) );
                    }
                }
            }
        }
        
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

    private void setUrl(String url) {
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

    public String getUrl() {
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
        Game tmp = new Game( this.url );
        this.newPrice = tmp.getNewPrice();
        this.usedPrice = tmp.getUsedPrice();
        this.oldNewPrice = tmp.getOldNewPrice();
        this.oldUsedPrice = tmp.getOldUsedPrice();
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

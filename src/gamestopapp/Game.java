package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
    private double vote;                    // not definitive (I find it useless, if you want a game the vote doesn't matter but sometime can help)
    private int usersNumber;                // not definitive (I find it useless)
    private List<Image> gallery;            // not definitive (may include photos and videos) (can be just a List<String> that contains the URLs)
    private String idNew;                   // not definitive (can be a number)
    private String idUsed;                  // not definitive (can be a number)
    private String genre;                   // not definitive (enum would be perfect but a game can have multiple genre)
    private String description;
    private String addToCart;
    private boolean storeAvailability;      // to discuss
    // the "BONUS" section should be added
*/

public class Game implements Serializable{
    
    private String title;
    private String url;
    private String publisher;
    private String platform;                // using String type, the app can memorize newer platform without making any changes to the app 
    private String cover;                    // not definitive (can be just a String with the URL / may break compatibility with Android)
    private double newPrice;
    private List<Double> olderNewPrices;    // in rare cases there are two older prices
    private double usedPrice;
    private List<Double> olderUsedPrices;   // in rare cases there are two older prices
    private List<String> pegi;
    //private List<String> genre;
    private String releaseDate;

    private Game() {
        this.title = null;
        this.url = null;
        this.publisher = null;
        this.platform = null;
        this.cover = null;
        this.newPrice = -1;
        this.olderNewPrices = new ArrayList<>();
        this.usedPrice = -1;
        this.olderUsedPrices = new ArrayList<>();
        this.pegi = new ArrayList<>();
        //this.genre = new ArrayList<>();
        this.releaseDate = null;
    }
    
    public Game (String url) throws IOException {
        
        this();
        
        File directories = new File("userData");
        if ( !directories.exists() ){
            directories.mkdir();
        }
        
        directories = new File("userData/covers");
        if ( !directories.exists() ){
            directories.mkdir();
        }
        
        Document html = Jsoup.connect(url).get();        // return the HTML page
        
        Element body = html.body();
        
        Element prodMain = body.getElementById("prodMain");
        
        Element mainInfo = prodMain.getElementsByClass("mainInfo").get(0);
        
        // in this section we can find: title, publisher, platform, vote, voting users
        Element prodTitle = mainInfo.getElementsByClass("prodTitle").get(0);        
        this.title = prodTitle.getElementsByTag("h1").text();
        this.publisher = prodTitle.getElementsByTag("strong").text();
        this.platform = url.split("/")[3];
        this.url = url;
        
        // in this section we can find: cover, gallery
        Element prodLeftBlock = prodMain.getElementsByClass("prodLeftBlock").get(0);
        String imageURL = prodLeftBlock.getElementsByClass("prodImg max").get(0).attr("href");       
        
        this.cover = URLEncoder.encode(title+".jpg", "UTF-8");
        
        // download the cover image if not already saved
        File imageOffline = new File("userData/covers/"+cover);
        if( !imageOffline.exists() ){
            try ( InputStream in = new URL(imageURL).openStream() ) {
                Files.copy(in, Paths.get("userData/covers/"+cover));
            }
        }
        
        // in this section we can find: prices, pegi, id, genre, release date, availability, addToCard
        Element prodRightBlock = prodMain.getElementsByClass("prodRightBlock").get(0);        
        Element buySection = prodRightBlock.getElementsByClass("buySection").get(0);
        
        for ( Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails") ){
            
            Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);                
            String price = null;

            if ( singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo") )
            {
                price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                this.newPrice = stringToPrice(price);

                for ( Element olderPrice : singleVariantText.getElementsByClass("olderPrice") ){
                    price = olderPrice.text();
                    this.olderNewPrices.add( stringToPrice(price) );
                }
            }

            if ( singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato") )
            {                
                price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                this.usedPrice = stringToPrice(price);

                for ( Element olderPrice : singleVariantText.getElementsByClass("olderPrice") ){                    
                    price = olderPrice.text();
                    this.olderUsedPrices.add( stringToPrice(price) );
                }
            }
            
        }
        
        
        // in this section we can find pegi, id, genre, releaseDate
        Element addedDet = prodRightBlock.getElementById("addedDet");       
        
        // cycle is totally useless, is just for performance        
        Element ageBlock = addedDet.getElementsByClass("ageBlock").get(0);
        
        // to replaced with getElementByClass StartingWith
        for ( Element e : ageBlock.getAllElements() )
        {
            if ( e.attr("class").equals("pegi18") ) { this.pegi.add("pegi18"); continue; }
            if ( e.attr("class").equals("pegi16") ) { this.pegi.add("pegi16"); continue; }
            if ( e.attr("class").equals("pegi12") ) { this.pegi.add("pegi12"); continue; }
            if ( e.attr("class").equals("pegi7") )  { this.pegi.add("pegi7"); continue; }
            if ( e.attr("class").equals("pegi3") )  { this.pegi.add("pegi3"); continue; }
            
            if ( e.attr("class").equals("ageDescr BadLanguage") )   { this.pegi.add("bad-language"); continue; }
            if ( e.attr("class").equals("ageDescr violence") )      { this.pegi.add("violence"); continue; }
            if ( e.attr("class").equals("ageDescr online") )        { this.pegi.add("online"); continue; }
            if ( e.attr("class").equals("ageDescr sex") )           { this.pegi.add("sex"); continue; }
            if ( e.attr("class").equals("ageDescr fear") )          { this.pegi.add("fear"); continue; }
            if ( e.attr("class").equals("ageDescr drugs") )         { this.pegi.add("drugs"); continue; }
            if ( e.attr("class").equals("ageDescr discrimination") ){ this.pegi.add("discrimination"); continue; }
            if ( e.attr("class").equals("ageDescr gambling") )      { this.pegi.add("gambling"); }
        }
        
        /*
        for ( Element p : addedDet.getElementsByTag("p") ) {
            //System.out.println( p.toString() );
            for ( TextNode t : p.textNodes() )
                System.out.println( t.toString() );
        }*/
        
    }
    
    // do not implement setter
    
    // implements getter when all the attributes are defintive

    // for now use toString just for tests
    @Override
    public String toString() {
        String str = "";
        
        str += "Title: " + title + "\n";
        str += "Cover: " + cover + "\n";
        str += "Publisher: " + publisher + "\n";
        str += "Platform: " + platform + "\n";
        str += "URL: " + url + "\n";
        
        if ( newPrice > 0 ) {
            str += "New Price: " + newPrice;
        
            for ( double price : olderNewPrices ){
                str += "\tOld New Price: " + price + "\n";
            }

            if ( olderNewPrices.size() < 1 ) { str += "\n"; }        
        }
        
        if ( usedPrice > 0 ) {
            str += "Used Price: " + usedPrice;
        
            for ( double price : olderUsedPrices ){
                str += "\tOld Used Price: " + price + "\n";
            }
            
            if ( olderUsedPrices.size() < 1 ) { str += "\n"; }
        }
        
        str += pegi.toString();
        
        return str;
    }
    
    public void update () {
        // update the game using the URL
        // to implement when all the attributes are defintive 
    }
    
    private double stringToPrice ( String price ) {
        price = price.substring( price.indexOf(' ') );
        price = price.replace(',', '.');
        return Double.parseDouble(price);
    }
    
    
}

package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
    private double vote;                    // not definitive (I find it useless, if you want a game the vote doesn't matter but sometime can help)
    private int usersNumber;                // not definitive (I find it useless)
    private List<Image> gallery;            // not definitive (may include photos and videos) (can be just a List<String> that contains the URLs)
    private int new_ID;
    private int digital_ID;
    private int used_ID;
    private String description;
    private String addToCart;
    private boolean storeAvailability;      // to discuss
    // the "BONUS" section should be added
*/

public class Game implements Serializable, Comparable<Game>{
    
    private String title;                   // it contains the title of the game
    private String url;                     // it contains the URL of the Gamestop's page
    private String publisher;               // it contains the game's publisher name
    private String platform;                // it contains the console where the game run (can be also a Gadget)
    private String cover;                   // it contains the title formatted with UTF-8. It's used to pick the image from the folder "usedData/covers"
    private double newPrice;                // it's the price for a new game
    private List<Double> olderNewPrices;    // it's the old price for a new game (in rare cases there are two or more older prices)
    private double usedPrice;               // it's the price for a used game
    private List<Double> olderUsedPrices;   // it's the old price for a used game (in rare cases there are two or more older prices)
    private List<String> pegi;              // it's a list containing all the types of PEGI a Game has
    private List<String> genres;            // it's a list containing all the genres a Game has
    private String officialSite;            // it contains the URL of the official site of the Game
    private short players;                  // it contains the number of players that can play the game at the same time
    private String releaseDate;             // it contains the release date of the game

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
        this.genres = new ArrayList<>();
        this.officialSite = null;
        this.players = -1;
        this.releaseDate = null;
    }
    
    public Game (String url) throws IOException {        
        
        this();     // init attributes
        
        // 1. CREATION OF CACHES FOLDERS
        
        // create userData folder if doesn't exist
        // userData folder contains caches
        File directories = new File("userData");
        if ( !directories.exists() ){
            directories.mkdir();
        }
        
        // create covers folder if doesn't exist
        // covers folder contains games' images caches
        directories = new File("userData/covers");
        if ( !directories.exists() ){
            directories.mkdir();
        }
        
        directories = new File("userData/gallery");
        if ( !directories.exists() ){
            directories.mkdir();
        }
        
        // 2. GET INFORMATION FROM THE WEBSITE
        
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
        
        // in this section we can find: cover, gallery?
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
        
        
        // "addedDet" section contains: pegi, id, genre, releaseDate
        Element addedDet = prodRightBlock.getElementById("addedDet");
        
        // cycle is totally useless, is just for performance  
        Element ageBlock = addedDet.getElementsByClass("ageBlock").get(0);
        
        // Init PEGI
        // to replace with getElementByClass StartingWith
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
        
        // Search: Codice Articolo / Genere / Sito ufficiale / Giocatori / Rilascio
        for ( Element e : addedDet.getElementsByTag("p") )
        {            
            // USE THIS FOR TESTS
            /*
            System.out.println( "\n" + e.toString() );
            System.out.println( e.childNodeSize() + "\n");
            if ( e.childNodeSize() > 1 )
                System.out.println( "#" + e.child(0).text() );
            */
            
            /*            
            for ( TextNode t : p.textNodes() )
                System.out.println( t.toString() );
            */  
            
            // important check to avoid IndexOutOfBound Exception
            if ( e.childNodeSize() > 1 )
            {
                // Set item ID
                if ( e.child(0).text().equals("Codice articolo") ) {
                    // It's not so useful
                    continue;
                }
                
                // set genre
                if ( e.child(0).text().equals("Genere") ) {
                    // System.out.println( e.child(1).text() );                    
                    String strGenres = e.child(1).text();  // return example: Action/Adventure
                    for ( String genre : strGenres.split("/") )
                        genres.add(genre);
                    continue;
                }
                
                // set official site
                if ( e.child(0).text().equals("Sito Ufficiale") ) {
                    // System.out.println( e.child(1) );
                    // System.out.println( e.child(1).getElementsByTag("a").attr("href") );
                    this.officialSite = e.child(1).getElementsByTag("a").attr("href");
                    continue;
                }
                
                // set the number of players
                if ( e.child(0).text().equals("Giocatori") ) {
                    // System.out.println( e.child(1).text() );
                    this.players = Short.parseShort( e.child(1).text() );
                    continue;
                }
                
                // set the release date
                if ( e.child(0).text().equals("Rilascio") ) {
                    //System.out.println( e.child(1).text() );
                    
                    String date = e.child(1).text();
                    date = date.replace(".", "/");
                    
                    this.releaseDate = date;
                }                
            }                      
        }
        
        // GALLERY IMPLEMENTATION
        
        
        Elements mediaIn = prodMain.getElementsByClass("mediaIn");
        
        // check if there are some medias
        if ( !mediaIn.isEmpty() )
        {
            Elements mediaVideo = prodMain.getElementsByClass("mediaVideo");
            
            if ( !mediaVideo.isEmpty() ){                
                // to take the video you must use Javascript
                // it's possible to pick the URL but just from the browser
            }            
            
            Elements mediaImages = prodMain.getElementsByClass("mediaImages");
            if ( !mediaImages.isEmpty() )
            {                
                Elements imagesURLs = mediaImages.get(0).getElementsByTag("a");
                for ( Element e : imagesURLs )
                {                    
                    imageURL = e.attr("href");
                    System.out.println( imageURL );
                    
                     directories = new File("userData/gallery/"+imageURL.split("/")[5]);
                    if ( !directories.exists() ){
                        directories.mkdir();
                    }
                    
                    try ( InputStream in = new URL(imageURL).openStream() ) {
                        
                        
                        Files.copy(in, Paths.get("userData/gallery/" +imageURL.split("/")[5] +"/"+ imageURL.split("/")[6] ));
                    } catch (Exception ex){}
                }
            }
        }
        
    }
    
    // do not implement setter
    
    // implements getter when all the attributes are definitive

    public String getPlatform() {
        return platform;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public double getUsedPrice() {
        return usedPrice;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }
    
    
    

    // toString method at the moment is used just for tests
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
        
        str += "PEGI: " + pegi.toString() + "\n";
        str += "Genres:" + genres.toString() + "\n";        
        
        if ( this.officialSite != null )
            str += "Official Site: " + officialSite + "\n";
        
        if ( this.players > 0 )
            str += "Number of Players: " + players + "\n";
        
        str += "Release Date: " + releaseDate + "\n";
        
        return str;
    }
        
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        final Game other = (Game) obj;
        
        // NOTE: these two URLs are the same
        // https://www.gamestop.it/PS3/Games/31910/persona-4-arena-limited-edition
        // https://www.gamestop.it/PS3/Games/31910        
        
        return Objects.equals(this.url, other.url);     // See the problem above
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public int compareTo(Game game) {
        return this.title.compareTo(game.title);    // game.title -> game.getTitle();
    }
    
    public void update () {
        // update the game using the URL
        // to implement when all the attributes are defintive 
    }
    
    private double stringToPrice ( String price )
    {        
        //System.out.println( "#" + price + "#" );        
        price = price.replace(',', '.');
        price = price.replace("€", "");
        price = price.replace("CHF", "");
        price = price.trim();
        //System.out.println( "#" + price + "#" );
        
        return Double.parseDouble(price);
    }
    
}

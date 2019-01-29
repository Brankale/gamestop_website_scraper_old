package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/*
    private double vote;                    // not definitive (I find it useless, if you want a game the vote doesn't matter but sometime can help)
    private int usersNumber;                // not definitive (I find it useless)    
    private String description;
    private String addToCart;
    private boolean storeAvailability;
    // the "BONUS" section should be added
*/

public class Game implements Serializable, Comparable<Game>{
    
    private String title;                   // it contains the title of the game
    private String url;                     // it contains the URL of the Gamestop's page
    private String publisher;               // it contains the game's publisher name
    private String platform;                // it contains the console where the game run (can be also a Gadget)
    private double newPrice;                // it's the price for a new game
    private List<Double> olderNewPrices;    // it's the old price for a new game (in rare cases there are two or more older prices)
    private double usedPrice;               // it's the price for a used game
    private List<Double> olderUsedPrices;   // it's the old price for a used game (in rare cases there are two or more older prices)
    private List<String> pegi;              // it's a list containing all the types of PEGI a Game has
    private String new_ID;
    private String digital_ID;
    private String used_ID;
    private List<String> genres;            // it's a list containing all the genres a Game has
    private String officialSite;            // it contains the URL of the official site of the Game
    private short players;                  // it contains the number of players that can play the game at the same time
    private String releaseDate;             // it contains the release date of the game

    private Game() {
        this.title = null;
        this.url = null;
        this.publisher = null;
        this.platform = null;
        this.newPrice = -1;
        this.olderNewPrices = new ArrayList<>();
        this.usedPrice = -1;
        this.olderUsedPrices = new ArrayList<>();
        this.pegi = new ArrayList<>();
        this.new_ID = null;
        this.digital_ID = null;
        this.used_ID = null;
        this.genres = new ArrayList<>();
        this.officialSite = null;
        this.players = -1;
        this.releaseDate = null;
    }
    
    public Game (String url) throws IOException
    {
        // init attributes 
        this();       
        
        // 1. GET INFORMATION FROM THE WEBSITE
        
        Document html = Jsoup.connect(url).get();
        Log.info("Game", "downloaded HTML", url);
        
        Element body = html.body();
        Element prodMain = body.getElementById("prodMain");
        
        Element mainInfo = prodMain.getElementsByClass("mainInfo").get(0);
        
        // "mainInfo" contains: title, publisher, platform, vote, voting users
        Element prodTitle = mainInfo.getElementsByClass("prodTitle").get(0);        
        this.title = prodTitle.getElementsByTag("h1").text();
        this.publisher = prodTitle.getElementsByTag("strong").text();
        this.platform = url.split("/")[3];
        this.url = url;       
        
        // "prodRightBlock" contains: prices, pegi, id, genre, release date, availability, addToCard
        Element prodRightBlock = prodMain.getElementsByClass("prodRightBlock").get(0);        
        Element buySection = prodRightBlock.getElementsByClass("buySection").get(0);
        
        for ( Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails") )
        {
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
        
        // "addedDet" contains: pegi, IDs, Genres, Official Site, Players, ReleaseDate
        Element addedDet = prodRightBlock.getElementById("addedDet");
        
        // cycle is totally useless, is just for performance  
        Element ageBlock = addedDet.getElementsByClass("ageBlock").get(0);
        
        // set PEGI
        // to replace with getElementByClass StartingWith
        for ( Element e : ageBlock.getAllElements() )
        {
            String str = e.attr("class");
            
            if ( str.equals("pegi18") ) { this.pegi.add("pegi18"); continue; }
            if ( str.equals("pegi16") ) { this.pegi.add("pegi16"); continue; }
            if ( str.equals("pegi12") ) { this.pegi.add("pegi12"); continue; }
            if ( str.equals("pegi7") )  { this.pegi.add("pegi7"); continue; }
            if ( str.equals("pegi3") )  { this.pegi.add("pegi3"); continue; }
            
            if ( str.equals("ageDescr BadLanguage") )   { this.pegi.add("bad-language"); continue; }
            if ( str.equals("ageDescr violence") )      { this.pegi.add("violence"); continue; }
            if ( str.equals("ageDescr online") )        { this.pegi.add("online"); continue; }
            if ( str.equals("ageDescr sex") )           { this.pegi.add("sex"); continue; }
            if ( str.equals("ageDescr fear") )          { this.pegi.add("fear"); continue; }
            if ( str.equals("ageDescr drugs") )         { this.pegi.add("drugs"); continue; }
            if ( str.equals("ageDescr discrimination") ){ this.pegi.add("discrimination"); continue; }
            if ( str.equals("ageDescr gambling") )      { this.pegi.add("gambling"); }
        }        
        
        // set: IDs, Genres, Official Site, Players, Release Date
        for ( Element e : addedDet.getElementsByTag("p") )
        {            
            // important check to avoid IndexOutOfBound Exception
            if ( e.childNodeSize() > 1 )
            {
                // set item ID
                if ( e.child(0).text().equals("Codice articolo") )
                {                    
                    for ( Node n : e.childNodes() )
                    {                        
                        String id = n.toString();
                        
                        id = id.replace("<span>", "");
                        id = id.replace("</span>", "");
                        id = id.replace("<em>", "");
                        id = id.replace("</em>", "");
                        id = id.replace(" ", "");

                        if ( id.split(":")[0].equals("Nuovo") ){
                            this.new_ID = id.split(":")[1];
                            continue;
                        }
                        
                        if ( id.split(":")[0].equals("Usato") ){
                            this.used_ID = id.split(":")[1];
                            continue;
                        }
                        
                        // NB: "ContenutoDigitale" should be like this "Contenuto Digitale"
                        // but before I removed the spaces, so it must be written like this
                        if ( id.split(":")[0].equals("ContenutoDigitale") ){
                            this.digital_ID = id.split(":")[1];
                        }                        
                    }
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
                    this.releaseDate = e.child(1).text();
                    this.releaseDate = releaseDate.replace(".","/");
                }                
            }                      
        }        
        
        // 2. CREATION OF CACHES FOLDERS
        
        /*
            CACHES FOLDER STRUCTURE
        
            userData/                       contains all caches
                |- xxx.tmp                  example of file
                |- GameID/                  it's a folder with the name of the Game ID that contains all the data about a game
                    |- cover.jpg            it's the cover of the game
                    |- gallery/             it's the folder which contains all the images of the gallery
                        |- imageXX.jpg      it's a image of the the game
        */
        
        // create userData folder if doesn't exist
        File directories = new File("userData");
        if ( !directories.exists() ){
            directories.mkdir();
            Log.info("Game", "userData folder created");
        }
        
        // creation of GameID folder
        String path = null;
        
        if ( new_ID != null )    { path = "userData/" + new_ID + "/"; }
        if ( digital_ID != null ){ path = "userData/" + digital_ID + "/"; }
        
        directories = new File(path);
        
        if ( !directories.exists() ){
            directories.mkdir();
            Log.info("Game", "folder created", path);
        } else {
            Log.warning("Game", "folder already exist", path);
        }
        
        // in "prodLeftBlock" section we can find the cover
        Element prodLeftBlock = prodMain.getElementsByClass("prodLeftBlock").get(0);
        String imageURL = prodLeftBlock.getElementsByClass("prodImg max").get(0).attr("href");
        
        // download the cover image if not already saved
        File imageOffline = new File(path+"cover.jpg");
        if( !imageOffline.exists() ){
            try ( InputStream in = new URL(imageURL).openStream() ) {
                Files.copy(in, Paths.get(path+"cover.jpg"));
                Log.info("Game", "cover downloaded", imageURL);
            } catch (IOException e) {
                Log.error("Game", "cannot download the cover", imageURL);
            }
        } else {
            Log.info("Game", "cover already exist", imageURL);
        }
        
        // in "mediaIn" section we can find the gallery
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
                // Create the folder if there are media files
                path = path + "gallery/";
                directories = new File(path);
                if ( !directories.exists() ){
                    directories.mkdir();
                }
                
                Elements imagesURLs = mediaImages.get(0).getElementsByTag("a");
                for ( Element e : imagesURLs )
                {                    
                    imageURL = e.attr("href");
                    String imageURI = path + imageURL.split("/")[6];
                    imageOffline = new File(imageURI);
                    
                    if ( !imageOffline.exists() ){
                        try ( InputStream in = new URL(imageURL).openStream() ) {
                            Files.copy(in, Paths.get(imageURI));
                            Log.info("Game", "downloaded the image", imageURL.split("/")[6]);
                        } catch (Exception ex) {
                            Log.error("Game", "cannot download the image", imageURL.split("/")[6]);
                        }
                    } else {
                        Log.warning("Game", "the image already exist", imageURL.split("/")[6]);
                    }
                }
            }
        }
        
    }

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

    @Override
    public String toString() {
        String str = "";
        
        str += "Title: " + title + "\n";
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
        
        if ( new_ID != null )
            str += "New ID: " + new_ID + "\n";
        
        if ( used_ID != null )
            str += "Used ID: " + used_ID + "\n";
        
        if ( digital_ID != null )
            str += "Digital ID: " + digital_ID + "\n";
            
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
    public boolean equals(Object obj)
    {
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
        return this.title.compareTo(game.getTitle());
    }
    
    public void update () {
        // update the game using the URL
        // to implement when all the attributes are defintive 
    }
    
    private double stringToPrice ( String price )
    {      
        price = price.replace(',', '.');
        price = price.replace("€", "");
        price = price.replace("CHF", "");
        price = price.trim();
        
        return Double.parseDouble(price);
    }
    
}

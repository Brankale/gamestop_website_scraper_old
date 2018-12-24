package gamestopapp;

import java.awt.Image;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Game {
    
    private String title;
    private String url;
    private String publisher;
    private String platform;                // not definitive (enum would be perfect / the name is not aprropriate for "Gadget" and "Varie" (platform -> contentType/type ) 
    private double vote;                    // not definitive (I find it useless, if you want a game the vote doesn't matter but sometime can help)
    private int usersNumber;                // not definitive (I find it useless)
    private Image image;                    // not definitive (can be just a String with the URL / may break compatibility with Android)
    private List<Image> gallery;            // not definitive (may include photos and videos) (can be just a List<String> that contains the URLs)
    private double newPrice;
    private List<Double> olderNewPrices;    // not definitive (in rare cases there are two olderPrices but we can use the bigger olderPrice and use a double)
    private double usedPrice;
    private List<Double> olderUsedPrices;   // not definitive (in rare cases there are two olderPrices but we can use the bigger olderPrice and use a double)
    private String pegi;                    // not definitive (can be a number)
    private String idNew;                   // not definitive (can be a number)
    private String idUsed;                  // not definitive (can be a number)
    private String genre;                   // not definitive (enum would be perfect but a game can have multiple genre)
    private String releaseDate;             // not definitive (it could be better using an appropriate class)
    private String description;
    private boolean storeAvailability;
    private String addToCart;
    // the "BONUS" section should be added    

    private Game() {
        this.title = null;
        this.url = null;
        this.publisher = null;
        this.platform = null;
        this.vote = -1;
        this.usersNumber = -1;
        this.image = null;
        this.gallery = new ArrayList<>();
        this.newPrice = -1;
        this.olderNewPrices = new ArrayList<>();
        this.usedPrice = -1;
        this.olderUsedPrices = new ArrayList<>();
        this.pegi = null;
        this.idNew = null;
        this.idUsed = null;
        this.genre = null;
        this.releaseDate = null;
        this.description = null;
        this.storeAvailability = false;
        this.addToCart = null;
    }
    
    public Game (String url) throws IOException{
        
        this();
        
        Document doc = Jsoup.connect(url).get();        // return the HTML page        
        Element body = doc.body();                      // get the body
        
        Elements tmp;
        
        // Set the URL
        this.url = url;
        
        // Find the title
        tmp = body.getElementsByClass("prodTitle");
        tmp = tmp.get(0).getElementsByTag("h1");
        this.title = tmp.get(0).text();
        
        // find the Image
        tmp = body.getElementsByClass("prodImg max");
        this.image = ImageIO.read( new URL(tmp.get(0).absUrl("href")) );
        
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
                    this.newPrice = Double.parseDouble(prezzo);
                    
                    for ( Element k : j.getElementsByClass("olderPrice") ){
                        prezzo = k.text();
                        prezzo = prezzo.substring( prezzo.indexOf(' ') );
                        prezzo = prezzo.replace(',', '.');
                        this.olderNewPrices.add( Double.parseDouble(prezzo) );
                    }
                }
                
                if ( j.getElementsByClass("variantName").get(0).text().equals("Usato") )
                {
                    prezzo = j.getElementsByClass("prodPriceCont").get(0).text();
                    prezzo = prezzo.substring( prezzo.indexOf(' ') );
                    prezzo = prezzo.replace(',', '.');
                    this.usedPrice = Double.parseDouble(prezzo);
                    
                    for ( Element k : j.getElementsByClass("olderPrice") ){
                        prezzo = k.text();
                        prezzo = prezzo.substring( prezzo.indexOf(' ') );
                        prezzo = prezzo.replace(',', '.');
                        this.olderUsedPrices.add( Double.parseDouble(prezzo) );
                    }
                }
            }
        }
        
        // Find the description        
        Element description;
        description = body.getElementById("prodDesc");
        description = description.getElementsByClass("textDesc").get(0);
        
        // formattazione non sempre corretta (sarebbe meglio aggiungere i \n alla fine di ogni riga
        // Per testare usare questo link: https://www.gamestop.it/PS4/Games/99826
        this.description = description.text();
        
    }
    
    // do not implement setter (beacause it's better not to use them in the constructor so they are useless)
    
    // implements getter when all the attributes are defintive

    // for now use toString just for tests
    @Override
    public String toString() {
        String str = "";
        
        str += "Title: " + title + "\n";
        str += "URL: " + url + "\n";
        
        if ( newPrice > 0 )
            str += "New Price: " + newPrice;
        
        if ( olderNewPrices.size()>0 && olderNewPrices.get(0) > 0 )
            str += "\tOld New Price: " + olderNewPrices.get(0) + "\n";
        else
            str += "\n";
        
        if ( usedPrice > 0 )
            str += "Used Price: " + usedPrice;
        
        if ( olderUsedPrices.size()>0 && olderUsedPrices.get(0) > 0 )
            str += "\tOld Used Price: " + olderUsedPrices.get(0) + "\n";
        else
            str += "\n";      
        
        return str;
    }
    
    public void update () {
        // update the game using the URL
        // to implement when all the attributes are defintive 
    }    
    
    public static List<GamePreview> searchGame(String searchedGameName) throws UnsupportedEncodingException, IOException {
        
        List<GamePreview> searchedGames = new ArrayList();
        String site = "https://www.gamestop.it";
        String path = "/SearchResult/QuickSearch";
        String query = "?q=" + URLEncoder.encode(searchedGameName, "UTF-8");
        String url = site + path + query;
        
        System.out.println(url);
        
        Document doc = Jsoup.connect(url).get();
        Element body = doc.body();
        
        Elements gamesList = body.getElementsByClass("singleProduct");
        
        System.out.println(doc.getElementById("group11"));
        
        for(Element game : gamesList){
            String gameImageUrl = game.getElementsByClass("prodImg").get(0).getElementsByTag("img").get(0).absUrl("data-llsrc");
            String gameTitle = game.getElementsByTag("h3").get(0).text();
            String gameUrl = game.getElementsByTag("h3").get(0).getElementsByTag("a").get(0).absUrl("href");
            String gamePlatform = gameUrl.split("/")[3];
            GamePreview previewGame = new GamePreview(gameTitle, gameUrl, gamePlatform, gameImageUrl );
            searchedGames.add(previewGame);
        }
        
        return searchedGames;
    }
    
    
    
}

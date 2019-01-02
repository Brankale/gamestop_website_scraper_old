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

/*
    private double vote;                    // not definitive (I find it useless, if you want a game the vote doesn't matter but sometime can help)
    private int usersNumber;                // not definitive (I find it useless)
    private List<Image> gallery;            // not definitive (may include photos and videos) (can be just a List<String> that contains the URLs)
    private String pegi;                    // not definitive (can be a number)
    private String idNew;                   // not definitive (can be a number)
    private String idUsed;                  // not definitive (can be a number)
    private String genre;                   // not definitive (enum would be perfect but a game can have multiple genre)
    private String description;
    private String addToCart;
    private boolean storeAvailability;      // to discuss
    // the "BONUS" section should be added
*/

public class Game {
    
    private String title;
    private String url;
    private String publisher;
    private String platform;                // using String type, the app can memorize newer platform without making any changes to the app 
    private Image cover;                    // not definitive (can be just a String with the URL / may break compatibility with Android)
    private double newPrice;
    private List<Double> olderNewPrices;    // in rare cases there are two older prices
    private double usedPrice;
    private List<Double> olderUsedPrices;   // in rare cases there are two older prices
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
        this.releaseDate = null;
    }
    
    public Game (String url) throws IOException {
        
        this();        
        
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
        this.cover = ImageIO.read( new URL(imageURL) );        
        
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

    }
    
    // do not implement setter
    
    // implements getter when all the attributes are defintive

    // for now use toString just for tests
    @Override
    public String toString() {
        String str = "";
        
        str += "Title: " + title + "\n";
        str += "Publisher: " + publisher + "\n";
        str += "Platform: " + platform + "\n";
        str += "URL: " + url + "\n";
        
        if ( newPrice > 0 )
            str += "New Price: " + newPrice;
        
        for ( double price : olderNewPrices ){
            str += "\tOld New Price: " + price + "\n";
        }
        
        if ( olderNewPrices.size() < 1 ) { str += "\n"; }
        
        if ( usedPrice > 0 )
            str += "Used Price: " + usedPrice;
        
        for ( double price : olderUsedPrices ){
            str += "\tOld Used Price: " + price + "\n";
        }
        
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
    
    private double stringToPrice ( String price ) {
        price = price.substring( price.indexOf(' ') );
        price = price.replace(',', '.');
        return Double.parseDouble(price);
    }
    
    
}

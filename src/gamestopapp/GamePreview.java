package gamestopapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GamePreview implements Comparable<GamePreview> {
    
    protected String id;
    protected String title;
    protected String publisher;
    protected String platform;
    
    protected Double newPrice;
    protected Double usedPrice;
    protected Double preorderPrice;
    protected Double digitalPrice;
    protected List<Double> olderNewPrices;
    protected List<Double> olderUsedPrices;
    
    protected List<String> pegi;
    protected String releaseDate;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPlatform() {
        return platform;
    }

    public Double getNewPrice() {
        return newPrice;
    }
    
    public boolean hasNewPrice() {
        return newPrice != null;
    }

    public Double getUsedPrice() {
        return usedPrice;
    }
    
    public boolean hasUsedPrice() {
        return usedPrice != null;
    }

    public Double getPreorderPrice() {
        return preorderPrice;
    }
    
    public boolean hasPreorderPrice() {
        return preorderPrice != null;
    }

    public Double getDigitalPrice() {
        return digitalPrice;
    }
    
    public boolean hasDigitalPrice() {
        return digitalPrice != null;
    }

    public List<Double> getOlderNewPrices() {
        return olderNewPrices;
    }
    
    public boolean hasOlderNewPrices() {
        return olderNewPrices != null;
    }

    public List<Double> getOlderUsedPrices() {
        return olderUsedPrices;
    }
    
    public boolean hasOlderUsedPrices() {
        return olderUsedPrices != null;
    }

    public List<String> getPegi() {
        return pegi;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    
    public boolean hasReleaseDate() {
        return releaseDate != null;
    }
    
    public String getURL() {
        return getURLByID(id);
    }
    
    public static String getURLByID ( String id ) {
        return "http://www.gamestop.it/Platform/Games/" + id;
    }
    
    public String getGameDirectory() {
        return DirectoryManager.getDirectory(id) + id + "/";
    }
    
    public String getCover() {
        return getGameDirectory() + "cover.jpg";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GamePreview other = (GamePreview) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "GamePreview{" + "id=" + id + ", title=" + title + ", publisher=" + publisher + ", platform=" + platform + ", newPrice=" + newPrice + ", usedPrice=" + usedPrice + ", preorderPrice=" + preorderPrice + ", digitalPrice=" + digitalPrice + ", olderNewPrices=" + olderNewPrices + ", olderUsedPrices=" + olderUsedPrices + ", pegi=" + pegi + ", releaseDate=" + releaseDate + '}';
    }

    @Override
    public int compareTo(GamePreview gamePreview) {
        return title.compareTo(gamePreview.getTitle());
    }
    
    public static List<GamePreview> searchGame(String searchedGameName) throws UnsupportedEncodingException, IOException {

        String url = "https://www.gamestop.it/SearchResult/QuickSearch?q=" + URLEncoder.encode(searchedGameName, "UTF-8");
        
        Document doc = Jsoup.connect(url).get();
        Element body = doc.body();
        
        Elements gamesList = body.getElementsByClass("singleProduct");
        Log.info("GamePreview", "search completed", gamesList.size()+" results" );
        
        // if there are no games
        if ( gamesList.isEmpty() ){
            return null;
        }
        
        List<GamePreview> searchedGames = new ArrayList();

        for ( Element game : gamesList )
        {
            GamePreview gamePreview = new GamePreview();
            
            gamePreview.id = game.getElementsByClass("prodImg").get(0).attr("href").split("/")[3];
            gamePreview.title = game.getElementsByTag("h3").get(0).text();
            //gamePreview.publisher;
            gamePreview.platform = game.getElementsByTag("h3").get(0).getElementsByTag("a").get(0).attr("href").split("/")[3];

            
            Elements e = game.getElementsByClass("buyNew");
            if ( !e.isEmpty() ){                
                if ( e.get(0).getElementsByClass("discounted").isEmpty() ){
                    String price = e.get(0).text();
                    System.out.println(price);
                    gamePreview.newPrice = stringToPrice(price);
                } else {
                    /*
                    String price = e.get(0).text();
                    gamePreview.newPrice = stringToPrice(price);
                    gamePreview.olderNewPrices = new ArrayList<>();
                    gamePreview.olderNewPrices.add(stringToPrice(price));
                    */
                }
            }
            
            e = game.getElementsByClass("buyUsed");
            if ( !e.isEmpty() ){
                String price = e.get(0).text();
                gamePreview.usedPrice = stringToPrice(price);
            }
            
            e = game.getElementsByClass("buyPresell");
            if ( !e.isEmpty() ){
                String price = e.get(0).text();
                gamePreview.preorderPrice = stringToPrice(price);
            }
            
            e = game.getElementsByClass("buyDLC");
            if ( !e.isEmpty() ){
                String price = e.get(0).text();
                gamePreview.digitalPrice = stringToPrice(price);
            }
            
            
            //gamePreview.olderNewPrices;
            //gamePreview.olderUsedPrices;
            
            gamePreview.pegi = new ArrayList<>();
            gamePreview.pegi.add( game.getElementsByTag("p").get(0).text() );
            gamePreview.releaseDate = game.getElementsByTag("li").get(0).text().split(": ")[1];
            
            //cover
            
            searchedGames.add(gamePreview);
        }
        
        return searchedGames;
    }
    
    protected static double stringToPrice(String price) {
        
        price = price.split(" ")[1];        // <-- example "Nuovo 19.99€"
        price = price.replace(".", "");     // <-- to handle prices over 999,99€ like 1.249,99€
        price = price.replace(',', '.');    // <-- to convert the price in a string that can be parsed
        price = price.replace("€", "");     // <-- remove unecessary characters
        price = price.replace("CHF", "");   // <-- remove unecessary characters
        price = price.trim();               // <-- remove remaning spaces
        
        return Double.parseDouble(price);
    }
    
}

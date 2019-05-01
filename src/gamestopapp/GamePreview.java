package gamestopapp;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    protected List<Double> olderDigitalPrices;
    protected List<Double> olderPreorderPrices;
    
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
        if ( olderNewPrices == null )
            return false;
        return olderNewPrices.size() > 0;
    }

    public List<Double> getOlderUsedPrices() {
        return olderUsedPrices;
    }
    
    public boolean hasOlderUsedPrices() {
        if ( olderUsedPrices == null )
            return false;
        return olderUsedPrices.size() > 0;
    }
    
    public List<Double> getOlderPreorderPrices() {
        return olderPreorderPrices;
    }
    
    public boolean hasOlderPreorderPrices() {
        if ( olderPreorderPrices == null )
            return false;
        return olderPreorderPrices.size() > 0;
    }
    
    public List<Double> getOlderDigitalPrices() {
        return olderDigitalPrices;
    }
    
    public boolean hasOlderDigitalPrices() {        
        if ( olderDigitalPrices == null )
            return false;
        return olderDigitalPrices.size() > 0;
    }

    public List<String> getPegi() {
        return pegi;
    }
    
    public boolean hasPegi() {
        if ( pegi == null )
            return false;
        return pegi.size() > 0;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    
    public boolean hasReleaseDate() {        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.parse(releaseDate);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
    
    public String getURL() {
        return getURLbyID(id);
    }
    
    public static String getURLbyID ( String id ) {
        return "http://www.gamestop.it/Platform/Games/" + id;
    }    
    
    public String getCover() {
        return DirectoryManager.getGameDirectory(id)+ "cover.jpg";
    }
    
    public boolean hasCover() {
        return new File(getCover()).exists();
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
        
        String str = new String();        
        str += "GamePreview{\n";
        
        if ( id != null )
            str += " id = " + id + "\n";
        
        if ( title != null )
            str += " title = " + title + "\n";
        
        if ( publisher != null )
            str += " publisher = " + publisher + "\n";
        
        if ( platform != null )
            str += " platform = " + platform + "\n";
        
        if ( newPrice != null )
            str += " newPrice = " + newPrice + "\n";
        
        if ( usedPrice != null )
            str += " usedPrice = " + usedPrice + "\n";
        
        if ( preorderPrice != null )
            str += " preorderPrice = " + preorderPrice + "\n";
        
        if ( digitalPrice != null )
            str += " digitalPrice = " + digitalPrice + "\n";
        
        if ( olderNewPrices != null )
            str += " olderNewPrices = " + olderNewPrices + "\n";
        
        if ( olderUsedPrices != null )
            str += " olderUsedPrices = " + olderUsedPrices + "\n";
        
        if ( olderPreorderPrices != null )
            str += " olderPreorderPrices = " + olderPreorderPrices + "\n";
        
        if ( olderDigitalPrices != null )
            str += " olderDigitalPrices = " + olderDigitalPrices + "\n";
        
        if ( pegi != null )
            str += " pegi = " + pegi + "\n";
        
        if ( releaseDate != null )
            str += " releaseDate = " + releaseDate + "\n";
        
        str += "}";
        return str;
    }

    @Override
    public int compareTo(GamePreview gamePreview) {
        return title.compareToIgnoreCase(gamePreview.title);
    }
    
    public static Games searchGame(String searchedGameName) throws UnsupportedEncodingException, IOException {

        String url = "https://www.gamestop.it/SearchResult/QuickSearch?q=" + URLEncoder.encode(searchedGameName, "UTF-8");
        
        Document doc = Jsoup.connect(url).get();
        Element body = doc.body();
        
        Elements gamesList = body.getElementsByClass("singleProduct");
        Log.info("GamePreview", "search completed", gamesList.size()+" results" );
        
        // if there are no games
        if ( gamesList.isEmpty() ){
            return null;
        }
        
        Games searchedGames = new Games();

        for ( Element game : gamesList )
        {
            GamePreview gamePreview = new GamePreview();
            
            gamePreview.id = game.getElementsByClass("prodImg").get(0).attr("href").split("/")[3];
            gamePreview.title = game.getElementsByTag("h3").get(0).text();
            gamePreview.publisher = game.getElementsByTag("h4").get(0).getElementsByTag("strong").text();
            gamePreview.platform = game.getElementsByTag("h4").get(0).textNodes().get(0).text().trim();
            
            Elements e = game.getElementsByClass("buyNew");
            if ( !e.isEmpty() ){                
                Elements prices = e.get(0).getElementsByTag("em");
                
                // if there's just one price
                // NB: <em> tag is present only if there are multiple prices
                if ( prices.isEmpty() ){
                    String price = e.get(0).text();
                    gamePreview.newPrice = stringToPrice(price);
                }
                
                // if more than one price is present
                if ( prices.size() > 1 )
                    gamePreview.olderNewPrices = new ArrayList<>();
                
                // memorize the prices
                for ( int i=0; i<prices.size(); ++i ){                    
                    String price = prices.get(i).text();
                    
                    if ( i==0 ){
                        gamePreview.newPrice = stringToPrice(price);
                    } else {
                        gamePreview.olderNewPrices.add(stringToPrice(price));
                    }                    
                }
            }
            
            e = game.getElementsByClass("buyUsed");
            if ( !e.isEmpty() ){                
                Elements prices = e.get(0).getElementsByTag("em");
                
                // if there's just one price
                // NB: <em> tag is present only if there are multiple prices
                if ( prices.isEmpty() ){
                    String price = e.get(0).text();
                    gamePreview.usedPrice = stringToPrice(price);
                }
                
                // if more than one price is present
                if ( prices.size() > 1 ) {
                    gamePreview.olderUsedPrices = new ArrayList<>();
                
                    // memorize the prices
                    for ( int i=0; i<prices.size(); ++i ){                    
                        String price = prices.get(i).text();

                        if ( i==0 ){
                            gamePreview.usedPrice = stringToPrice(price);
                        } else {
                            gamePreview.olderUsedPrices.add(stringToPrice(price));
                        }                    
                    }
                }
            }
            
            e = game.getElementsByClass("buyPresell");
            if ( !e.isEmpty() ){                
                Elements prices = e.get(0).getElementsByTag("em");
                
                // if there's just one price
                // NB: <em> tag is present only if there are multiple prices
                if ( prices.isEmpty() ){
                    String price = e.get(0).text();
                    gamePreview.preorderPrice = stringToPrice(price);
                }
                
                // if more than one price is present
                if ( prices.size() > 1 ) {
                    gamePreview.olderPreorderPrices = new ArrayList<>();
                
                    // memorize the prices
                    for ( int i=0; i<prices.size(); ++i ){                    
                        String price = prices.get(i).text();

                        if ( i==0 ){
                            gamePreview.preorderPrice = stringToPrice(price);
                        } else {
                            gamePreview.olderPreorderPrices.add(stringToPrice(price));
                        }                    
                    }
                }
            }
            
            e = game.getElementsByClass("buyDLC");
            if ( !e.isEmpty() ){                
                Elements prices = e.get(0).getElementsByTag("em");
                
                // if there's just one price
                // NB: <em> tag is present only if there are multiple prices
                if ( prices.isEmpty() ){
                    String price = e.get(0).text();
                    gamePreview.digitalPrice = stringToPrice(price);
                }
                
                // if more than one price is present
                if ( prices.size() > 1 ) {
                    gamePreview.olderDigitalPrices = new ArrayList<>();
                
                    // memorize the prices
                    for ( int i=0; i<prices.size(); ++i ){                    
                        String price = prices.get(i).text();

                        if ( i==0 ){
                            gamePreview.digitalPrice = stringToPrice(price);
                        } else {
                            gamePreview.olderDigitalPrices.add(stringToPrice(price));
                        }                    
                    }
                }
            }
            
            gamePreview.pegi = new ArrayList<>();
            gamePreview.pegi.add( game.getElementsByTag("p").get(0).text() );
            gamePreview.releaseDate = game.getElementsByTag("li").get(0).text().split(": ")[1];
            
            // create the necessary directories
            DirectoryManager.mkdir(gamePreview.id);
            
            // download the cover
            String imageUrl = game.getElementsByClass("prodImg").get(0).getElementsByTag("img").get(0).attr("data-llsrc");
            imageUrl = imageUrl.replace("2med", "3max");  
            DirectoryManager.downloadImage(gamePreview.getCover(), imageUrl);
            
            searchedGames.add(gamePreview);
        }
        
        return searchedGames;
    }
    
    protected static double stringToPrice(String price) {        
        
        // example "Nuovo 19.99€"
        price = price.replaceAll("[^0-9.,]","");    // remove all the characters except for numbers, ',' and '.'
        price = price.replace(".", "");             // to handle prices over 999,99€ like 1.249,99€
        price = price.replace(',', '.');            // to convert the price in a string that can be parsed
        
        return Double.parseDouble(price);
    }
    
}

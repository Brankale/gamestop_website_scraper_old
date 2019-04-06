package gamestopapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
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
        return "GamePreview{" + "id=" + id + ", title=" + title + ", publisher=" + publisher + ", platform=" + platform + ", newPrice=" + newPrice + ", usedPrice=" + usedPrice + ", preorderPrice=" + preorderPrice + ", olderNewPrices=" + olderNewPrices + ", olderUsedPrices=" + olderUsedPrices + '}';
    }

    @Override
    public int compareTo(GamePreview gamePreview) {
        return title.compareTo(gamePreview.getTitle());
    }
    
    public static List<GamePreview> searchGame(String searchedGameName) throws UnsupportedEncodingException, IOException {
        
        List<GamePreview> searchedGames = new ArrayList();
        
        String site = "https://www.gamestop.it";        
        String path = "/SearchResult/QuickSearch";        
        String query = "?q=" + URLEncoder.encode(searchedGameName, "UTF-8");
        String url = site + path + query;
        
        Document doc = null;
        
        try {
            doc = Jsoup.connect(url).get();
        } catch (SocketTimeoutException ste) {
            Log.error("GamePreview","SocketTimeoutException", url);
            return null;
        }
        
        Element body = doc.body();
        
        Elements gamesList = body.getElementsByClass("singleProduct");
        Log.info("GamePreview", "search completed", gamesList.size()+" results" );
        
        for ( Element game : gamesList ) {
            GamePreview gamePreview = new GamePreview();
            
            gamePreview.title = game.getElementsByTag("h3").get(0).text();
            gamePreview.platform = game.getElementsByTag("h3").get(0).getElementsByTag("a").get(0).absUrl("href").split("/")[3];
            searchedGames.add(gamePreview);
        }
        
        return searchedGames;
    }
    
}

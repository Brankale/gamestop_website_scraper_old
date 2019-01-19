package gamestopapp;

import java.awt.Image;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GamePreview {
    
    private String title;
    private String url;
    private String platform;
    private Image cover;        // not definitive (can be just a String with the URL / may break compatibility with Android)

    public GamePreview(String title, String url, String platform, String image) throws MalformedURLException, IOException {
        this.title = title;
        this.url = url;
        this.platform = platform;
        this.cover = ImageIO.read( new URL(image) );
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPlatform() {
        return platform;
    }

    public Image getImage() {
        return cover;
    }

    @Override
    public String toString() {
        return  "Title: " + title + "\n" +
                "URL: " + url + "\n" +
                "Platform: " + platform + "\n";
    }
    
    public static String toString ( List<GamePreview> list ) {
        String str = new String();
        for ( GamePreview gp : list )
            str += gp.toString()+"\n";
        return str;
    }
    
    public static List<GamePreview> searchGame(String searchedGameName) throws UnsupportedEncodingException, IOException {
        
        List<GamePreview> searchedGames = new ArrayList();        
        String site = "https://www.gamestop.it";        
        String path = "/SearchResult/QuickSearch";        
        String query = "?q=" + URLEncoder.encode(searchedGameName, "UTF-8");
        String url = site + path + query;
        
        Document doc = Jsoup.connect(url).get();
        Element body = doc.body();
        
        Elements gamesList = body.getElementsByClass("singleProduct");
        
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

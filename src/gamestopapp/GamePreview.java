package gamestopapp;

import java.util.List;
import java.util.Objects;

public class GamePreview implements Comparable<GamePreview> {
    
    public static final String PATH = "tmp/";
    
    protected String id;
    protected String title;
    protected String publisher;
    protected String platform;
    
    protected Double newPrice;
    protected Double usedPrice;
    protected Double preorderPrice;    
    protected List<Double> olderNewPrices;
    protected List<Double> olderUsedPrices;
    
    protected List<String> pegi;
    protected String releaseDate;
    
    
    
    /*
    private String title;
    private String url;
    private String platform;
    private Image cover;        // not definitive (can be just a String with the URL / may break compatibility with Android)

    public GamePreview(String title, String url, String platform, String image) throws MalformedURLException, IOException {
        this.title = title;
        this.url = url;
        this.platform = platform;
        try {
            this.cover = ImageIO.read( new URL(image) );
        } catch (IOException e) {
            Log.error("GamePreview", "cannot download game cover", image);
        }
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
        
        for(Element game : gamesList){
            String gameImageUrl = game.getElementsByClass("prodImg").get(0).getElementsByTag("img").get(0).absUrl("data-llsrc");
            String gameTitle = game.getElementsByTag("h3").get(0).text();
            String gameUrl = game.getElementsByTag("h3").get(0).getElementsByTag("a").get(0).absUrl("href");
            String gamePlatform = gameUrl.split("/")[3];
            GamePreview previewGame = new GamePreview(gameTitle, gameUrl, gamePlatform, gameImageUrl );
            searchedGames.add(previewGame);
            //Log.debug("GamePreview", "game added");
        }
        
        return searchedGames;
    }
*/

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
    
    public String getURL() {
        return getURLByID(id);
    }
    
    public static String getURLByID ( String id ) {
        return "http://www.gamestop.it/Platform/Games/" + id;
    }
    
    public String getGameDirectory() {
        return PATH + id + "/";
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
    
}

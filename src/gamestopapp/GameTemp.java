package gamestopapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class GameTemp {
    
    private final String PATH = "userData/";
    private final int ATTEMPTS = 5;
    
    private String title;
    private String url;
    private String publisher;
    private String platform;
    private double newPrice;
    private List<Double> olderNewPrices;
    private double usedPrice;
    private List<Double> olderUsedPrices;
    private List<String> pegi;
    private String new_ID;
    private String used_ID;
    private String digital_ID;
    private String presell_ID;
    private List<String> genres;
    private String officialSite;
    private String players;
    private String releaseDate;
    private List<Promo> promo;
    private String description;
    
    /**
     * This function returns a GameTemp object
     * 
     * @param url
     * url of the game
     */
    public GameTemp ( String url )
    {        
        this.url = url;
        
        try {
            
            Document html = Jsoup.connect(url).get();        
            Element body = html.body();
            
            updateMainInfo(body);
            updateMetadata(body);
            updatePrices(body);
            updatePEGI(body);
            //updateBonus(body);
            //updateDescription(body);
            
            mkdir();
            
            updateCover(body);
            updateGallery(body);
            
        } catch (Exception e) {
            try {
                
                // create log directory if doesn't exist
                File directory = new File("log");
                if ( !directory.exists() )
                    directory.mkdir();
                
                // save the information of the crash in the log                
                String fileName = "log/"+System.currentTimeMillis()+".txt";
                FileWriter fw = new FileWriter (fileName, true);
                PrintWriter pw = new PrintWriter (fw);
                
                pw.write(url+"\n\n");
                e.printStackTrace (pw);
                fw.close();
                
                Log.error("GameTemp", "crash log created");
                
            } catch (IOException ex) {
                Log.error("GameTemp", "error during creation of crash log");
            }
        }
    }

    public String getPATH() {
        return PATH;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPlatform() {
        return platform;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public List<Double> getOlderNewPrices() {
        return olderNewPrices;
    }

    public double getUsedPrice() {
        return usedPrice;
    }

    public List<Double> getOlderUsedPrices() {
        return olderUsedPrices;
    }

    public List<String> getPegi() {
        return pegi;
    }

    public String getNew_ID() {
        return new_ID;
    }

    public String getUsed_ID() {
        return used_ID;
    }

    public String getDigital_ID() {
        return digital_ID;
    }

    public String getPresell_ID() {
        return presell_ID;
    }
    
    public String getID () {
        if ( new_ID != null )
            return new_ID;
        if ( digital_ID != null )
            return digital_ID;
        if ( presell_ID != null )
            return presell_ID;
        return null;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getOfficialSite() {
        return officialSite;
    }

    public String getPlayers() {
        return players;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Promo> getPromo() {
        return promo;
    }

    public String getDescription() {
        return description;
    }
    
    
    
    /**
     * This function set these attributes:
     * title, publisher, platform 
     * 
     * @param prodTitle
     * The function accept any Element but, it would be
     * correct to pass directly an Element of class "prodTitle"
     * 
     * @return
     * true - if all went correctly <br>
     * false - if there's no Element of class "prodTitle" <br>
     */
    private boolean updateMainInfo (Element prodTitle) {
        
        // if the element hasn't got the class name "prodTitle" 
        if ( !prodTitle.className().equals("prodTitle") )
        {
            // search for a tag with this class name
            if ( prodTitle.getElementsByClass("prodTitle").isEmpty() )
                return false;
            
            prodTitle = prodTitle.getElementsByClass("prodTitle").get(0);
        }
        
        this.title = prodTitle.getElementsByTag("h1").text();
        this.publisher = prodTitle.getElementsByTag("strong").text();
        this.platform = url.split("/")[3];        
        
        return true;
    }
    
    /**
     * This function set these attributes:
     * genres, IDs, officialSite, players, releaseDate
     * 
     * @param addedDet
     * The function accept any Element but, it would be
     * correct to pass directly an Element of id "addedDet"
     * 
     * @return
     * true - if all went correctly <br>
     * false - if there's no Element of id "addedDet" <br>
     */
    private boolean updateMetadata (Element addedDet) {
        
        // if the element hasn't got the id name "addedDet" 
        if ( !addedDet.id().equals("addedDet") )
        {
            // search for a tag with this id name
            addedDet = addedDet.getElementById("addedDet");
            
            if ( addedDet == null )
                return false;
        }
        
        this.genres = new ArrayList<>();
        
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
                            continue;
                        }
                        
                        if ( id.split(":")[0].equals("Presell") ){
                            this.presell_ID = id.split(":")[1];
                        }
                    }
                    continue;
                }
                
                // set genre
                if ( e.child(0).text().equals("Genere") ) {               
                    String strGenres = e.child(1).text();           // return example: Action/Adventure
                    for ( String genre : strGenres.split("/") )
                        genres.add(genre);
                    continue;
                }
                
                // set official site
                if ( e.child(0).text().equals("Sito Ufficiale") ) {
                    this.officialSite = e.child(1).getElementsByTag("a").attr("href");
                    continue;
                }
                
                // set the number of players
                if ( e.child(0).text().equals("Giocatori") ) {
                    this.players = e.child(1).text();
                    continue;
                }
                
                // set the release date
                if ( e.child(0).text().equals("Rilascio") ) {
                    this.releaseDate = e.child(1).text();
                    this.releaseDate = releaseDate.replace(".","/");
                }                
            }                      
        }
        
        return true;
    }
    
    /**
     * This function set these attributes:
     * newPrice, UsedPrice, olderNewPrices, olderUsedPrices 
     * 
     * @param buySection
     * The function accept any Element but, it would be
     * correct to pass directly an Element of class "buySection"
     * 
     * @return
     * true - if all went correctly <br>
     * false - if there's no Element of class "buySection" <br>
     */
    private boolean updatePrices (Element buySection) {
        
        // if the element hasn't got the class name "buySection" 
        if ( !buySection.className().equals("buySection") )
        {
            // search for a tag with this class name
            if ( buySection.getElementsByClass("buySection").isEmpty() )
                return false;
            
            buySection = buySection.getElementsByClass("buySection").get(0);
        }
        
        this.olderNewPrices = new ArrayList<>();
        this.olderUsedPrices = new ArrayList<>();
        
        for ( Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails") )
        {
            if ( singleVariantDetails.getElementsByClass("singleVariantText").isEmpty() )
                return false;
            
            Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);

            if ( singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo") )
            {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                this.newPrice = stringToPrice(price);
                
                for ( Element olderPrice : singleVariantText.getElementsByClass("olderPrice") ){
                    price = olderPrice.text();
                    this.olderNewPrices.add( stringToPrice(price) );
                }
            }

            if ( singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato") )
            {                
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                this.usedPrice = stringToPrice(price);

                for ( Element olderPrice : singleVariantText.getElementsByClass("olderPrice") ){                    
                    price = olderPrice.text();
                    this.olderUsedPrices.add( stringToPrice(price) );
                }
            }
        }        
        
        return true;        
    }
    
    /**
     * This function set these attributes:
     * pegi
     * 
     * @param ageBlock
     * The function accept any Element but, it would be
     * correct to pass directly an Element of class "ageBlock"
     * 
     * @return
     * true - if all went correctly <br>
     * false - if there's no Element of class "ageBlock" <br>
     */
    private boolean updatePEGI (Element ageBlock) {
        
        // if the element hasn't got the class name "ageBlock" 
        if ( !ageBlock.className().equals("ageBlock") )
        {
            // search for a tag with this class name
            if ( ageBlock.getElementsByClass("ageBlock").isEmpty() )
                return false;
            
            ageBlock = ageBlock.getElementsByClass("ageBlock").get(0);
        }
        
        // init the array
        this.pegi = new ArrayList<>();
        
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
        
        return true;
    }
    
    /**
     * This function is in BETA <br>
     * 
     * This function set these attributes:
     * promo
     * 
     * @param bonusBlock
     * The function accept any Element but, it would be
     * correct to pass directly an Element of class "bonusBlock"
     * 
     * @return
     * true - if all went correctly <br>
     * false - if there's no Element of class "bonusBlock" <br>
     */
    private boolean updateBonus (Element bonusBlock) {
        
        Log.debug("GameTemp", "search for bonusBlock");
        
        // if the element hasn't got the id name "bonusBlock" 
        if ( !bonusBlock.id().equals("bonusBlock") )
        {
            // search for a tag with this id name
            bonusBlock = bonusBlock.getElementById("bonusBlock");
            
            if ( bonusBlock == null )
                return false;
        }
        
        Log.debug("GameTemp", "bonusBlock finded");
        promo = new ArrayList<>();
        
        for ( Element prodSinglePromo : bonusBlock.getElementsByClass("prodSinglePromo") )
        {            
            Elements h4 = prodSinglePromo.getElementsByTag("h4");
            Elements p = prodSinglePromo.getElementsByTag("p");
            
            // possible NullPointerException
            promo.add( new Promo( h4.text(), p.text() ) );
            
            // per il momento non voglio correggere questo errore perchè
            // mi servono molti casi di test
        }        
        
        return true;
    }
    
    /**
     * This function set these attributes:
     * description
     * 
     * @param prodDesc
     * The function accept any Element but, it would be
     * correct to pass directly an Element of id "prodDesc"
     * 
     * @return
     * true - if all went correctly <br>
     * false - if there's no Element of id "prodDesc" <br>
     */
    private boolean updateDescription (Element prodDesc) {
        
        // if the element hasn't got the id name "addedDet" 
        if ( !prodDesc.id().equals("prodDesc") )
        {
            // search for a tag with this id name
            prodDesc = prodDesc.getElementById("prodDesc");
            
            if ( prodDesc == null )
                return false;
        }
        
        this.description = new String();
        
        for ( Element e : prodDesc.getElementsByTag("p") ){
            for ( TextNode tn : e.textNodes() ){
                description += tn.text()+"\n";
            }
        }
        
        return true;
    }
    
    /**
     * Create the game folder
     */
    private void mkdir()
    {
        // create the game folder
        File dir = new File( PATH + getID() );
        
        if ( !dir.exists() )
            dir.mkdir();
    }
    
    /**
     * 
     * @param prodImgMax
     * @return 
     */
    private boolean updateCover (Element prodImgMax) {
        
        // if the element hasn't got the class name "prodImg max" 
        if ( !prodImgMax.className().equals("prodImg max") )
        {
            // search for a tag with this class name
            if ( prodImgMax.getElementsByClass("prodImg max").isEmpty() )
                return false;
            
            prodImgMax = prodImgMax.getElementsByClass("prodImg max").get(0);
        }
        
        String imgUrl = prodImgMax.attr("href");
        String imgPath = PATH + getID();
        
        try {
            return downloadImg("cover.jpg", imgUrl, imgPath);
        } catch (InterruptedException ex) {
            Logger.getLogger(GameTemp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    /**
     * 
     * @param mediaImages
     * @return 
     */
    private boolean updateGallery (Element mediaImages) {
        
        // if the element hasn't got the class name "mediaImages" 
        if ( !mediaImages.className().equals("mediaImages") )
        {
            // search for a tag with this class name
            if ( mediaImages.getElementsByClass("mediaImages").isEmpty() )
                return false;
            
            mediaImages = mediaImages.getElementsByClass("mediaImages").get(0);
        }
        
        String imgPath = PATH + getID() + "/" + "gallery";
        
        File dir = new File (imgPath);
        if ( !dir.exists() )
            dir.mkdir();
        
        for ( Element e : mediaImages.getElementsByTag("a") )
        {            
            String imageUrl = e.attr("href");
            if ( imageUrl.equals("") ){
                // this can handle very rare cases of malformed HTMLs
                // ex. https://www.gamestop.it/Varie/Games/95367/cambio-driving-force-per-volanti-g29-e-g920
                imageUrl = e.getElementsByTag("img").get(0).attr("src");
            }
            
            String imgName = imageUrl.split("/")[6];
            try {
                downloadImg(imgName, imageUrl, imgPath);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameTemp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return true;        
    }
    
    /**
     * 
     *
     * @return 
     * @throws java.io.IOException 
     */
    public boolean update () throws IOException {
        
        Document html = Jsoup.connect(url).get();        
        Element body = html.body();
        boolean result;
        
        result = updateMetadata(body);
        if ( result == false )
            Log.error("GameTemp", "updateMetadata() failed during update", url);
        
        result = updatePrices(body);
        if ( result == false )
            Log.error("GameTemp", "updatePrices() failed during update", url);        
        
        result = updateBonus(body);
        if ( result == false )
            Log.error("GameTemp", "updateBonus() failed during update", url);
        
        return false;
    }
    
    private double stringToPrice ( String price )
    {      
        price = price.replace(',', '.');
        price = price.replace("€", "");
        price = price.replace("CHF", "");
        price = price.trim();
        
        return Double.parseDouble(price);
    }

    @Override
    public String toString() {
        return "GameTemp {\n" + " title = " + title + "\n url = " + url + "\n publisher = " + publisher + "\n platform = " + platform + "\n newPrice = " + newPrice + "\n olderNewPrices = " + olderNewPrices + "\n usedPrice = " + usedPrice + "\n olderUsedPrices = " + olderUsedPrices + "\n pegi = " + pegi + "\n new_ID = " + new_ID + "\n used_ID = " + used_ID + "\n digital_ID = " + digital_ID + "\n presell_ID = " + presell_ID + "\n genres = " + genres + "\n officialSite = " + officialSite + "\n players = " + players + "\n releaseDate = " + releaseDate + "\n promo = " + promo + "\n description {\n" + description + "}\n}";
    }
    
    private boolean downloadImg ( String name, String imgUrl, String imgPath ) throws InterruptedException
    {
        imgPath = imgPath + "/" + name;
        File f = new File (imgPath);
        
        // if the image already exists
        if ( f.exists() ){
            Log.warning("GameTemp", "img already exists", imgPath);
            return true;
        }        
        
        boolean downloaded = false;
        int attempts = 0;
        
        // download the image
        do {            
            try {
                InputStream in = new URL(imgUrl).openStream();
                Files.copy(in, Paths.get(imgPath));
                Log.info("GameTemp", "image downloaded", imgUrl);
                downloaded = true;
            } catch (IOException e) {
                attempts++;
                Log.error("GameTemp", "failed to download the img", imgUrl);
                Log.error("GameTemp", "Retry ...", imgUrl);
                Thread.sleep(1000);
            }            
        } while ( downloaded == false && attempts < ATTEMPTS );
        
        if ( downloaded == true && attempts > 0 )
            Log.debug("GameTemp", "finalmente l'ho scaricata");
        
        if ( downloaded == false )
            Log.error("GameTemp", "cannot download the image", imgUrl);
        
        return downloaded == true; 
    }
    
}

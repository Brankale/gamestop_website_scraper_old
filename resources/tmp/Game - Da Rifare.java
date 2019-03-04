package gamestopapp;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import sun.security.acl.OwnerImpl;

public class Game implements Comparable<Game>, Serializable {

    private static final String PATH = "userData/";

    private String id;
    private String title;
    private String publisher;
    private String platform;
    private double newPrice;
    private List<Double> olderNewPrices;
    private double usedPrice;
    private List<Double> olderUsedPrices;
    private List<String> pegi;
    private List<String> genres;
    private String officialSite;
    private String players;
    private String releaseDate;
    private List<Promo> promo;
    private String description;
    private boolean available;

    public Game(String url) throws IOException {
        
        this.id = url.split("/")[5];
        
        Document html = Jsoup.connect(url).get();
        Element body = html.body();
        
        // these three methods are necessary to create a Game
        updateMainInfo(body);
        updateMetadata(body);
        updatePrices(body);

        // the following information are not necessary to create a game
        updatePEGI(body);
        updateBonus(body);
        updateDescription(body);

        mkdir();
        updateCover(body);
        updateGallery(body);
    }

    public String getTitle() {
        return title;
    }

    public String getURL() {
        return "www.gamestop.it/Platform/Games/" + getID();
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

    public String getID() {
        return id;
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
    
    public String getGamePath () {
        return PATH + getID() + "/";
    }

    public boolean hasPromo() {
        return !promo.isEmpty();
    }
    
    public boolean isAvailable() {
        return available;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final Game other = (Game) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int compareTo(Game game) {
        return this.getTitle().compareTo(game.getTitle());
    }

    @Override
    public String toString() {
        return "Game {\n" +" ID = " + id + "\n title = " + title + "\n publisher = " + publisher + "\n platform = " + platform + "\n newPrice = " + newPrice + "\n olderNewPrices = " + olderNewPrices + "\n usedPrice = " + usedPrice + "\n olderUsedPrices = " + olderUsedPrices + "\n pegi = " + pegi + "\n ID = " + getID() + "\n genres = " + genres + "\n officialSite = " + officialSite + "\n players = " + players + "\n releaseDate = " + releaseDate + "\n promo = " + promo + "\n description {\n" + description + "}\n}";
    }

    /**
     * returned value checked
     * @param prodTitle
     * @return 
     */
    private boolean updateMainInfo(Element prodTitle) {
        
        boolean changes = false;

        // if the element hasn't got the class name "prodTitle" 
        if (!prodTitle.className().equals("prodTitle")) {
            // search for a tag with this class name
            if (prodTitle.getElementsByClass("prodTitle").isEmpty()) {
                throw new GameException();
            }

            prodTitle = prodTitle.getElementsByClass("prodTitle").get(0);
        }
        
        String tmp = title;
        this.title = prodTitle.getElementsByTag("h1").text();
        if ( !title.equals(tmp) )
            changes = true;
        
        tmp = publisher;
        this.publisher = prodTitle.getElementsByTag("strong").text();
        if ( !publisher.equals(tmp) )
            changes = true;
        
        tmp = platform;
        this.platform = prodTitle.getElementsByTag("p").get(0).getElementsByTag("span").text();
        if ( !platform.equals(tmp) )
            changes = true;

        return changes;
    }
    
    /**
     * returned value checked
     * @param addedDet
     * @return 
     */
    private boolean updateMetadata(Element addedDet) {
        
        boolean changes = false;

        // if the element hasn't got the id name "addedDet" 
        if (!addedDet.id().equals("addedDet")) {
            // search for a tag with this id name
            addedDet = addedDet.getElementById("addedDet");

            if (addedDet == null) {
                throw new GameException();
            }
        }
        
        List<String> genresCopy = genres;
        if ( genresCopy == null )
            genresCopy = new ArrayList<>();
        
        this.genres = new ArrayList<>();

        for (Element e : addedDet.getElementsByTag("p")) {
            // important check to avoid IndexOutOfBound Exception
            if (e.childNodeSize() > 1) {
                
                // set item ID (DEPRECATED)
                if (e.child(0).text().equals("Codice articolo")) {
                    /*
                    for (Node n : e.childNodes()) {
                        String id = n.toString();

                        id = id.replace("<span>", "");
                        id = id.replace("</span>", "");
                        id = id.replace("<em>", "");
                        id = id.replace("</em>", "");
                        id = id.replace(" ", "");

                        if (id.split(":")[0].equals("Nuovo")) {
                            this.newID = id.split(":")[1];
                            continue;
                        }

                        if (id.split(":")[0].equals("Usato")) {
                            this.usedID = id.split(":")[1];
                            continue;
                        }

                        // NB: "ContenutoDigitale" should be like this "Contenuto Digitale"
                        // but before I removed the spaces, so it must be written like this
                        if (id.split(":")[0].equals("ContenutoDigitale")) {
                            this.digitalID = id.split(":")[1];
                            continue;
                        }

                        if (id.split(":")[0].equals("Presell")) {
                            this.presellID = id.split(":")[1];
                        }
                    }
                    */
                    continue;
                }

                // set genre
                if (e.child(0).text().equals("Genere")) {
                    String strGenres = e.child(1).text();           // return example: Action/Adventure
                    for (String genre : strGenres.split("/")) {
                        genres.add(genre);
                    }
                    continue;
                }

                // set official site
                if (e.child(0).text().equals("Sito Ufficiale")) {
                    String officialSiteCopy = officialSite;
                    this.officialSite = e.child(1).getElementsByTag("a").attr("href");
                    if ( !officialSite.equals(officialSiteCopy) )
                        changes = true;
                    continue;
                }

                // set the number of players
                if (e.child(0).text().equals("Giocatori")) {
                    String playersCopy = players;
                    this.players = e.child(1).text();
                    if ( !players.equals(playersCopy) )
                        changes = true;
                    continue;
                }

                // set the release date
                if (e.child(0).text().equals("Rilascio")) {
                    String releaseDateCopy = releaseDate;
                    this.releaseDate = e.child(1).text();
                    this.releaseDate = releaseDate.replace(".", "/");
                    if ( !releaseDate.equals(releaseDateCopy) )
                        changes = true;
                }
            }
        }
        
        
        if ( !genres.equals(genresCopy) )
            changes = true;

        return changes;
    }

    /**
     * returned value checked
     * @param buySection
     * @return 
     */
    private boolean updatePrices(Element buySection) {
        
        boolean changes = false;

        // if the element hasn't got the class name "buySection" 
        if (!buySection.className().equals("buySection")) {
            // search for a tag with this class name
            if (buySection.getElementsByClass("buySection").isEmpty()) {
                throw new GameException();
            }

            buySection = buySection.getElementsByClass("buySection").get(0);
        }
        
        List<Double> olderNewPricesCopy = olderNewPrices;
        List<Double> olderUsedPricesCopy = olderUsedPrices;
        
        if ( olderNewPricesCopy == null )
            olderNewPricesCopy = new ArrayList<>();
        
        if ( olderUsedPricesCopy == null )
            olderUsedPricesCopy = new ArrayList<>();
        
        this.olderNewPrices = new ArrayList<>();
        this.olderUsedPrices = new ArrayList<>();

        for (Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails")) {
            
            if (singleVariantDetails.getElementsByClass("singleVariantText").isEmpty()) {
                throw new GameException();
            }

            Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                
                Double newPriceCopy = newPrice;
                this.newPrice = stringToPrice(price);
                if ( newPriceCopy != newPrice )
                    changes = true;

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {
                    price = olderPrice.text();
                    this.olderNewPrices.add(stringToPrice(price));
                }
            }

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                
                Double usedPriceCopy = usedPrice;
                this.usedPrice = stringToPrice(price);
                if ( usedPriceCopy != usedPrice )
                    changes = true;

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {
                    price = olderPrice.text();
                    this.olderUsedPrices.add(stringToPrice(price));
                }
            }
        }        
        
        // sposta in un'altra funzione
        Element btnAddToCart = buySection.getElementById("btnAddToCart");
        if ( btnAddToCart != null ){            
            String style = btnAddToCart.attr("style");
            if ( style.equals("display: block;") ){
                if ( available == false )
                    changes = true;
                
                available = true;
            } else {
                if ( available == true )
                    changes = true;
                
                available = false;
            }
        }            
        
        if ( !olderNewPricesCopy.equals(olderNewPrices) )
            changes = true;
        
        if ( !olderUsedPricesCopy.equals(olderUsedPrices) )
            changes = true;

        return changes;
    }

    private double stringToPrice(String price) {
        price = price.replace(',', '.');
        price = price.replace("€", "");
        price = price.replace("CHF", "");
        price = price.trim();

        return Double.parseDouble(price);
    }

    /**
     * returned value checked
     * @param ageBlock
     * @return 
     */
    private boolean updatePEGI(Element ageBlock) {
        
        boolean changes = false;

        // if the element hasn't got the class name "ageBlock" 
        if (!ageBlock.className().equals("ageBlock")) {
            // search for a tag with this class name
            if (ageBlock.getElementsByClass("ageBlock").isEmpty()) {
                return changes;
            }

            ageBlock = ageBlock.getElementsByClass("ageBlock").get(0);
        }

        // init the array
        List<String> pegiCopy = pegi;
        if ( pegiCopy == null )
            pegiCopy =  new ArrayList<>();
        
        this.pegi = new ArrayList<>();

        for (Element e : ageBlock.getAllElements()) {
            String str = e.attr("class");

            if (str.equals("pegi18"))   { this.pegi.add("pegi18"); continue; }
            if (str.equals("pegi16"))   { this.pegi.add("pegi16"); continue; }
            if (str.equals("pegi12"))   { this.pegi.add("pegi12"); continue; }
            if (str.equals("pegi7"))    { this.pegi.add("pegi7"); continue; }
            if (str.equals("pegi3"))    { this.pegi.add("pegi3"); continue; }

            if (str.equals("ageDescr BadLanguage"))     { this.pegi.add("bad-language"); continue; }
            if (str.equals("ageDescr violence"))        { this.pegi.add("violence"); continue; }
            if (str.equals("ageDescr online"))          { this.pegi.add("online"); continue; }
            if (str.equals("ageDescr sex"))             { this.pegi.add("sex"); continue; }
            if (str.equals("ageDescr fear"))            { this.pegi.add("fear"); continue; }
            if (str.equals("ageDescr drugs"))           { this.pegi.add("drugs"); continue; }
            if (str.equals("ageDescr discrimination"))  { this.pegi.add("discrimination"); continue; }
            if (str.equals("ageDescr gambling"))        { this.pegi.add("gambling"); }
        }

        if ( !pegi.equals(pegiCopy) )
            changes = true;
        
        return changes;
    }

    /**
     * returned value checked
     * @param bonusBlock
     * @return 
     */
    private boolean updateBonus(Element bonusBlock) {
        
        boolean changes = false;

        // if the element hasn't got the id name "bonusBlock" 
        if (!bonusBlock.id().equals("bonusBlock")) {
            // search for a tag with this id name
            bonusBlock = bonusBlock.getElementById("bonusBlock");

            if (bonusBlock == null) {
                return changes;
            }
        }
        
        List<Promo> promoCopy = promo;
        if ( promoCopy == null )
            promoCopy = new ArrayList<>();
        
        promo = new ArrayList<>();

        for (Element prodSinglePromo : bonusBlock.getElementsByClass("prodSinglePromo")) {
            
            Elements h4 = prodSinglePromo.getElementsByTag("h4");
            Elements p = prodSinglePromo.getElementsByTag("p");

            // possible NullPointerException
            String header = h4.text();
            String validity = p.get(0).text();
            String message = p.get(1).text();
            String messageURL = "www.gamestop.it" + p.get(1).getElementsByTag("a").attr("href");
            
            promo.add(new Promo(header, validity, message, messageURL));

            // per il momento non voglio correggere questo errore perchè
            // mi servono molti casi di test
        }
        
        if ( !promo.equals(promoCopy) )
            changes = true;

        return changes;
    }

    /**
     * returned value checked
     * @param prodDesc
     * @return 
     */
    private boolean updateDescription(Element prodDesc) {
        
        boolean changes = false;

        // if the element hasn't got the id name "addedDet" 
        if (!prodDesc.id().equals("prodDesc")) {
            // search for a tag with this id name
            prodDesc = prodDesc.getElementById("prodDesc");

            if (prodDesc == null) {
                return changes;
            }
        }

        String descriptionCopy = this.description;
        if ( descriptionCopy == null )
            descriptionCopy = new String();        
        
        this.description = new String();

        for (Element e : prodDesc.getElementsByTag("p")) {
            for (TextNode tn : e.textNodes()) {
                description += tn.text() + "\n";
            }
        }
        
        if ( !description.equals(descriptionCopy) )
            changes = true;

        return changes;
    }

    /**
     * Create the game folder
     */
    private void mkdir() {
        // create userData folder if doesn't exist
        File dir = new File(PATH);

        if (!dir.exists()) {
            dir.mkdir();
        }

        // create the game folder if doesn't exist
        dir = new File(PATH + getID());

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * 
     * @param prodImgMax 
     */
    private void updateCover(Element prodImgMax) {

        // if the element hasn't got the class name "prodImg max" 
        if (!prodImgMax.className().equals("prodImg max")) {
            // search for a tag with this class name
            if (prodImgMax.getElementsByClass("prodImg max").isEmpty()) {
                return;
            }

            prodImgMax = prodImgMax.getElementsByClass("prodImg max").get(0);
        }

        String imgUrl = prodImgMax.attr("href");
        String imgPath = PATH + getID();

        try {
            downloadImage("cover.jpg", imgUrl, imgPath);
        } catch (IOException ex) {
            Log.error("Game", "cannot download cover", imgUrl);
        }
    }

    /**
     *
     * @param mediaImages
     */
    private void updateGallery(Element mediaImages) {

        // if the element hasn't got the class name "mediaImages" 
        if (!mediaImages.className().equals("mediaImages")) {
            // search for a tag with this class name
            if (mediaImages.getElementsByClass("mediaImages").isEmpty()) {
                return;
            }

            mediaImages = mediaImages.getElementsByClass("mediaImages").get(0);
        }

        String imgPath = PATH + getID() + "/" + "gallery";

        File dir = new File(imgPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (Element e : mediaImages.getElementsByTag("a")) {
            String imgUrl = e.attr("href");
            if (imgUrl.equals("")) {
                // this can handle very rare cases of malformed HTMLs
                // ex. https://www.gamestop.it/Varie/Games/95367/cambio-driving-force-per-volanti-g29-e-g920
                imgUrl = e.getElementsByTag("img").get(0).attr("src");
            }

            String imgName = imgUrl.split("/")[6];

            try {
                downloadImage(imgName, imgUrl, imgPath);
            } catch (IOException ex) {
                Log.error("Game", "cannot download cover", imgUrl);
            }
        }

    }

    private void downloadImage(String name, String imgUrl, String imgPath) throws MalformedURLException, IOException {
        imgPath = imgPath + "/" + name;
        File f = new File(imgPath);

        // if the image already exists
        if (f.exists()) {
            Log.warning("Game", "img already exists", imgPath);
            return;
        }

        InputStream in = new URL(imgUrl).openStream();
        Files.copy(in, Paths.get(imgPath));
        Log.info("Game", "image downloaded", imgUrl);
    }

    /**
     *
     * @throws IOException
     */
    public void update() throws IOException {
        
        Document html = Jsoup.connect( getURL() ).get();
        Element body = html.body();
        
        if ( updateMetadata(body) == true ){
            Log.debug("Game", getTitle() + ": Metadata have changed");
        }
        
        if ( updatePrices(body) == true ) {
            Log.debug("Game", getTitle() + ": Prices have changed");
        }
        
        if ( updateBonus(body) == true ){
            Log.debug("Game", getTitle() + ": Promo has changed");
        }
        
        return;
    }
    
    public void exportBinary() throws IOException
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream( getGamePath() + "data.dat"));
        
        oos.writeObject( this );
        
        Log.info("Game", "exported to binary");
        oos.close();
    }
    
    public static Game importBinary( String path ) throws FileNotFoundException, IOException, ClassNotFoundException
    {        
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        
        Game game = null;    
        boolean eof = false;
        
        while(!eof){
            try{
                game = (Game)ois.readObject();
            }catch(EOFException e){
                eof = true;
            }
        }
        
        Log.info("Game", "imported from binary");
        return game;
    }
    
    public void exportXML() throws Exception{
        
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        
        doc.appendChild(exportXML(doc));
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        File f = new File("data.xml");
        transformer.transform( new DOMSource(doc), new StreamResult(f));
       
        
    }
    
    public org.w3c.dom.Element exportXML(org.w3c.dom.Document doc){
        
        org.w3c.dom.Element game = doc.createElement("game");
        
        //Element Title
        org.w3c.dom.Element elementTitle = doc.createElement("title");
        elementTitle.setTextContent(this.title);
        game.appendChild(elementTitle);
        
        /*
        //Element URL
        org.w3c.dom.Element elementUrl = doc.createElement("url");
        elementUrl.setTextContent(this.url);
        game.appendChild(elementUrl);*/
        
        //Element Publisher
        org.w3c.dom.Element elementPublisher = doc.createElement("publisher");
        elementPublisher.setTextContent(this.publisher);
        game.appendChild(elementPublisher);
        
        //Element Platform
        org.w3c.dom.Element elementPlatform = doc.createElement("platform");
        elementPlatform.setTextContent(this.platform);
        game.appendChild(elementPlatform);
        
        //Element NewPrice
        if(this.newPrice > -1){
            org.w3c.dom.Element elementNewPrice = doc.createElement("newPrice");
            elementNewPrice.setTextContent(String.valueOf(this.newPrice));
            game.appendChild(elementNewPrice); 
        }
        
        //Element OlderNewPrices
        if(this.olderNewPrices != null && this.olderNewPrices.size() > 0){
            org.w3c.dom.Element elementOlderNewPrice = doc.createElement("olderNewPrices");
            
            for(Double price : this.olderNewPrices){
                org.w3c.dom.Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderNewPrice.appendChild(elementPrice);
            }
            
            game.appendChild(elementOlderNewPrice);
        }
        
        //Element UsedPrice
        if(this.usedPrice > -1){
            org.w3c.dom.Element elementUsedPrice = doc.createElement("usedPrice");
            elementUsedPrice.setTextContent(String.valueOf(this.usedPrice));
            game.appendChild(elementUsedPrice); 
        }
        
        //Element OlderUsedPrices
        if(this.olderUsedPrices != null && this.olderUsedPrices.size() > 0){
            org.w3c.dom.Element elementOlderUsedPrice = doc.createElement("olderUsedPrices");
            
            for(Double price : this.olderUsedPrices){
                org.w3c.dom.Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderUsedPrice.appendChild(elementPrice);
            }
            
            game.appendChild(elementOlderUsedPrice);
        }
        
        //Element Pegi
        if(pegi != null){
            org.w3c.dom.Element elementPegiList = doc.createElement("pegiList");
            for(String p : this.pegi){
                org.w3c.dom.Element elementPegi = doc.createElement("pegi");
                elementPegi.setTextContent(p);
                elementPegiList.appendChild(elementPegi);
            }
            game.appendChild(elementPegiList);
        }
        
        /*
        //Element NewId
        if(this.new_ID != null){
            org.w3c.dom.Element elementNewId = doc.createElement("newId");
            elementNewId.setTextContent(this.new_ID);
            game.appendChild(elementNewId);
        }
        
        //Element DigitalId
        if(this.digital_ID != null){
            org.w3c.dom.Element elementDigitalId = doc.createElement("digitalId");
            elementDigitalId.setTextContent(this.digital_ID);
            game.appendChild(elementDigitalId);
        }
        
        //Element UsedId
        if(this.used_ID != null){
            org.w3c.dom.Element elementUsedId = doc.createElement("usedId");
            elementUsedId.setTextContent(this.used_ID);
            game.appendChild(elementUsedId);
        }
        
        //Element PresellId
        if(this.presell_ID!= null){
            org.w3c.dom.Element elementPresellId = doc.createElement("presellId");
            elementPresellId.setTextContent(this.presell_ID);
            game.appendChild(elementPresellId);
        }
        */
        
        //Element Genres
        if(this.genres != null && this.genres.size() > 0){
            org.w3c.dom.Element elementGenres = doc.createElement("genres");
            
            for(String genre : this.genres){
                org.w3c.dom.Element elementGenre = doc.createElement("genre");
                elementGenre.setTextContent(genre);
                elementGenres.appendChild(elementGenre);
            }
            
            game.appendChild(elementGenres);
        }
        
        //Element OfficialSite
        if(this.officialSite != null){
            org.w3c.dom.Element elementOfficialSite = doc.createElement("officialSite");
            elementOfficialSite.setTextContent(this.officialSite);
            game.appendChild(elementOfficialSite);
        }
        
        //Element Players
        if(this.players != null){
            org.w3c.dom.Element elementPlayers = doc.createElement("players");
            elementPlayers.setTextContent(this.players);
            game.appendChild(elementPlayers);
        }
        
        //Element ReleaseDate
        org.w3c.dom.Element elementReleaseDate = doc.createElement("releaseDate");
        elementReleaseDate.setTextContent(this.releaseDate);
        game.appendChild(elementReleaseDate);
        
        return game;
        
    }
    
    
    public Game(){
        this.id = "adasd";
        this.title="adsd";
        this.publisher = "adasd";
        this.platform = "dasda";
    }
}

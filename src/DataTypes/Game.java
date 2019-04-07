package DataTypes;

import gamestopapp.DirectoryManager;
import gamestopapp.GameException;
import gamestopapp.Log;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.w3c.dom.CDATASection;
import org.xml.sax.SAXException;

public class Game extends GamePreview {

    private List<String> genres;
    private String officialSite;
    private String players;
    private boolean validForPromotions;
    
    private List<Promo> promo;
    private String description;
    
    private Game (){
        // used by importXML()
    }

    public Game(String url) throws IOException {
        
        this.id = url.split("/")[5];
        
        Document html = Jsoup.connect(url).get();
        Element body = html.body();
        
        updateMainInfo(body);
        updateMetadata(body);
        updatePrices(body);

        Log.info("Game", "Game found", id + ": \"" + title + "\"");
        
        // the following information are not necessary to create a game
        updatePEGI(body);
        updateBonus(body);
        updateDescription(body);

        mkdir();
        updateCover(body);
        updateGallery(body);
    }

    public List<String> getGenres() {
        return genres;
    }
    
    public boolean hasGenres() {
        if ( genres == null )
            return false;
        return genres.size() > 0;
    }

    public String getOfficialSite() {
        return officialSite;
    }
    
    public boolean hasOfficialSite() {
        if ( officialSite == null )
            return false;
        return !officialSite.equals("");
    }

    public String getPlayers() {
        return players;
    }
    
    public boolean hasPlayers() {
        if ( players == null )
            return false;
        return !players.equals("");
    }

    public boolean isValidForPromotions() {
        return validForPromotions;
    }

    public List<Promo> getPromo() {
        return promo;
    }
    
    public boolean hasPromo() {
        if ( promo == null )
            return false;
        return promo.size() > 0;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean hasDescription() {
        if ( description == null )
            return false;
        return !description.equals("");
    }
    
    public String getStoreAvailabilityURL () {
        
        // if a game is pre-orderable can't be in the store
        if ( getPreorderPrice() != null )
            return null;
        
        return "www.gamestop.it/StoreLocator/Index?productId=" + getId();
    }
    
    @Override
    public String getGameDirectory() {
        return DirectoryManager.getDirectory(id) + id + "/";
    }
    
    public String getGalleryDirectory() {
        return getGameDirectory() + "gallery/";
    }
    
    public String[] getGallery() {
        
        // salvo i nomi delle immagini
        File file = new File(getGalleryDirectory());
        String[] images = file.list();
        
        // aggiungo il percorso al nome delle immagini
        for ( int i=0; i<images.length; ++i ){
            images[i] = getGalleryDirectory() + images[i];
        }
        
        return images;
    }
    
    public boolean hasGallery() {
        return new File(getGalleryDirectory()).exists();
    }    
    
    @Override
    public String toString () {
        String str = new String();
        
        str += "Game {" + "\n ";        
        str += "id = " + id + "\n ";
        str += "title = " + title + "\n ";
        str += "publisher = " + publisher + "\n ";
        str += "platform = " + platform + "\n ";
        
        if ( hasNewPrice() ){
            str += "newPrice = " + newPrice + "\n ";
            
            if ( hasOlderNewPrices() )
                str += "olderNewPrices = " + olderNewPrices + "\n ";
        }
        
        if ( hasUsedPrice() ){
            str += "usedPrice = " + usedPrice + "\n ";
            
            if ( hasOlderUsedPrices() )
                str += "olderUsedPrices = " + olderUsedPrices + "\n ";
        }
        
        if ( hasPreorderPrice() ){
            str += "preorderPrice = " + preorderPrice + "\n ";
        }
        
        if ( hasDigitalPrice() ){
            str += "digitalPrice = " + digitalPrice + "\n ";
        }
        
        if ( hasPromo() ){
            for ( Promo p : promo )
                str += p + "\n ";
        }
        
        if ( hasPegi() ){
            str += "pegi = " + pegi + "\n ";
        }
        
        if ( hasPegi() ){
            str += "genres = " + genres + "\n ";
        }
        
        if ( hasOfficialSite() ){
            str += "officialSite = " + officialSite + "\n ";
        }
        
        if ( hasPlayers() ){
            str += "players = " + players + "\n ";
        }
        
        if ( hasReleaseDate() ){
            str += "releaseDate = " + releaseDate + "\n ";
        }
        
        if ( hasDescription() ){
            str += "description = " + description + "\n ";
        }
        
        str += "validForPromotions = " + validForPromotions + "\n";
        
        str += "}";
        
        return str;
    }
    
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
    
    private boolean updateMetadata(Element addedDet) {
        
        // the content is inside "addedDetInfo" which is inside "addedDet"
        // the method use "addedDet" instead of "addedDetInfo" because
        // "addedDet" has an ID instead of a class
        
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
                
                if (e.child(0).text().equals("Codice articolo")) {
                    // set item ID (DEPRECATED)
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
        
        // search for a tag with this class name
        if ( !addedDet.getElementsByClass("ProdottoValido").isEmpty() ) {
            this.validForPromotions = true;
            changes = true;
        }
        
        if ( !genres.equals(genresCopy) )
            changes = true;

        return changes;
    }

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
        
        // I make a copy of all the prices before overwriting them
        Double newPriceCopy = this.newPrice;
        Double usedPriceCopy = this.usedPrice;
        Double preorderPriceCopy = this.preorderPrice;
        Double digitalPriceCopy = this.digitalPrice;
                
        // if the prices are removed they don't change
        // example: newPrice is 20€ > then newPrice no longer exist > newPrice is still 20€
        this.newPrice = null;
        this.usedPrice = null;
        this.preorderPrice = null;
        this.digitalPrice = null;
        this.olderNewPrices = null;
        this.olderUsedPrices = null;

        for (Element singleVariantDetails : buySection.getElementsByClass("singleVariantDetails")) {
            
            if (singleVariantDetails.getElementsByClass("singleVariantText").isEmpty()) {
                throw new GameException();
            }

            Element singleVariantText = singleVariantDetails.getElementsByClass("singleVariantText").get(0);

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Nuovo")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                
                this.newPrice = stringToPrice(price);

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {
                    
                    if ( this.olderNewPrices == null ){
                        this.olderNewPrices = new ArrayList<>();
                    }
                    
                    price = olderPrice.text();
                    this.olderNewPrices.add(stringToPrice(price));
                }
            }

            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Usato")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                
                this.usedPrice = stringToPrice(price);

                for (Element olderPrice : singleVariantText.getElementsByClass("olderPrice")) {
                    
                    if ( this.olderUsedPrices == null ){
                        this.olderUsedPrices = new ArrayList<>();
                    }
                    
                    price = olderPrice.text();
                    this.olderUsedPrices.add(stringToPrice(price));
                }
            }
            
            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Prenotazione")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                
                this.preorderPrice = stringToPrice(price);
            }
            
            if (singleVariantText.getElementsByClass("variantName").get(0).text().equals("Contenuto Digitale")) {
                String price = singleVariantText.getElementsByClass("prodPriceCont").get(0).text();
                
                this.digitalPrice = stringToPrice(price);
            }
        }
        
        if ( newPrice != null && !newPrice.equals(newPriceCopy) )
            changes = true;
        
        if ( usedPrice != null && !usedPrice.equals(usedPriceCopy) )
            changes = true;
        
        if ( preorderPrice != null && !preorderPrice.equals(preorderPriceCopy) )
            changes = true;
        
        if ( digitalPrice != null && !digitalPrice.equals(digitalPriceCopy) )
            changes = true;

        return changes;
    }

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
        promo = new ArrayList<>();

        for (Element prodSinglePromo : bonusBlock.getElementsByClass("prodSinglePromo")) {
            
            Elements h4 = prodSinglePromo.getElementsByTag("h4");
            Elements p = prodSinglePromo.getElementsByTag("p");

            // possible NullPointerException
            // per il momento non voglio correggere questo errore perchè
            // mi servono molti casi di test
            String header = h4.text();
            String validity = p.get(0).text();
            String message = null;
            String messageURL = null;
            
            // se la promozione contiene un link per personalizzare l'acquisto
            if ( p.size() > 1 ) {
                message = p.get(1).text();
                messageURL = "www.gamestop.it" + p.get(1).getElementsByTag("a").attr("href");
            }
            
            promo.add(new Promo(header, validity, message, messageURL));
        }
        
        if ( !promo.equals(promoCopy) )
            changes = true;

        return changes;
    }

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
        String imgPath = getGameDirectory();

        try {
            downloadImage("cover.jpg", imgUrl, imgPath);
        } catch ( MalformedURLException ex ) {
            Log.error("Game", "ID: " + getId() + " - malformed URL", imgUrl);
        } catch (IOException ex) {
            Log.error("Game", "cannot download cover", imgUrl);
        }
    }

    private void updateGallery(Element mediaImages) {

        // if the element hasn't got the class name "mediaImages" 
        if (!mediaImages.className().equals("mediaImages")) {
            // search for a tag with this class name
            if (mediaImages.getElementsByClass("mediaImages").isEmpty()) {
                return;
            }

            mediaImages = mediaImages.getElementsByClass("mediaImages").get(0);
        }

        String imgPath = getGalleryDirectory();

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
            } catch ( MalformedURLException ex ) {
                Log.error("Game", "ID: " + getId() + " - malformed URL", imgUrl);
            } catch (IOException ex) {
                Log.error("Game", "cannot download image", imgUrl);
            }
        }

    }

    public void update() throws IOException {
        // not implemented
        Log.warning("Game", "Method not implemented");
    }
    
    public void exportXML() throws ParserConfigurationException, TransformerException, SAXException, IOException {
    	
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        
        doc.appendChild(exportXML(doc));
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        File f = new File( getGameDirectory() + "data.xml" );        
        transformer.transform( new DOMSource(doc), new StreamResult(f) );
        
        Log.info("Game", "Game exported successfully", id + ": \"" + title + "\"");        
        validate(f);
    }
    
    public org.w3c.dom.Element exportXML(org.w3c.dom.Document doc){
        
        org.w3c.dom.Element game = doc.createElement("game");
        
        game.setAttribute("id", this.id);
        
        //Element Title
        org.w3c.dom.Element elementTitle = doc.createElement("title");
        CDATASection cdataTitle = doc.createCDATASection(this.title);
        elementTitle.appendChild(cdataTitle);
        game.appendChild(elementTitle);
        
        
        //Element Publisher
        org.w3c.dom.Element elementPublisher = doc.createElement("publisher");
        CDATASection cdataPublisher = doc.createCDATASection(this.publisher);
        elementPublisher.appendChild(cdataPublisher);
        game.appendChild(elementPublisher);
        
        //Element Platform
        org.w3c.dom.Element elementPlatform = doc.createElement("platform");
        CDATASection cdataPlatform = doc.createCDATASection(this.platform);
        elementPlatform.appendChild(cdataPlatform);
        game.appendChild(elementPlatform);
        
        //Element Prices
        org.w3c.dom.Element prices = doc.createElement("prices");
        
        //Element NewPrice
        if( hasNewPrice() ){
            org.w3c.dom.Element elementNewPrice = doc.createElement("newPrice");
            elementNewPrice.setTextContent(String.valueOf(this.newPrice));
            prices.appendChild(elementNewPrice); 
        }
        
        //Element OlderNewPrices
        if( hasOlderNewPrices() ){
            org.w3c.dom.Element elementOlderNewPrice = doc.createElement("olderNewPrices");
            
            for(Double price : this.olderNewPrices){
                org.w3c.dom.Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderNewPrice.appendChild(elementPrice);
            }
            
            prices.appendChild(elementOlderNewPrice);
        }
        
        //Element UsedPrice
        if( hasUsedPrice() ){
            org.w3c.dom.Element elementUsedPrice = doc.createElement("usedPrice");
            elementUsedPrice.setTextContent(String.valueOf(this.usedPrice));
            prices.appendChild(elementUsedPrice); 
        }
        
        //Element OlderUsedPrices
        if( hasOlderUsedPrices() ){
            org.w3c.dom.Element elementOlderUsedPrice = doc.createElement("olderUsedPrices");
            
            for(Double price : this.olderUsedPrices){
                org.w3c.dom.Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderUsedPrice.appendChild(elementPrice);
            }
            
            prices.appendChild(elementOlderUsedPrice);
        }
        
        //Element PreOrderPrice
        if( hasPreorderPrice() ){
            org.w3c.dom.Element elementPreorderPrice = doc.createElement("preorderPrice");
            elementPreorderPrice.setTextContent(String.valueOf(this.preorderPrice));
            prices.appendChild(elementPreorderPrice); 
        }
        
        //Element DigitalPrice
        if( hasDigitalPrice() ){
            org.w3c.dom.Element elementDigitalPrice = doc.createElement("digitalPrice");
            elementDigitalPrice.setTextContent(String.valueOf(this.digitalPrice));
            prices.appendChild(elementDigitalPrice); 
        }
        
        game.appendChild(prices);
        
        //Element Pegi
        if( hasPegi() ){
            org.w3c.dom.Element elementPegiList = doc.createElement("pegi");
            for(String p : this.pegi){
                org.w3c.dom.Element elementPegi = doc.createElement("type");
                elementPegi.setTextContent(p);
                elementPegiList.appendChild(elementPegi);
            }
            game.appendChild(elementPegiList);
        }
        
        //Element Genres
        if( hasGenres() ){
            org.w3c.dom.Element elementGenres = doc.createElement("genres");
            
            for(String genre : this.genres){
                org.w3c.dom.Element elementGenre = doc.createElement("genre");
                CDATASection cdataGenre = doc.createCDATASection(genre);
                elementGenre.appendChild(cdataGenre);
                elementGenres.appendChild(elementGenre);
            }
            
            game.appendChild(elementGenres);
        }
        
        //Element OfficialSite
        if( hasOfficialSite() ){
            org.w3c.dom.Element elementOfficialSite = doc.createElement("officialSite");
            CDATASection cdataOfficialSite = doc.createCDATASection(this.officialSite);
            elementOfficialSite.appendChild(cdataOfficialSite);
            game.appendChild(elementOfficialSite);
        }
        
        //Element Players
        if( hasPlayers() ){
            org.w3c.dom.Element elementPlayers = doc.createElement("players");
            CDATASection cdataPlayers = doc.createCDATASection(this.players);
            elementPlayers.appendChild(cdataPlayers);
            game.appendChild(elementPlayers);
        }
        
        //Element ReleaseDate
        org.w3c.dom.Element elementReleaseDate = doc.createElement("releaseDate");
        elementReleaseDate.setTextContent(this.releaseDate);
        game.appendChild(elementReleaseDate);        
        
        // promo
        if( hasPromo() ){
            org.w3c.dom.Element elementPromos = doc.createElement("promos");
            
            for(Promo p : this.promo){
                org.w3c.dom.Element elementPromo = doc.createElement("promo");
                
                org.w3c.dom.Element elementHeader = doc.createElement("header");
                CDATASection cdataHeader = doc.createCDATASection(p.getHeader());
                elementHeader.appendChild(cdataHeader);
                elementPromo.appendChild(elementHeader);
                
                org.w3c.dom.Element elementValidity = doc.createElement("validity");
                CDATASection cdataValidity = doc.createCDATASection(p.getValidity());
                elementValidity.appendChild(cdataValidity);
                elementPromo.appendChild(elementValidity);
                
                if(p.getMessage() != null){
                    org.w3c.dom.Element elementMessage = doc.createElement("message");
                    CDATASection cdataMessage = doc.createCDATASection(p.getMessage());
                    elementMessage.appendChild(cdataMessage);
                    elementPromo.appendChild(elementMessage);
                    
                    org.w3c.dom.Element elementMessageURL = doc.createElement("messageURL");
                    CDATASection cdataMessageURL = doc.createCDATASection(p.getMessageURL());
                    elementMessageURL.appendChild(cdataMessageURL);
                    elementPromo.appendChild(elementMessageURL);
                }                
                
                elementPromos.appendChild(elementPromo);
            }
            
            game.appendChild(elementPromos);
        }
        
        //Element Description
        if( hasDescription() ){
            org.w3c.dom.Element elementDescription = doc.createElement("description");
            CDATASection cdataDescription = doc.createCDATASection(this.description);
            elementDescription.appendChild(cdataDescription);
            game.appendChild(elementDescription);
        }
        
        //Element ValidForPromos
        if( isValidForPromotions() ){
            org.w3c.dom.Element elementValidForPromo = doc.createElement("validForPromo");
            elementValidForPromo.setTextContent(""+this.validForPromotions);
            game.appendChild(elementValidForPromo);
        }
        
        return game;        
    }
    
    public static void validate(File f) throws SAXException, IOException {        
        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(DirectoryManager.SCHEMA_GAME));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(f));
    }
    
    public static Game importXML() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("data.xml");      // need revision
        validate(f);
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        org.w3c.dom.Element game = doc.getDocumentElement();
        return importXML(game);
    }
    
    public static Game importXML(org.w3c.dom.Element game){
        
        Game g = new Game();
        
        g.id = game.getAttribute("id");
        
        g.title = game.getElementsByTagName("title").item(0).getChildNodes().item(0).getTextContent();        
        g.publisher = game.getElementsByTagName("publisher").item(0).getChildNodes().item(0).getTextContent();
        g.platform = game.getElementsByTagName("platform").item(0).getChildNodes().item(0).getTextContent();
        
        org.w3c.dom.Element prices = (org.w3c.dom.Element)game.getElementsByTagName("prices").item(0);
        
        //NEW PRICE
        org.w3c.dom.NodeList nl = prices.getElementsByTagName("newPrice");
        if(nl.getLength() > 0){
            org.w3c.dom.Element newPrice = (org.w3c.dom.Element)nl.item(0);
            g.newPrice = Double.valueOf(newPrice.getTextContent());
        }
        
        //OLDER NEW PRICES
        nl = prices.getElementsByTagName("olderNewPrices");
        if(nl.getLength() > 0){
            g.olderNewPrices = new ArrayList();
            org.w3c.dom.Element olderNewPrices = (org.w3c.dom.Element)nl.item(0);
            nl = olderNewPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                org.w3c.dom.Element elementPrice = (org.w3c.dom.Element)nl.item(i);
                g.olderNewPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }
        
        //USED PRICE
        nl = prices.getElementsByTagName("usedPrice");
        if(nl.getLength() > 0){
            org.w3c.dom.Element usedPrice = (org.w3c.dom.Element)nl.item(0);
            g.usedPrice = Double.valueOf(usedPrice.getTextContent());
        }
        
        //OLDER USED PRICES
        nl = prices.getElementsByTagName("olderUsedPrices");
        if(nl.getLength() > 0){
            g.olderUsedPrices = new ArrayList();
            org.w3c.dom.Element olderUsedPrices = (org.w3c.dom.Element)nl.item(0);
            nl = olderUsedPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                org.w3c.dom.Element elementPrice = (org.w3c.dom.Element)nl.item(i);
                g.olderUsedPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }
        
        //PREORDER PRICE
        nl = prices.getElementsByTagName("preorderPrice");
        if(nl.getLength() > 0){
            org.w3c.dom.Element preorderPrice = (org.w3c.dom.Element)nl.item(0);
            g.preorderPrice = Double.valueOf(preorderPrice.getTextContent());
        }
        
        //DIGITAL PRICE
        nl = prices.getElementsByTagName("digitalPrice");
        if(nl.getLength() > 0){
            org.w3c.dom.Element digitalPrice = (org.w3c.dom.Element)nl.item(0);
            g.digitalPrice = Double.valueOf(digitalPrice.getTextContent());
        }
        
        //PEGI
        nl = game.getElementsByTagName("pegi");
        if(nl.getLength() > 0){
            org.w3c.dom.Element pegi = (org.w3c.dom.Element)nl.item(0);
            nl = pegi.getElementsByTagName("type");
            g.pegi = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                org.w3c.dom.Element type = (org.w3c.dom.Element)nl.item(i);
                g.pegi.add(type.getTextContent());
            }
        }
        
        //GENRES
        nl = game.getElementsByTagName("genres");
        if(nl.getLength() > 0){
            org.w3c.dom.Element genres = (org.w3c.dom.Element)nl.item(0);
            nl = genres.getElementsByTagName("genre");
            g.genres = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                org.w3c.dom.Element genre = (org.w3c.dom.Element)nl.item(i);
                g.genres.add(genre.getTextContent());
            }
        }
        
        //OFFICIAL SITE
        nl = game.getElementsByTagName("officialSite");
        if(nl.getLength() > 0){
            org.w3c.dom.Element officialSite = (org.w3c.dom.Element)nl.item(0);
            g.officialSite = officialSite.getChildNodes().item(0).getTextContent();
        }
        
        //PLAYERS
        nl = game.getElementsByTagName("players");
        if(nl.getLength() > 0){
            org.w3c.dom.Element players = (org.w3c.dom.Element)nl.item(0);
            g.players = players.getChildNodes().item(0).getTextContent();
        }
        
        //RELEASE DATE
        nl = game.getElementsByTagName("releaseDate");
        if(nl.getLength() > 0){
            org.w3c.dom.Element releaseDate = (org.w3c.dom.Element)nl.item(0);
            g.releaseDate = releaseDate.getTextContent();
        }
        
        //PROMOS
        nl = game.getElementsByTagName("promos");
        if(nl.getLength() > 0){
            org.w3c.dom.Element promos = (org.w3c.dom.Element)nl.item(0);
            nl = promos.getElementsByTagName("promo");
            g.promo = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                org.w3c.dom.Element promo = (org.w3c.dom.Element)nl.item(i);
                
                String header = promo.getElementsByTagName("header").item(0).getChildNodes().item(0).getTextContent();
                String validity = promo.getElementsByTagName("validity").item(0).getChildNodes().item(0).getTextContent();
                
                String message = null;
                String messageURL = null;
                if(promo.getElementsByTagName("message").getLength() > 0){
                    message = promo.getElementsByTagName("message").item(0).getChildNodes().item(0).getTextContent();
                    messageURL = promo.getElementsByTagName("messageURL").item(0).getChildNodes().item(0).getTextContent();
                }

                Promo p = new Promo(header, validity, message, messageURL);
                g.promo.add(p);
            }
        }
        
        //DESCRIPTION
        nl = game.getElementsByTagName("description");
        if(nl.getLength() > 0){
            org.w3c.dom.Element description = (org.w3c.dom.Element)nl.item(0);
            g.description = description.getChildNodes().item(0).getTextContent();
        }
        
        //VALID FOR PROMO
        nl = game.getElementsByTagName("validForPromo");
        if(nl.getLength() > 0){
            org.w3c.dom.Element validForPromo = (org.w3c.dom.Element)nl.item(0);
            g.validForPromotions = Boolean.valueOf(validForPromo.getTextContent());
        }
        
        return g;
    }

}

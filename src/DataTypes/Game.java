package DataTypes;

import gamestopapp.DirectoryManager;
import gamestopapp.GameException;
import gamestopapp.Log;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class Game extends GamePreview {

    private List<String> genres;
    private String officialSite;
    private String players;
    private boolean validForPromotions;
    
    private List<Promo> promo;
    private String description;

    public Game(String url) throws IOException {
        
        this.id = url.split("/")[5];
        
        Document html = Jsoup.connect(url).get();
        Element body = html.body();
        
        updateMainInfo(body);
        updateMetadata(body);
        updatePrices(body);

        Log.info("Game", "Game found", title);
        
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
        return genres != null;
    }

    public String getOfficialSite() {
        return officialSite;
    }
    
    public boolean hasOfficialSite() {
        return officialSite != null;
    }

    public String getPlayers() {
        return players;
    }
    
    public boolean hasPlayers() {
        return players != null;
    }

    public boolean isValidForPromotions() {
        return validForPromotions;
    }

    public List<Promo> getPromo() {
        return promo;
    }
    
    public boolean hasPromo() {
        return promo != null;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean hasDescription() {
        return description != null;             // <-- controlla se vengono comunque inizializzati o rimangono nulli
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

}

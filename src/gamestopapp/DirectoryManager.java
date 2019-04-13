package gamestopapp;

import DataTypes.Game;
import DataTypes.Games;
import DataTypes.Promo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class DirectoryManager {
    
    private static final String GAMES_DIRECTORY = "userdata/";
    private static final String WISHLIST = GAMES_DIRECTORY + "data.csv";
    
    public static void mkdir(){
        File dir = new File(GAMES_DIRECTORY);
        
        if(!dir.exists()){
            dir.mkdir();
        }
    }
    
    public static String getGamesDirectory() {
        return GAMES_DIRECTORY;
    }

    public static File getWishlist() {
        return new File(WISHLIST);
    }
    
    public static String getGameDirectory(String gameId){
        return "";
    }
    
    public static File getGameXML(String gameId){
        return null;
    }
    
    
    //Game Import-Export
    public void exportGame(Game game) throws ParserConfigurationException, TransformerException, SAXException, IOException {
    	
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        
        doc.appendChild(exportGame(game,doc));
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        File f = getGameXML(game.getId());
        //Non aggiungere controlli per esportare il file XML, perchè se subisce modifiche il gioco, l'XML non si aggiorna
        transformer.transform( new DOMSource(doc), new StreamResult(f) );
        
        Log.info("DirectoryManager", "Game exported successfully", game.getId() + ": \"" + game.getTitle() + "\"");        
        validate(f);
    }
    
    public Element exportGame(Game game, Document doc){
        
        Element gameElement = doc.createElement("game");
        
        gameElement.setAttribute("id", game.getId());
        
        //Element Title
        Element elementTitle = doc.createElement("title");
        CDATASection cdataTitle = doc.createCDATASection(game.getTitle());
        elementTitle.appendChild(cdataTitle);
        gameElement.appendChild(elementTitle);
        
        
        //Element Publisher
        Element elementPublisher = doc.createElement("publisher");
        CDATASection cdataPublisher = doc.createCDATASection(game.getPublisher());
        elementPublisher.appendChild(cdataPublisher);
        gameElement.appendChild(elementPublisher);
        
        //Element Platform
        Element elementPlatform = doc.createElement("platform");
        CDATASection cdataPlatform = doc.createCDATASection(game.getPlatform());
        elementPlatform.appendChild(cdataPlatform);
        gameElement.appendChild(elementPlatform);
        
        //Element Prices
        Element prices = doc.createElement("prices");
        
        //Element NewPrice
        if( game.hasNewPrice() ){
            Element elementNewPrice = doc.createElement("newPrice");
            elementNewPrice.setTextContent(String.valueOf(game.getNewPrice()));
            prices.appendChild(elementNewPrice); 
        }
        
        //Element OlderNewPrices
        if( game.hasOlderNewPrices() ){
            Element elementOlderNewPrice = doc.createElement("olderNewPrices");
            
            for(Double price : game.getOlderNewPrices()){
                Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderNewPrice.appendChild(elementPrice);
            }
            
            prices.appendChild(elementOlderNewPrice);
        }
        
        //Element UsedPrice
        if( game.hasUsedPrice() ){
            Element elementUsedPrice = doc.createElement("usedPrice");
            elementUsedPrice.setTextContent(String.valueOf(game.getUsedPrice()));
            prices.appendChild(elementUsedPrice); 
        }
        
        //Element OlderUsedPrices
        if( game.hasOlderUsedPrices() ){
            Element elementOlderUsedPrice = doc.createElement("olderUsedPrices");
            
            for(Double price : game.getOlderUsedPrices()){
                Element elementPrice = doc.createElement("price");
                elementPrice.setTextContent(price.toString());
                elementOlderUsedPrice.appendChild(elementPrice);
            }
            
            prices.appendChild(elementOlderUsedPrice);
        }
        
        //Element PreOrderPrice
        if( game.hasPreorderPrice() ){
            Element elementPreorderPrice = doc.createElement("preorderPrice");
            elementPreorderPrice.setTextContent(String.valueOf(game.getPreorderPrice()));
            prices.appendChild(elementPreorderPrice); 
        }
        
        //Element DigitalPrice
        if( game.hasDigitalPrice() ){
            Element elementDigitalPrice = doc.createElement("digitalPrice");
            elementDigitalPrice.setTextContent(String.valueOf(game.getDigitalPrice()));
            prices.appendChild(elementDigitalPrice); 
        }
        
        gameElement.appendChild(prices);
        
        //Element Pegi
        if( game.hasPegi() ){
            Element elementPegiList = doc.createElement("pegi");
            for(String p : game.getPegi()){
                Element elementPegi = doc.createElement("type");
                elementPegi.setTextContent(p);
                elementPegiList.appendChild(elementPegi);
            }
            gameElement.appendChild(elementPegiList);
        }
        
        //Element Genres
        if( game.hasGenres() ){
            Element elementGenres = doc.createElement("genres");
            
            for(String genre : game.getGenres()){
                Element elementGenre = doc.createElement("genre");
                CDATASection cdataGenre = doc.createCDATASection(genre);
                elementGenre.appendChild(cdataGenre);
                elementGenres.appendChild(elementGenre);
            }
            
            gameElement.appendChild(elementGenres);
        }
        
        //Element OfficialSite
        if( game.hasOfficialSite() ){
            Element elementOfficialSite = doc.createElement("officialSite");
            CDATASection cdataOfficialSite = doc.createCDATASection(game.getOfficialSite());
            elementOfficialSite.appendChild(cdataOfficialSite);
            gameElement.appendChild(elementOfficialSite);
        }
        
        //Element Players
        if( game.hasPlayers() ){
            Element elementPlayers = doc.createElement("players");
            CDATASection cdataPlayers = doc.createCDATASection(game.getPlayers());
            elementPlayers.appendChild(cdataPlayers);
            gameElement.appendChild(elementPlayers);
        }
        
        //Element ReleaseDate
        Element elementReleaseDate = doc.createElement("releaseDate");
        elementReleaseDate.setTextContent(game.getReleaseDate());
        gameElement.appendChild(elementReleaseDate);        
        
        // promo
        if( game.hasPromo() ){
            Element elementPromos = doc.createElement("promos");
            
            for(Promo p : game.getPromo()){
                Element elementPromo = doc.createElement("promo");
                
                Element elementHeader = doc.createElement("header");
                CDATASection cdataHeader = doc.createCDATASection(p.getHeader());
                elementHeader.appendChild(cdataHeader);
                elementPromo.appendChild(elementHeader);
                
                Element elementValidity = doc.createElement("validity");
                CDATASection cdataValidity = doc.createCDATASection(p.getValidity());
                elementValidity.appendChild(cdataValidity);
                elementPromo.appendChild(elementValidity);
                
                if(p.getMessage() != null){
                    Element elementMessage = doc.createElement("message");
                    CDATASection cdataMessage = doc.createCDATASection(p.getMessage());
                    elementMessage.appendChild(cdataMessage);
                    elementPromo.appendChild(elementMessage);
                    
                    Element elementMessageURL = doc.createElement("messageURL");
                    CDATASection cdataMessageURL = doc.createCDATASection(p.getMessageURL());
                    elementMessageURL.appendChild(cdataMessageURL);
                    elementPromo.appendChild(elementMessageURL);
                }                
                
                elementPromos.appendChild(elementPromo);
            }
            
            gameElement.appendChild(elementPromos);
        }
        
        //Element Description
        if( game.hasDescription() ){
            Element elementDescription = doc.createElement("description");
            CDATASection cdataDescription = doc.createCDATASection(game.getDescription());
            elementDescription.appendChild(cdataDescription);
            gameElement.appendChild(elementDescription);
        }
        
        //Element ValidForPromos
        if( game.isValidForPromotions() ){
            Element elementValidForPromo = doc.createElement("validForPromo");
            elementValidForPromo.setTextContent(""+game.isValidForPromotions());
            gameElement.appendChild(elementValidForPromo);
        }
        
        return gameElement;        
    }
    
    public static void validate(File f) throws SAXException, IOException {        
        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(DirectoryManager.SCHEMA_GAME));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(f));
    }
    
    public static Game importGame(String gameId) throws IOException, ParserConfigurationException, SAXException {
        File f = new File(DirectoryManager.getGameDirectory(gameId)+"data.xml");      // need revision
        validate(f);
        org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        Element game = doc.getDocumentElement();
        return importXML(game);
    }
    
    private static Game importGame(Element game){
        
        Game g = new Game();
        
        g.id = game.getAttribute("id");
        
        g.title = game.getElementsByTagName("title").item(0).getChildNodes().item(0).getTextContent();        
        g.publisher = game.getElementsByTagName("publisher").item(0).getChildNodes().item(0).getTextContent();
        g.platform = game.getElementsByTagName("platform").item(0).getChildNodes().item(0).getTextContent();
        
        Element prices = (Element)game.getElementsByTagName("prices").item(0);
        
        //NEW PRICE
        org.w3c.dom.NodeList nl = prices.getElementsByTagName("newPrice");
        if(nl.getLength() > 0){
            Element newPrice = (Element)nl.item(0);
            g.newPrice = Double.valueOf(newPrice.getTextContent());
        }
        
        //OLDER NEW PRICES
        nl = prices.getElementsByTagName("olderNewPrices");
        if(nl.getLength() > 0){
            g.olderNewPrices = new ArrayList();
            Element olderNewPrices = (Element)nl.item(0);
            nl = olderNewPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                Element elementPrice = (Element)nl.item(i);
                g.olderNewPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }
        
        //USED PRICE
        nl = prices.getElementsByTagName("usedPrice");
        if(nl.getLength() > 0){
            Element usedPrice = (Element)nl.item(0);
            g.usedPrice = Double.valueOf(usedPrice.getTextContent());
        }
        
        //OLDER USED PRICES
        nl = prices.getElementsByTagName("olderUsedPrices");
        if(nl.getLength() > 0){
            g.olderUsedPrices = new ArrayList();
            Element olderUsedPrices = (Element)nl.item(0);
            nl = olderUsedPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                Element elementPrice = (Element)nl.item(i);
                g.olderUsedPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }
        
        //PREORDER PRICE
        nl = prices.getElementsByTagName("preorderPrice");
        if(nl.getLength() > 0){
            Element preorderPrice = (Element)nl.item(0);
            g.preorderPrice = Double.valueOf(preorderPrice.getTextContent());
        }
        
        //DIGITAL PRICE
        nl = prices.getElementsByTagName("digitalPrice");
        if(nl.getLength() > 0){
            Element digitalPrice = (Element)nl.item(0);
            g.digitalPrice = Double.valueOf(digitalPrice.getTextContent());
        }
        
        //PEGI
        nl = game.getElementsByTagName("pegi");
        if(nl.getLength() > 0){
            Element pegi = (Element)nl.item(0);
            nl = pegi.getElementsByTagName("type");
            g.pegi = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element type = (Element)nl.item(i);
                g.pegi.add(type.getTextContent());
            }
        }
        
        //GENRES
        nl = game.getElementsByTagName("genres");
        if(nl.getLength() > 0){
            Element genres = (Element)nl.item(0);
            nl = genres.getElementsByTagName("genre");
            g.genres = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element genre = (Element)nl.item(i);
                g.genres.add(genre.getTextContent());
            }
        }
        
        //OFFICIAL SITE
        nl = game.getElementsByTagName("officialSite");
        if(nl.getLength() > 0){
            Element officialSite = (Element)nl.item(0);
            g.officialSite = officialSite.getChildNodes().item(0).getTextContent();
        }
        
        //PLAYERS
        nl = game.getElementsByTagName("players");
        if(nl.getLength() > 0){
            Element players = (Element)nl.item(0);
            g.players = players.getChildNodes().item(0).getTextContent();
        }
        
        //RELEASE DATE
        nl = game.getElementsByTagName("releaseDate");
        if(nl.getLength() > 0){
            Element releaseDate = (Element)nl.item(0);
            g.releaseDate = releaseDate.getTextContent();
        }
        
        //PROMOS
        nl = game.getElementsByTagName("promos");
        if(nl.getLength() > 0){
            Element promos = (Element)nl.item(0);
            nl = promos.getElementsByTagName("promo");
            g.promo = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element promo = (Element)nl.item(i);
                
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
            Element description = (Element)nl.item(0);
            g.description = description.getChildNodes().item(0).getTextContent();
        }
        
        //VALID FOR PROMO
        nl = game.getElementsByTagName("validForPromo");
        if(nl.getLength() > 0){
            Element validForPromo = (Element)nl.item(0);
            g.validForPromotions = Boolean.valueOf(validForPromo.getTextContent());
        }
        
        return g;
    }
    
    //Games Import-Export
    public void exportGames() throws ParserConfigurationException, TransformerException, SAXException, IOException{
        File f = new File(DirectoryManager.WISHLIST_DIR+"data.csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        for(int i = 0; i<this.size(); i++){
            this.get(i).exportXML();
            bw.write(this.get(i).getId());
            if(i < (this.size()-1)){
                bw.write(",");
            }
        }
        bw.close();
    }
    
    public static Games importGames() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException{
        Games g = new Games();
        File f = new File(DirectoryManager.WISHLIST_DIR+"data.csv");
        BufferedReader br = new BufferedReader(new FileReader(f));
        
        String row = br.readLine();
        String[] ids = row.split(",");
        
        for(String id : ids){
            g.add(Game.importXML(id));
        }
        
        return g;
    }
    
}

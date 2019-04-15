package gamestopapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DirectoryManager {
    
    private static final String GAMES_DIRECTORY = "userdata/";
    private static final String WISHLIST = GAMES_DIRECTORY + "data.csv";
    private static final String SCHEMA_GAME = "resources/xsd/Game.xsd";
    
    public static void mkdir(){
        File dir = new File(GAMES_DIRECTORY);
        
        if(!dir.exists()){
            dir.mkdir();
        }
    }
    
    public static void mkdir(String gameId){        
        mkdir();
        
        File dir = new File(getGameDirectory(gameId));
        
        if(!dir.exists()){
            dir.mkdir();
        }
    }
    
    public static String getGamesDirectory() {
        return GAMES_DIRECTORY;
    }
    
    public static String getGameDirectory(String gameId){
        return getGamesDirectory() + gameId + "/";
    }
    
    public static String getGameGalleryDirectory(String gameId){
        return getGameDirectory(gameId) + "gallery/";
    }
    
    public static File getWishlist() {
        return new File(WISHLIST);
    }
    
    public static File getGameXML(String gameId){
        return new File(getGameDirectory(gameId)+"data.xml");
    }
    
    public static final void downloadImage(String imgPath, String imgURL) throws MalformedURLException, IOException {
        
        File image = new File(imgPath);

        // if the image already exists
        if ( image.exists() ) {
            Log.warning("DirectoryManager", "image already exists", imgPath);
            return;
        }
        
        // download the image
        InputStream in = new URL(imgURL).openStream();
        Files.copy(in, Paths.get(imgPath));
        Log.info("DirectoryManager", "image downloaded", imgURL);
    }
    
    // IMPORT/EXPORT METHODS FOR GAME CLASS -----------------------------------
    
    public static void exportGame(Game game) throws Exception  {
    	
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();        
        doc.appendChild( exportGame(game, doc) );
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        File f = getGameXML(game.getId());
        transformer.transform( new DOMSource(doc), new StreamResult(f) );     
        
        // check the XML
        validateGame(f);
        
        Log.info("DirectoryManager", "Game exported successfully", "["+game.getId()+"]" + "["+game.getPlatform()+"]" + " - \"" + game.getTitle() + "\"");
    }
    
    public static Element exportGame(Game game, Document doc){
        
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
    
    public static Game importGame(String gameId) throws Exception  {
        
        File f = getGameXML(gameId);      // need revision
        
        // check the XML
        validateGame(f);
        
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);        
        return importGame(doc);
    }
    
    private static Game importGame(Document doc){
        
        Game game = new Game();
        
        Element gameElement = doc.getDocumentElement();
        
        game.id = gameElement.getAttribute("id");
        
        game.title = gameElement.getElementsByTagName("title").item(0).getChildNodes().item(0).getTextContent();        
        game.publisher = gameElement.getElementsByTagName("publisher").item(0).getChildNodes().item(0).getTextContent();
        game.platform = gameElement.getElementsByTagName("platform").item(0).getChildNodes().item(0).getTextContent();
        
        Element prices = (Element)gameElement.getElementsByTagName("prices").item(0);
        
        //NEW PRICE
        org.w3c.dom.NodeList nl = prices.getElementsByTagName("newPrice");
        if(nl.getLength() > 0){
            Element newPrice = (Element)nl.item(0);
            game.newPrice = Double.valueOf(newPrice.getTextContent());
        }
        
        //OLDER NEW PRICES
        nl = prices.getElementsByTagName("olderNewPrices");
        if(nl.getLength() > 0){
            game.olderNewPrices = new ArrayList();
            Element olderNewPrices = (Element)nl.item(0);
            nl = olderNewPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                Element elementPrice = (Element)nl.item(i);
                game.olderNewPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }
        
        //USED PRICE
        nl = prices.getElementsByTagName("usedPrice");
        if(nl.getLength() > 0){
            Element usedPrice = (Element)nl.item(0);
            game.usedPrice = Double.valueOf(usedPrice.getTextContent());
        }
        
        //OLDER USED PRICES
        nl = prices.getElementsByTagName("olderUsedPrices");
        if(nl.getLength() > 0){
            game.olderUsedPrices = new ArrayList();
            Element olderUsedPrices = (Element)nl.item(0);
            nl = olderUsedPrices.getElementsByTagName("price");
            for(int i = 0; i<nl.getLength(); i++){
                Element elementPrice = (Element)nl.item(i);
                game.olderUsedPrices.add(Double.valueOf(elementPrice.getTextContent()));
            }
        }
        
        //PREORDER PRICE
        nl = prices.getElementsByTagName("preorderPrice");
        if(nl.getLength() > 0){
            Element preorderPrice = (Element)nl.item(0);
            game.preorderPrice = Double.valueOf(preorderPrice.getTextContent());
        }
        
        //DIGITAL PRICE
        nl = prices.getElementsByTagName("digitalPrice");
        if(nl.getLength() > 0){
            Element digitalPrice = (Element)nl.item(0);
            game.digitalPrice = Double.valueOf(digitalPrice.getTextContent());
        }
        
        //PEGI
        nl = gameElement.getElementsByTagName("pegi");
        if(nl.getLength() > 0){
            Element pegi = (Element)nl.item(0);
            nl = pegi.getElementsByTagName("type");
            game.pegi = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element type = (Element)nl.item(i);
                game.pegi.add(type.getTextContent());
            }
        }
        
        //GENRES
        nl = gameElement.getElementsByTagName("genres");
        if(nl.getLength() > 0){
            Element genres = (Element)nl.item(0);
            nl = genres.getElementsByTagName("genre");
            game.genres = new ArrayList();
            for(int i = 0; i<nl.getLength(); i++){
                Element genre = (Element)nl.item(i);
                game.genres.add(genre.getTextContent());
            }
        }
        
        //OFFICIAL SITE
        nl = gameElement.getElementsByTagName("officialSite");
        if(nl.getLength() > 0){
            Element officialSite = (Element)nl.item(0);
            game.officialSite = officialSite.getChildNodes().item(0).getTextContent();
        }
        
        //PLAYERS
        nl = gameElement.getElementsByTagName("players");
        if(nl.getLength() > 0){
            Element players = (Element)nl.item(0);
            game.players = players.getChildNodes().item(0).getTextContent();
        }
        
        //RELEASE DATE
        nl = gameElement.getElementsByTagName("releaseDate");
        if(nl.getLength() > 0){
            Element releaseDate = (Element)nl.item(0);
            game.releaseDate = releaseDate.getTextContent();
        }
        
        //PROMOS
        nl = gameElement.getElementsByTagName("promos");
        if(nl.getLength() > 0){
            Element promos = (Element)nl.item(0);
            nl = promos.getElementsByTagName("promo");
            game.promo = new ArrayList();
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
                game.promo.add(p);
            }
        }
        
        //DESCRIPTION
        nl = gameElement.getElementsByTagName("description");
        if(nl.getLength() > 0){
            Element description = (Element)nl.item(0);
            game.description = description.getChildNodes().item(0).getTextContent();
        }
        
        //VALID FOR PROMO
        nl = gameElement.getElementsByTagName("validForPromo");
        if(nl.getLength() > 0){
            Element validForPromo = (Element)nl.item(0);
            game.validForPromotions = Boolean.valueOf(validForPromo.getTextContent());
        }
        
        return game;
    }
    
    public static void validateGame(File f) throws Exception  {        
        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(DirectoryManager.SCHEMA_GAME));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(f));
    }
    
    // IMPORT/EXPORT METHODS FOR GAMES CLASS ----------------------------------
    
    public static void exportGames(Games games) throws Exception {
        
        File f = new File(WISHLIST);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        
        for ( Game game : games ){
            exportGame(game);
            bw.write( game.getId() + ";" );
        }
        
        bw.close();
    }
    
    public static Games importGames() throws Exception {
        
        Games games = new Games();
        
        File f = new File(WISHLIST);
        BufferedReader br = new BufferedReader(new FileReader(f));
        
        String row = br.readLine();
        String[] IDs = row.split(";");
        
        for( String id : IDs ){
            games.add(importGame(id));
        }
        
        return games;
    }
    
    // DELETE METHODS
    
    // discutere sui parametri passati in ingresso
    public static void deleteTempGames(Games games){
        
        // make a tree with the ids to speed the operations
        TreeSet<String> ids = new TreeSet<>();
        for ( Game game : games ){
            ids.add(game.getId());
        }
        
        File[] gameFolders = new File(getGamesDirectory()).listFiles();
        
        for ( File file : gameFolders ){
            // if the file is a folder and doesn't have the name of an id contained in the wishlist
            if ( file.isDirectory() && ids.contains(file.getName()) == false ){
                deleteFile(file);
                Log.info("DirectoryManager", "\""+file.getName()+"\" folder deleted");
            }
        }
        
    }
    
    public static void deleteAllGames() {
        File[] gameFolders = new File(getGamesDirectory()).listFiles();
        for ( File file : gameFolders ){
            deleteFile(file);
        }
    }
    
    private static void deleteFile(File f) {
        
        if ( f.isDirectory() ){
            for ( File file : f.listFiles() ) {
                deleteFile(file);
            }
        }
        
        f.delete();        
    }
    
}

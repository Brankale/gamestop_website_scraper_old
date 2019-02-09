package gamestopapp;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import javax.xml.transform.OutputKeys;

public class Games extends ArrayList<Game> {
    
    private static final String SCHEMA_PATH = "schema.xsd";
    
    @Override
    public boolean add ( Game game ) {
        
        for ( Game g : this ){
            if ( g.equals(game) ){
                // it's a warning because equals() requires a revision
                Log.warning("Games", "the game already exist", game.getTitle() );
                return false;
            }
        }
        
        super.add(game);
        Log.info("Games", "game added", game.getTitle() );
        
        return true;
    }
    
    @Override
    public String toString ()
    {
        String str = new String();
        for( int game=0; game<this.size(); ++game ){
            str += this.get(game).toString()+"\n\n";
        }
        return str;
    }
    
    public void exportToBinary() throws IOException
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userData/wishlist.dat"));
        
        for( int game=0; game<this.size(); ++game ){
            oos.writeObject( this.get(game) );
        }
        
        Log.info("Games", "exported to binary");
        oos.close();
    }
    
    public static Games importFromBinary() throws FileNotFoundException, IOException, ClassNotFoundException
    {        
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userData/wishlist.dat"));
        
        Games wishlist = new Games();        
        boolean eof = false;
        
        while(!eof){
            try{
                Game g = (Game)ois.readObject();
                wishlist.add(g);
            }catch(EOFException e){
                eof = true;
            }
        }
        
        Log.info("Games", "imported from binary");
        return wishlist;
    }
    
    public void sortbyName () {
        Collections.sort( this );
    }
    
    public void sortByPlatform () {
        Collections.sort( this, new GamePlatformComparator() );
    }
    
    public void sortByNewPrice () {
        Collections.sort( this, new GameNewPriceComparator() );
    }
    
    public void sortByUsedPrice () {
        Collections.sort( this, new GameUsedPriceComparator() );
    }
    
    public void sortByReleaseDate () {
        Collections.sort( this, new GameReleaseDateComparator() );
    }
    
    public void exportXML(String fileName) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException, org.xml.sax.SAXException{
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = parser.newDocument();
        
        //Element Games
        org.w3c.dom.Element elementGames = doc.createElement("games");
        
        
        if(this.size() > 0){
            for(Game g : this){
                elementGames.appendChild(g.exportXML(doc));
            }
        }
        
        doc.appendChild(elementGames);
        
        validate(doc);
        
        // Save
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        File f = new File(fileName);
        transformer.transform( new DOMSource(doc), new StreamResult(f));       
    }
    
    public static Games importXML(String fileName) throws ParserConfigurationException,IOException, org.xml.sax.SAXException{
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = parser.parse(new File(fileName));       
        validate(doc);
        
        Games games = new Games();
        
        org.w3c.dom.Element elementGames = doc.getDocumentElement();
        NodeList listGames = elementGames.getElementsByTagName("game");
        if(listGames.getLength() > 0){
            for(int i = 0; i<listGames.getLength(); i++){
                org.w3c.dom.Element elementGame = (org.w3c.dom.Element)listGames.item(i);
                games.add(Game.importXML(elementGame));
            }
        }
        
        return games;
    }
    
    private static void validate(org.w3c.dom.Document doc) throws IOException, org.xml.sax.SAXException{
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        SchemaFactory factory = SchemaFactory.newInstance(language);
        Schema schema = factory.newSchema(new File(SCHEMA_PATH));
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new DOMSource(doc));
    }
    
}

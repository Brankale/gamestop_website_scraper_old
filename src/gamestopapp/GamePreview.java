package gamestopapp;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

public class GamePreview {
    
    private String title;
    private String url;
    private String platform;    // not definitive
    private Image image;        // not definitive (can be just a String with the URL / may break compatibility with Android)

    public GamePreview(String title, String url, String platform, String image) throws MalformedURLException, IOException {
        this.title = title;
        this.url = url;
        this.platform = platform;
        this.image = ImageIO.read( new URL(image) );
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
        return image;
    }

    @Override
    public String toString() {
        return  "Title: " + title + "\n" +
                "URL: " + url + "\n" +
                "Platform: " + platform + "\n";
    }
    
    
    
}

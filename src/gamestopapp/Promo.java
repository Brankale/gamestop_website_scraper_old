package gamestopapp;

import java.util.Objects;

public class Promo {
    
    private String header;
    private String validity;
    private String message;
    private String messageURL;

    public Promo(String header, String validity, String message, String messageURL) {
        this.header = header;
        this.validity = validity;
        this.message = message;
        this.messageURL = messageURL;
    }

    public String getHeader() {
        return header;
    }

    public String getValidity() {
        return validity;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageURL() {
        return messageURL;
    }

    @Override
    public String toString() {
        if ( message == null )
            return "Promo{" + "\n  header=" + header + ",\n  validity=" + validity + "  \n }";
        
        return "Promo{" + "\n  header=" + header + ",\n  validity=" + validity + ",\n  message=" + message + ",\n  messageURL=" + messageURL + "  \n }";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.header);
        hash = 89 * hash + Objects.hashCode(this.validity);
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
        final Promo other = (Promo) obj;
        if (!Objects.equals(this.header, other.header)) {
            return false;
        }
        return Objects.equals(this.validity, other.validity);
    }
    
}

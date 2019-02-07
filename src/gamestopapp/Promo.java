package gamestopapp;

import java.util.Objects;

public class Promo {
    
    private String header;
    private String validity;

    public Promo(String header, String validity) {
        this.header = header;
        this.validity = validity;
    }

    public String getHeader() {
        return header;
    }

    public String getValidity() {
        return validity;
    }

    @Override
    public String toString() {
        return "Promo{" + "header=" + header + ", validity=" + validity + '}';
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

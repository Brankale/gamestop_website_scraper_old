package gamestopapp;

public class IsNotAGameException extends RuntimeException {

    @Override
    public String toString() {
        return "This is not a Game!";
    }
    
}

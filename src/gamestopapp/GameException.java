package gamestopapp;

public class GameException extends RuntimeException {

    @Override
    public String toString() {
        return "failed to create a Game";
    }
    
}

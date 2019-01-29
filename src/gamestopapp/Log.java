package gamestopapp;

// see https://en.wikipedia.org/wiki/ANSI_escape_code

public class Log {
    
    // Reset
    private static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    private static final String BLACK = "\033[0;30m";   // BLACK
    private static final String RED = "\033[0;31m";     // RED
    private static final String GREEN = "\033[0;32m";   // GREEN
    private static final String YELLOW = "\033[0;33m";  // YELLOW
    private static final String BLUE = "\033[0;34m";    // BLUE
    private static final String PURPLE = "\033[0;35m";  // PURPLE
    private static final String CYAN = "\033[0;36m";    // CYAN
    private static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    private static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    private static final String RED_BOLD = "\033[1;31m";    // RED
    private static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    private static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    private static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    private static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    private static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    private static final String WHITE_BOLD = "\033[1;37m";  // WHITE


    //info
    public static void info(String className, String message) {
        System.out.println(GREEN + className + " : " + message + RESET);
    }
    
    public static void info(String className, String message, String resource) {
        System.out.println(GREEN + className + " : " + message + "   src: " + resource + RESET);
    }

    //error
    public static void error(String className, String message) {
        System.out.println(RED + className + " : " + message + RESET);
    }
    
    public static void error(String className, String message, String resource) {
        System.out.println(RED + className + " : " + message + "   src: " + resource + RESET);
    }

    //debug
    public static void debug(String className, String message) {
        System.out.println( CYAN + className + " : " + message + RESET);
    }
    
    public static void debug(String className, String message, String resource) {
        System.out.println( CYAN + className + " :" + message + "   src: " + resource + RESET);
    }

    //warning
    public static void warning(String className, String message) {
        System.out.println( PURPLE_BOLD + className + " : " + message + RESET);
    }
    
    public static void warning(String className, String message, String resource) {
        System.out.println( PURPLE_BOLD + className + " : " + message + "   src: " + resource + RESET);
    }
    
}

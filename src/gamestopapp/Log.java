package gamestopapp;

// see https://en.wikipedia.org/wiki/ANSI_escape_code

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


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
        System.out.println(GREEN + format(className,message,null) + RESET);
    }
    
    public static void info(String className, String message, String resource) {
        System.out.println(GREEN + format(className,message,resource) + RESET);
    }

    //error
    public static void error(String className, String message) {
        System.out.println(RED + format(className,message,null) + RESET);
    }
    
    public static void error(String className, String message, String resource) { 
        System.out.println(RED + format(className,message,resource)+ RESET);
    }

    //debug
    public static void debug(String className, String message) {
        System.out.println(CYAN + format(className,message,null) + RESET);
    }
    
    public static void debug(String className, String message, String resource) {
        System.out.println(CYAN + format(className,message,resource) + RESET);
    }

    //warning
    public static void warning(String className, String message) {
        System.out.println(PURPLE_BOLD + format(className,message,null) + RESET);
    }
    
    public static void warning(String className, String message, String resource) {
        System.out.println(PURPLE_BOLD + format(className,message,resource) + RESET);
    }   
    
    public static void crash ( Exception e, String src ) {
        
        try {            
            // create log directory if doesn't exist
            File directory = new File("log");
            if ( !directory.exists() )
                directory.mkdir();

            // save the information of the crash in the log                
            String fileName = "log/"+System.currentTimeMillis()+".txt";
            FileWriter fw = new FileWriter (fileName, true);
            PrintWriter pw = new PrintWriter (fw);

            pw.write(src+"\n\n");
            e.printStackTrace (pw);
            fw.close();

            Log.error("Log", "Crash Log file created");
                
        } catch (IOException ex) {
            Log.error("Log", "Failed to create crash log");
        }
        
    }
    
    private static String format( String className, String message, String resource ){
        
        final int CLASS_NAME = 15;
        final int MESSAGE = 50;
        
        if ( className.length() > CLASS_NAME ){
            className = className.substring(0, CLASS_NAME-3) + ".: ";
        } else {
            className += ":";
            while ( className.length() != CLASS_NAME ) {
                className += " ";
            }
        }
        
        if ( message.length() > MESSAGE ){
            message = message.substring(0, MESSAGE-3) + "...";
        } else {
            while ( message.length() != MESSAGE ) {
                message += " ";
            }
        }
        
        if ( resource == null )
            resource = "";
        
        return className + message + "\t\t" + resource;
    }
}

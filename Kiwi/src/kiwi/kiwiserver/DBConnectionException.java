package kiwi.kiwiserver;

/**
 * An exception indication that an action couldn't occur due to a failed
 * database connection on the server.
 * @author Tala Ross(rsstal002)
 */
public class DBConnectionException extends Exception{
    
    /**
     * Default constructor.
     */
    public DBConnectionException() { 
        super(); 
    }
    
    /**
     * Construct exception due to database error with given message as
     * exception message.
     * @param message The exception message.
     */
    public DBConnectionException(String message) { 
        super(message); 
    }
    
    
}

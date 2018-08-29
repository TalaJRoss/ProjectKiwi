package kiwi.kiwiserver;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class DBConnectionException extends Exception{
    
    public DBConnectionException() { 
        super(); 
    }
    
    public DBConnectionException(String message) { 
        super(message); 
    }
    
    public DBConnectionException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public DBConnectionException(Throwable cause) {
        super(cause); 
    }
    
}

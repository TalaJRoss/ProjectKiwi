package kiwiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Listens for lecturer connections and starts LecturerHandler thread.
 * @author Tala Ross(rsstal002)
 */
public class Student extends Thread{
    
    //Instance Variables:
    /**
     * Port number which server is listening for lecturer connections on.
     */
    ServerSocket serverSocket;
    
    
    //Constructors:
    /**
     * Creates listener socket for lecturer connections on server's lecturer
     * port number. 
     * @param lecturerSocket Socket which server is listening for lecturer connections on.
     */
    public void LecturerListener() {
        try {      
            this.serverSocket= new ServerSocket(Server.LECTURER_PORT_NUM);
        } 
        catch (IOException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //Run Method:
    /**
     * Listens for lecturer connections and starts lecturer handler thread when
     * connection is made.
     */
    @Override
    public void run(){
        while(true) {
            try {
                System.out.println("Waiting for lecturer connection.");
                Socket lecturerSocket= serverSocket.accept();
                System.out.println("Accepted connection: lecturer " + lecturerSocket + ".");
                LecturerHandler lecturerHandler= new LecturerHandler();
                lecturerHandler.start();
            } 
            catch (IOException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}

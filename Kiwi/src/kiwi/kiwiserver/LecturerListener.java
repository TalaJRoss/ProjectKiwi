package kiwi.kiwiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Listens for lecturer connections and starts LecturerHandler thread.
 * @author Tala Ross(rsstal002)
 */
public class LecturerListener extends Thread{
    
    //Instance Variables:
    /**
     * Port number which server is listening for lecturer connections on.
     */
    private ServerSocket serverSocket;
    
    
    //Constructors:
    /**
     * Creates listener socket for lecturer connections on server's lecturer
     * port number. 
     * @param server The server which the listener is servicing
     * @throws java.io.IOException
     */
    public LecturerListener() {
        try {
            serverSocket= new ServerSocket(ServerStartup.LECTURER_PORT_NUM);
        } catch (IOException ex) {
            Logger.getLogger(LecturerListener.class.getName()).log(Level.SEVERE, null, ex);
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
                System.out.println("DEBUG2: " + serverSocket);
                Socket lecturerSocket = serverSocket.accept();
                System.out.println("Accepted connection: lecturer " + lecturerSocket + ".");
                LecturerHandler lecturerHandler = new LecturerHandler(lecturerSocket);
                lecturerHandler.start();
            } 
            catch (IOException ex) {
                Logger.getLogger(LecturerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}

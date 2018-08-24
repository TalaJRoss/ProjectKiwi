package kiwi.kiwiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


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
        } 
        catch (IOException e) {
            System.out.println("Problem setting up lecturer socket.");
            System.out.println(e);
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
                Socket lecturerSocket = serverSocket.accept();
                System.out.println("Accepted connection: lecturer " + lecturerSocket + ".");
                LecturerHandler lecturerHandler = new LecturerHandler(lecturerSocket);
                lecturerHandler.start();
            } 
            catch (IOException e) {
                System.out.println("Problem processing lecturer connection.");
                System.out.println(e);
            }
        }
    }
    
}

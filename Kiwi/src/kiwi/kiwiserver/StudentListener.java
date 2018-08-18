package kiwi.kiwiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Listens for student connections and starts StudentHandler thread.
 * @author Tala Ross(rsstal002)
 */
public class StudentListener extends Thread{
    
    //Instance Variables:
    /**
     * Port number which server is listening for student connections on.
     */
    private ServerSocket serverSocket;
    
    /**
     * List of student handler threads running.
     */
    private CopyOnWriteArrayList<StudentHandler> handlerList;
    
    
    //Constructors:
    /**
     * Creates listener socket for student connections on server's student
     * port number. 
     * @param server The server which the listener is servicing
     * @throws java.io.IOException
     */
    public StudentListener() throws IOException {
        this.serverSocket= new ServerSocket(ServerStartup.STUDENT_PORT_NUM);
        this.handlerList= new CopyOnWriteArrayList<>();
    }
    
    
    //Run Method:
    /**
     * Listens for student connections and starts student handler thread when
     * connection is made.
     */
    @Override
    public void run(){
        while(true) {
            try {
                System.out.println("Waiting for student connection.");
                Socket studentSocket= serverSocket.accept();
                System.out.println("Accepted connection: student " + studentSocket + ".");
                StudentHandler studentHandler= new StudentHandler(studentSocket);
                handlerList.add(studentHandler);    //may not be necessary
                studentHandler.start();
            } 
            catch (IOException ex) {
                Logger.getLogger(StudentListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}

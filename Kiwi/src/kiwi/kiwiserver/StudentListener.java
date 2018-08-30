package kiwi.kiwiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


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
    
    
    //Constructors:
    /**
     * Creates listener socket for student connections on server's student
     * port number. 
     */
    public StudentListener() {
        try {
            this.serverSocket= new ServerSocket(ServerStartup.STUDENT_PORT_NUM);
        } catch (IOException e) {
            System.out.println("Problem setting up student socket.");
            System.out.println(e);
        }
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
                studentHandler.start();
            } 
            catch (IOException e) {
                System.out.println("Problem processing student connection.");
                System.out.println(e);
            }
        }
    }
    
}

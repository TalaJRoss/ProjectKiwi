/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.kiwiserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tala Ross(rsstal002)
 */
class StudentHandler extends Thread {
    
    /**
     * Socket on client who's communications this thread is managing.
     */
    private Socket studentSocket;
    
    /**
     * Input stream for server socket (reader) which reads in client messages.
     */
    private ObjectInputStream reader;   
    
    /**
     * Output stream for server socket (writer) which writes messages to client.
     */
    private ObjectOutputStream writer;  
    
    /**
     * User name of client that this thread manages.
     */
    private String studentNo;
    
    
    //Constructor:
    
    /**
     * Creates new client handler thread to relay message and process status
     * updates and hashtag join/leaves.
     * @param server server that client connects to and thread services
     * @param clientSocket socket on client side linking server to client
     */
    public StudentHandler(Socket studentSocket) {
        this.studentSocket = studentSocket;
        studentNo= null;
        try {
            OutputStream os= studentSocket.getOutputStream();
            writer= new ObjectOutputStream(os);   //MESSAGE
            InputStream is= studentSocket.getInputStream();
            reader= new ObjectInputStream(is); //MESSAGE
        } 
        catch (IOException ex) {
            Logger.getLogger(StudentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

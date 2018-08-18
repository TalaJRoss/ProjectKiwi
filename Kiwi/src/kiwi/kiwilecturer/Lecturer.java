package kiwi.kiwilecturer;

import com.opencsv.CSVReader;
import java.sql.Statement;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.LecturerMessage;

//TODO: get assignment deadline and no. submissions from lecturer
//TODO: update create students table to just require studentNo fields in csv and not require formated headings row
//TODO: update create questions table to not require formated headings row
/**
 * Contains static lecturer functionality methods for uploading csv files.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Lecturer {
    
    
    //Server:
    
    /**
     * Name/IP address of the server socket.(Either "localhost" or
     * "IP_address>").
     */
    private static final String SERVER_NAME = "localhost";
    
    /**
     * Port number on which server is listening.
     */
    private static final int PORT_NO = 2048;
    
    /**
     * This lecturer's socket which is used to communicate with the server.
     */
    private final Socket lecturerSocket;
    
    /**
     * Stream used to read in messages from the server.
     */
    private final ObjectOutputStream writer;
    
    /**
     * Stream used to write messages to the server.
     */
    private final ObjectInputStream reader;
    
    private boolean connected;

    public Lecturer() throws IOException, ClassNotFoundException {
        //make socket:
        lecturerSocket= new Socket(SERVER_NAME, PORT_NO);
        System.out.println("Client port is: " + lecturerSocket.getLocalPort());
        //Set up streams:
        InputStream is= lecturerSocket.getInputStream();
        reader= new ObjectInputStream(is); //MESSAGE
        OutputStream os= lecturerSocket.getOutputStream();
        writer= new ObjectOutputStream(os);   //MESSAGE
        
        LecturerMessage response = (LecturerMessage) reader.readObject();
        connected = response.getResponse().equals(LecturerMessage.RES_SUCCESS);
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    
     /**
     * Converts file path in string form to byte array representing file.
     * @param filePath string path for file
     * @return byte array representing file data
     * @throws IOException 
     */
    private byte[] getFileBytes(String filePath) throws IOException
        {
        Path path= Paths.get(filePath);
        byte[] data= Files.readAllBytes(path); //Convert file to byte array
        return data;
        }
    
    //Main functionality methods(public):
    
    /**
     * Uploads student information contained in the given csv file to the
     * students table in the database on the server.
     * Expected csv file format:
     * studentNo,highestGrade,noSubmissionsCompleted
     * @param csv The csv file containing student information.
     */
    public void uploadStudents(String path) {
        try {
            ArrayList<byte []> csvFiles = new ArrayList<>();
            csvFiles.add(getFileBytes(path));
            //String [] filenames = {name};
            writer.writeObject(new LecturerMessage(csvFiles, null, "", LecturerMessage.CMD_UPLOAD_STUDENTS, ""));
        } catch (IOException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Uploads question-answer pairs contained in the given csv file to the
     * questions table in the database on the server.
     * Expected csv file format:
     * questionNo,question,answer,difficulty
     * @param csv The csv file containing question-answer pairs.
     */
    public void uploadQuestions(String path) {
        try {
            ArrayList<byte []> csvFiles = new ArrayList<>();
            csvFiles.add(getFileBytes(path));
            //String [] filenames = {name};
            writer.writeObject(new LecturerMessage(csvFiles, null, "", LecturerMessage.CMD_UPLOAD_QUESTIONS, ""));
        } catch (IOException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates tables on database to represent the query data contained in the
     * given csv files and uploads the contained information to the database on
     * the server.
     * Expected csv file format:
     * File name: nameOfTable.csv.
     * First line: "columnLabel columnType(length) isPrimaryKey(1/0)",...
     * This is comma-separated for each column.
     * Actual row entries: fields are comma-separated and enclosed in quotes
     * and lines are terminated by '\r\n'.
     * @param csvs Array of csv files containing query data.
     */
    public void uploadQueryData(String [] paths, String [] names) {
        try {
            ArrayList<byte []> csvFiles = new ArrayList<>();
            for (String path: paths) {
                csvFiles.add(getFileBytes(path));
            }
            writer.writeObject(new LecturerMessage(csvFiles, names, "", LecturerMessage.CMD_UPLOAD_QUERY, ""));
        } catch (IOException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to ascending student number.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * ascending student number.
     */
    public String viewGradeAscStudent() {
        try {
            writer.writeObject(new LecturerMessage(null, null, "", LecturerMessage.CMD_GRADE_ALPH, ""));
            LecturerMessage response = (LecturerMessage) reader.readObject();
            return response.getToReturn();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to descending student number.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * descending student number.
     */
    public String viewGradeDescGrade() {
        try {
            writer.writeObject(new LecturerMessage(null, null, "", LecturerMessage.CMD_GRADE_DESC, ""));
            LecturerMessage response = (LecturerMessage) reader.readObject();
            return response.getToReturn();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";
    }

    void connectToDB() throws IOException, ClassNotFoundException {
        writer.writeObject(new LecturerMessage(null, null, "", LecturerMessage.CMD_CONNECT, ""));
        LecturerMessage response = (LecturerMessage) reader.readObject();
        connected = response.getResponse().equals(LecturerMessage.RES_SUCCESS);
    }
    
}

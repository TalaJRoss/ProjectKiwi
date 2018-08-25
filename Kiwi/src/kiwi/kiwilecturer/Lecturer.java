package kiwi.kiwilecturer;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import kiwi.kiwiserver.ServerStartup;
import kiwi.message.*;

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
    
    /**
     * Success message
     */
    public static final String SUCCESS = "Success";
    
    /**
     * Fail message
     */
    public static final String FAIL = "Error";

    public Lecturer() throws IOException, ClassNotFoundException {
        //make socket:
        lecturerSocket= new Socket(ServerStartup.SERVER_NAME, ServerStartup.LECTURER_PORT_NUM);
        System.out.println("Client port is: " + lecturerSocket.getLocalPort());
        //Set up streams:
        InputStream is= lecturerSocket.getInputStream();
        reader= new ObjectInputStream(is); //MESSAGE
        OutputStream os= lecturerSocket.getOutputStream();
        writer= new ObjectOutputStream(os);   //MESSAGE
        
        LecturerMessage response = (LecturerMessage) reader.readObject();
        connected = (response.getMessage() == LecturerMessage.RESP_SUCCESS);
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Informs server of close and then closes socket connections.
     * @throws IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void closeConnection() throws IOException, ClassNotFoundException {
        writer.writeObject(new LecturerMessage(StudentMessage.CMD_CLOSE, null));
        reader.close();
        writer.close();
        lecturerSocket.close();
        connected = false;
    }
    
     /**
     * Converts file path in string form to byte array representing file.
     * @param filePath string path for file
     * @return byte array representing file data
     * @throws IOException 
     */
    private byte[] getFileBytes(String filePath) throws IOException {
        Path path= Paths.get(filePath);
        byte[] data= Files.readAllBytes(path); //Convert file to byte array
        return data;
    }
    
    //Main functionality methods(public):
    
    public String uploadAssignmentInfo(String noSubmissions, String noQuestions, String date, String time, String schemaPath){
        
        byte [] schemaImg = null;
        
        try {
            if (schemaPath != null) {
                schemaImg = getFileBytes(schemaPath);
            }
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_ASSIGNMENT_INFO, new AssignmentInfo(noSubmissions, noQuestions, date, time, schemaImg)));
            
            LecturerMessage resp = (LecturerMessage) reader.readObject();
            if (resp.getMessage()==LecturerMessage.RESP_SUCCESS) {
                return SUCCESS;
            }
            else {
                return FAIL;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: problem uploading info.");
            System.out.println(ex);
            return FAIL;
        }
    }
    
    /**
     * Uploads student information contained in the given csv file to the
     * students table in the database on the server.
     * Expected csv file format:
     * studentNo,highestGrade,noSubmissionsCompleted
     * @param path The path to the csv file containing student information.
     * @return a success/fail message.
     */
    public String uploadStudents(String path) {
        try {
            ArrayList<byte []> csvFiles = new ArrayList<>();
            csvFiles.add(getFileBytes(path));
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_STUDENTS, new CSVFiles(csvFiles, null)));
        
            LecturerMessage resp = (LecturerMessage) reader.readObject();
            if (resp.getMessage()==LecturerMessage.RESP_SUCCESS) {
                return SUCCESS;
            }
            else {
                return FAIL;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: problem uploading info.");
            System.out.println(ex);
            return FAIL;
        }
    }
    
    /**
     * Uploads question-answer pairs contained in the given csv file to the
     * questions table in the database on the server.
     * Expected csv file format:
     * questionNo,question,answer,difficulty
     * @param path The path to the csv file containing question-answer pairs.
     * @return a success/fail message.
     */
    public String uploadQuestions(String path) {
        try {
            ArrayList<byte []> csvFiles = new ArrayList<>();
            csvFiles.add(getFileBytes(path));
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUESTIONS, new CSVFiles(csvFiles, null)));
            
            LecturerMessage resp = (LecturerMessage) reader.readObject();
            if (resp.getMessage()==LecturerMessage.RESP_SUCCESS) {
                return SUCCESS;
            }
            else {
                return FAIL;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: problem uploading info.");
            System.out.println(ex);
            return FAIL;
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
     * @param paths Array of paths of csv files containing query data.
     * @param names Names of the csv files containing query data.
     * @return a success/fail message.
     */
    public String uploadQueryData(String [] paths, String [] names) {
        try {
            ArrayList<byte []> csvFiles = new ArrayList<>();
            for (String path: paths) {
                csvFiles.add(getFileBytes(path));
            }
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUERY, new CSVFiles(csvFiles, names)));
            
            LecturerMessage resp = (LecturerMessage) reader.readObject();
            if (resp.getMessage()==LecturerMessage.RESP_SUCCESS) {
                return SUCCESS;
            }
            else {
                return FAIL;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: problem uploading info.");
            System.out.println(ex);
            return FAIL;
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
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_GRADE_ALPH, null));
            LecturerMessage response = (LecturerMessage) reader.readObject();
            if (response.getMessage()==LecturerMessage.RESP_SUCCESS) {
                return ((Grades) response.getBody()).getGrades();
            }
            else {
                return FAIL;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: problem getting grades.");
            System.out.println(ex);
            return FAIL;
        }
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
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_GRADE_DESC, null));
            LecturerMessage response = (LecturerMessage) reader.readObject();
            if (response.getMessage()==LecturerMessage.RESP_SUCCESS) {
                return ((Grades) response.getBody()).getGrades();
            }
            else {
                return FAIL;
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: problem getting grades.");
            System.out.println(ex);
            return FAIL;
        }
    }

    public void connectToDB() throws IOException, ClassNotFoundException {
        writer.writeObject(new LecturerMessage(LecturerMessage.CMD_CONNECT, null));
        LecturerMessage response = (LecturerMessage) reader.readObject();
        connected = (response.getMessage() == LecturerMessage.RESP_SUCCESS);            
    }
    
}

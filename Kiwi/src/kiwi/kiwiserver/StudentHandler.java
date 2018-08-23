/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.kiwiserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import kiwi.message.QuestionInfo;
import kiwi.message.StudentMessage;
import kiwi.message.StudentStatistics;

/**
 *
 * @author Tala Ross(rsstal002)
 */
class StudentHandler extends Thread {
    
    
    private static final String DB_PATH = "C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Data\\kiwidb\\";
    
    
    /**
     * User name for database connection.
     */
    //TODO: change to "lecturer"
    public static final String USER_NAME = "root";
    
    /**
     * Password for database connection.
     */
    //TODO: change to secure pwd
    public static final String PASSWORD = "mysql";
    
    
    //Socket stuff:
    
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
    
    
    //Student stuff:
    
    /**
     * User name of client that this thread manages.
     */
    private String studentNo;
    
    private Assignment assignment;
    
    
    //DB stuff:
    
    Connection conn;
    
    private byte [] schemaImg;
    
    
    //Constructor:
    
    /**
     * Creates new client handler thread to relay message and process status
     * updates and hashtag join/leaves.
     * @param server server that client connects to and thread services
     * @param clientSocket socket on client side linking server to client
     */
    public StudentHandler(Socket studentSocket) {
        //DEBUG:
        File file = new File("schema.jpg");
        byte[] bytesArray = new byte[(int) file.length()]; 

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
            schemaImg = bytesArray;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StudentHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StudentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.studentSocket = studentSocket;
        studentNo = null;
        try {
            OutputStream os = studentSocket.getOutputStream();
            writer = new ObjectOutputStream(os);   //MESSAGE
            InputStream is = studentSocket.getInputStream();
            reader = new ObjectInputStream(is); //MESSAGE
        } 
        catch (IOException ex) {
            Logger.getLogger(StudentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
    public void connectToDB() throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", USER_NAME, PASSWORD);
            System.out.println("conn: " + conn);
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CONNECT, null));
        } catch (SQLException | ClassNotFoundException ex) {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CONNECT, null, StudentMessage.FAIL_CONNECT));
        }
    }
    
     @Override
    public void run() {
        try {
            connectToDB();
        } catch (IOException ex) {
            Logger.getLogger(StudentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            StudentMessage m = (StudentMessage) reader.readObject();   //MESSAGE
            int command;
            
            while (m!= null)
            {
                command = m.getCmd();
                switch (command) 
                {
                    case StudentMessage.CMD_CONNECT:  //retry connect
                        connectToDB();
                        break;
                    case StudentMessage.CMD_LOGIN:   //login
                        login((String)m.getBody());
                        break;
                    case StudentMessage.CMD_START:   //start assignment
                        generateAssignment();
                        break;
                    case StudentMessage.CMD_STATS: //view statistic
                        viewStats();
                        break;
                    case StudentMessage.CMD_CHECK: //check output
                        checkOutput((String)m.getBody());
                        break;
                    case StudentMessage.CMD_SUBMIT: //upload file
                        markQuestion((String)m.getBody());
                        break;
                    case StudentMessage.CMD_QUIT: //upload file
                        updateGrade();
                        break;
                    default:    //nothing else
                        break;
                }
                writer.flush();
                m= (StudentMessage) reader.readObject();
            }
            studentSocket.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(StudentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Checks whether student number given is stored in students table on the
     * database and initializes instance variables if login is successful.
     * @param studentNumber The student number of the student to login.
     */
    public void login(String studentNumber) throws IOException {
        try {
            //Get student info:
            Statement st = conn.createStatement();
            String statement = "SELECT * FROM students WHERE StudentNo LIKE '" + studentNumber + "'";   //check for student number in student table
            ResultSet rs = st.executeQuery(statement);
            
            //Check student number exists and load values:
            if (rs.next()) {    //student number exists
                studentNo = (String) rs.getObject("studentNo");
                writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, null));
            }
            else { //student number doesn't exist
                writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, null, StudentMessage.FAIL_INPUT)); //null on student end indicates error
            }  
        }catch (SQLException e) { //can't check student number
            System.out.println("Error: Problem checking student number on database for login.");
            System.out.println(e);
            writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, null, StudentMessage.FAIL_CONNECT)); //null on student end indicates error
        }
         
    }
    
    private void generateAssignment() throws IOException {
        try {
            //Get max no. submissions allowed:
            Statement st = conn.createStatement();
            String statement = "SELECT NoSubmissions FROM AssignmentInfo;"; 
            ResultSet rs = st.executeQuery(statement);
            int maxNoSubmissions;
            if (rs.next()) {
                maxNoSubmissions = (int) rs.getObject("NoSubmissions");
            }
            else {  //couldn't get max no. submissions
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.FAIL_CONNECT));
                return;
            }
            
            //Check that student allowed to do assignment:
            statement = "SELECT NoSubmissionsCompleted FROM Students WHERE StudentNo LIKE '" + studentNo + "'"; 
            rs = st.executeQuery(statement);
            int noSubmissionsCompleted;
            if (rs.next()) {
                noSubmissionsCompleted = (int) rs.getObject("NoSubmissionsCompleted");
            }
            else {  //couldn't get max no. submissions
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.FAIL_CONNECT));
                return;
            }
            if (maxNoSubmissions-noSubmissionsCompleted<=0) {   //no more submissions allowed 
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.FAIL_DENY));
                return;
            }
            
            //Submissions still allowed:
            assignment = new Assignment(conn, studentNo);  //create assignment 
        
            //decrease submissions allowed on server:
            //increase noSubmissions by 1:
            statement = "UPDATE Students SET NoSubmissionsCompleted = NoSubmissionsCompleted + 1 WHERE StudentNo LIKE '" + studentNo + "';";  
            int updateResp = st.executeUpdate(statement);
            if (updateResp!=1) {    //error updating
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.FAIL_CONNECT));
                //TODO: revert with transacts
            }
        } catch (SQLException e) {
            System.out.println("Error: Problem updating number of submissions and creating assignment.");
            System.out.println(e); 
            writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.FAIL_CONNECT));
        }
        QuestionInfo qi = new QuestionInfo(assignment, schemaImg);  //question info to return to student end
        writer.writeObject(new StudentMessage(StudentMessage.CMD_START, qi));
    }

    private void viewStats() throws IOException {
        try {
            //Get max no. submissions allowed:
            Statement st = conn.createStatement();
            String statement = "SELECT NoSubmissions FROM AssignmentInfo;"; 
            ResultSet rs = st.executeQuery(statement);
            int maxNoSubmissions;
            if (rs.next()) {
                maxNoSubmissions = (int) rs.getObject("NoSubmissions");
            }
            else {  //couldn't get max no. submissions
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.FAIL_CONNECT));
                return;
            }
            
            //get student info:
            st = conn.createStatement();
            statement = "SELECT * FROM students WHERE StudentNo LIKE '" + studentNo + "'";   //check for student number in student table
            rs = st.executeQuery(statement);
            
            //load values for this student:
            if (rs.next()) {
                double highestGrade = (double) rs.getObject("highestGrade");
                int noSubmissionsCompleted = (int) rs.getObject("noSubmissionsCompleted");
                int noSubmissionsRemaining = maxNoSubmissions - noSubmissionsCompleted;
                Date deadlineDay = (Date) rs.getObject("deadlineDay");
                Time deadlineTime = (Time) rs.getObject("deadlineTime");
                StudentStatistics ss = new StudentStatistics(highestGrade, noSubmissionsRemaining, deadlineDay, deadlineTime);
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, ss));
            }
            else {
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.FAIL_CONNECT));
            }
        } catch (SQLException ex) {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.FAIL_CONNECT));
        }
    }

    private void checkOutput(String studentStatement) throws IOException {
        String output = assignment.check(studentStatement);
        if (output!=null) {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CHECK, output));
        }
        else {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CHECK, null, StudentMessage.FAIL_CONNECT));
        }
    }
    
    //TODO: check for err handling in underlying logic
    private void markQuestion(String sudentAns) throws IOException {
        //marks question and creates return object with feedback and next question info:
        QuestionInfo qi = new QuestionInfo(assignment, sudentAns);
        writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, qi));
    }

    private void updateGrade() throws IOException {
        try {
            double grade = 0;
            Statement st = conn.createStatement();
            //update grade if higher:
            if (!assignment.hasNext()) {    //finished assignment
                grade = assignment.getGrade();
            }
            String statement = "UPDATE students SET HighestGrade = " + grade + " WHERE StudentNo LIKE '" + studentNo + "' AND HighestGrade < " + grade + ";";  
            int updateResp = st.executeUpdate(statement);
            if (updateResp!=1 && updateResp!=0) {   //error updating
                writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null, StudentMessage.FAIL_CONNECT));
                //TODO: revert with transacts
            }
            else {
                writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null));
            }
        } catch (SQLException e) {
            System.out.println("Error: Problem updating table.");
            System.out.println(e); 
            writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null, StudentMessage.FAIL_CONNECT));
        }
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.kiwiserver;

import java.io.File;
import java.io.FileInputStream;
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
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import kiwi.message.QuestionInfo;
import kiwi.message.StudentMessage;
import kiwi.message.StudentStatistics;

/**
 *
 * @author Tala Ross(rsstal002)
 */
class StudentHandler extends Thread {
    
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
    
    Connection connLimited;
    
    private byte [] schemaImg;
    //Constructor:
    
    /**
     * Creates new client handler thread to relay message and process status
     * updates and hashtag join/leaves.
     * @param server server that client connects to and thread services
     * @param clientSocket socket on client side linking server to client
     */
    public StudentHandler(Socket studentSocket) {
        File file = new File(ServerStartup.DB_PATH + "schema.jpg");
        byte[] bytesArray = new byte[(int) file.length()]; 
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
            schemaImg = bytesArray;
        }
        catch (IOException  e) { 
            System.out.println("Problem processing schema image.");
            System.out.println(e);
        }
        this.studentSocket = studentSocket;
        studentNo = null;
        try {
            OutputStream os = studentSocket.getOutputStream();
            writer = new ObjectOutputStream(os);   //MESSAGE
            InputStream is = studentSocket.getInputStream();
            reader = new ObjectInputStream(is); //MESSAGE
        } 
        catch (IOException e) {
            System.out.println("Problem setting up student socket.");
            System.out.println(e);
        }
    }
    
    //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
    public void connectToDB() throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", ServerStartup.ROOT_NAME, ServerStartup.ROOT_PWD);
            this.connLimited = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", ServerStartup.STUDENT_NAME, ServerStartup.STUDENT_PWD);
            System.out.println(connLimited);
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CONNECT, null));
        } catch (SQLException | ClassNotFoundException ex) {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CONNECT, null, StudentMessage.RESP_FAIL_CONNECT));
        }
    }
    
     @Override
    public void run() {
        try {
            connectToDB();
        } 
        catch (IOException e) {
            System.out.println("Problem creating to database on intial student login.");
            System.out.println(e);
        }
        try {
            StudentMessage m;
            int command;
            
            ONLINE:
            while ((m = (StudentMessage) reader.readObject())!= null) {
                command = m.getCmd();
                switch (command) {
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
                    case StudentMessage.CMD_REPORT: //upload file
                        reportQuestion((String)m.getBody());
                        break;
                    case StudentMessage.CMD_QUIT: //upload file
                        updateGrade();
                        break;
                    case StudentMessage.CMD_CLOSE: //close connection
                        closeConnection();
                        break ONLINE;
                    default:    //nothing else
                        break;
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Problem processing student message.");
            System.out.println(e);
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
                writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, null, StudentMessage.RESP_FAIL_INPUT)); //null on student end indicates error
            }  
        }catch (SQLException e) { //can't check student number
            System.out.println("Error: Problem checking student number on database for login.");
            System.out.println(e);
            writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, null, StudentMessage.RESP_FAIL_CONNECT)); //null on student end indicates error
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
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.RESP_FAIL_CONNECT));
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
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.RESP_FAIL_CONNECT));
                return;
            }
            if (maxNoSubmissions-noSubmissionsCompleted<=0) {   //no more submissions allowed 
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.RESP_FAIL_DENY));
                return;
            }
            
            //Submissions still allowed:
            assignment = new Assignment(conn, connLimited, studentNo, noSubmissionsCompleted);  //create assignment 
        
            //decrease submissions allowed on server:
            //increase noSubmissions by 1:
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            statement = "UPDATE Students SET NoSubmissionsCompleted = NoSubmissionsCompleted + 1 WHERE StudentNo LIKE '" + studentNo + "';";  
            int updateResp = 0;
            try {
                updateResp = st.executeUpdate(statement);
            }
            catch (SQLException e) {
                conn.rollback(sp);
                conn.setAutoCommit(true);
                System.out.println("Error: Problem updating number of submissions.");
                System.out.println(e); 
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.RESP_FAIL_CONNECT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);   //end transaction
            
            if (updateResp!=1) {    //error updating
                writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.RESP_FAIL_CONNECT));
            }
        } catch (SQLException e) {
            System.out.println("Error: Problem creating assignment.");
            System.out.println(e); 
            writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null, StudentMessage.RESP_FAIL_CONNECT));
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
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.RESP_FAIL_CONNECT));
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
                Date deadlineDay = (Date) rs.getObject("deadlineDate");
                Time deadlineTime = (Time) rs.getObject("deadlineTime");
                StudentStatistics ss = new StudentStatistics(highestGrade, noSubmissionsRemaining, deadlineDay, deadlineTime);
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, ss));
            }
            else {
                writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.RESP_FAIL_CONNECT));
            }
        } catch (SQLException ex) {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null, StudentMessage.RESP_FAIL_CONNECT));
        }
    }

    private void checkOutput(String studentStatement) throws IOException {
        String output = assignment.check(studentStatement);
        if (output!=null) {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CHECK, output));
        }
        else {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CHECK, null, StudentMessage.RESP_FAIL_CONNECT));
        }
    }
    
    private void markQuestion(String sudentAns) throws IOException {
        //marks question and creates return object with feedback and next question info:
        QuestionInfo qi = new QuestionInfo(assignment, sudentAns);
        switch (qi.getMark()) {
            case -1:    //lecturer answer is wrong
                writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, qi, StudentMessage.RESP_FAIL_INPUT));
                break;
            case -2:    //problem comparing result sets
                writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, qi, StudentMessage.RESP_FAIL_CONNECT));
                break;
            default:
                writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, qi));
                break;
        }
    }

    private void updateGrade() throws IOException {
        try {
            double grade = 0;
            //update grade if higher:
            if (!assignment.hasNext()) {    //finished assignment
                grade = assignment.getGrade();
            }
            Statement st = conn.createStatement();
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            String statement = "UPDATE students SET HighestGrade = " + grade + " WHERE StudentNo LIKE '" + studentNo + "' AND HighestGrade < " + grade + ";";  
            int updateResp = -1;
            try {
                updateResp = st.executeUpdate(statement);
            }
            catch (SQLException e) {
                conn.rollback(sp);
                conn.setAutoCommit(true);   //end transaction
                System.out.println("Error: Problem updating table.");
                System.out.println(e); 
                writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null, StudentMessage.RESP_FAIL_CONNECT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);   //end transaction
            if (!(updateResp==1 || updateResp==0)) {   //error updating
                writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null, StudentMessage.RESP_FAIL_CONNECT));
            }
            else {
                writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null));
            }
        } catch (SQLException e) {
            System.out.println("Error: Problem updating table.");
            System.out.println(e); 
            writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null, StudentMessage.RESP_FAIL_CONNECT));
        }
    }

    private void closeConnection() throws IOException {
        reader.close();
        writer.close();
        studentSocket.close();
        System.out.println("Student logged off and connection closed: " + studentNo);
    }

    private void reportQuestion(String suggested) throws IOException {
        try {
            Statement st = conn.createStatement();
            ResultSet rs;
            
            //Setup reported table:
            String createStatement = "CREATE TABLE IF NOT EXISTS reported (" +
                    "QuestionNo int(11) NOT NULL, " +
                    "StudentNo varchar(9) NOT NULL, " +
                    "Suggested varchar(500) DEFAULT NULL, " +
                    "PRIMARY KEY (QuestionNo, StudentNo) " +
                    ") ENGINE=InnoDB;";
            st.executeUpdate(createStatement);
            
            //Enter the report:
            String reportStatement = "INSERT INTO reported (QuestionNo, StudentNo, Suggested) " +
                    "VALUES (" + 
                    assignment.getQuestionID() + ", " +    //current question number
                    "'" + studentNo + "', " +          //student's student no.
                    "'" + suggested + "');";       //student's suggested answer
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            try {
                st.executeUpdate(reportStatement);
            }
            catch (SQLException e) {
                conn.rollback(sp);
                conn.setAutoCommit(true);   //end transaction
                System.out.println("Error: Problem adding report.");
                System.out.println(e); 
                writer.writeObject(new StudentMessage(StudentMessage.CMD_REPORT, null, StudentMessage.RESP_FAIL_CONNECT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);
            
            //Mark question as reported:
            reportStatement = "UPDATE questions "
                    + "SET Problem='Reported' "
                    + "WHERE QuestionNo=" + assignment.getQuestionID() + ";";
            conn.setAutoCommit(false);  //start transaction
            sp = conn.setSavepoint();
            try {
                st.executeUpdate(reportStatement);
            }
            catch (SQLException e) {
                conn.rollback(sp);
                conn.setAutoCommit(true);   //end transaction
                System.out.println("Error: Problem adding report.");
                System.out.println(e); 
                writer.writeObject(new StudentMessage(StudentMessage.CMD_REPORT, null, StudentMessage.RESP_FAIL_CONNECT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);
            
            //Successfully reported:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_REPORT, null, StudentMessage.RESP_SUCCESS));
        } 
        catch (SQLException e) {
            System.out.println("Error: Problem adding report.");
            System.out.println(e); 
            writer.writeObject(new StudentMessage(StudentMessage.CMD_REPORT, null, StudentMessage.RESP_FAIL_CONNECT));
        }
    }
    
    
    
}

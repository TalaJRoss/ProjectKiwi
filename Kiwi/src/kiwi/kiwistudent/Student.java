package kiwi.kiwistudent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import kiwi.message.QuestionInfo;
import kiwi.message.StudentMessage;

/**
 * Creates object to handle access to and control of student's information.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
//TODO: update grade/submissions on server at end of each assignment
public class Student {
    
    
    //Constants:
    
    
    /**
     * Name/IP address of the server socket.(Either "localhost" or
     * "IP_address>").
     */
    private static final String SERVER_NAME = "localhost";
    
    /**
     * Port number on which server is listening.
     */
    private static final int PORT_NO = 2048;
    
    
    //Instance Variables:
    
    private String currentFeedback;
    private String currentCheckOutput;
    private int currentOutOf;
    private int currentMark;
    private String nextQuestion;
    
    private double currentFinalGrade;
    private int noQuestions;
    private File schemaImg;
    
    
    /**
     * The student's student number.
     */
    String studentNo;
    
    /**
     * The student's highest recorded assignment grade.
     */
    double highestGrade;
    
    //TODO: get number of submissions from server
    /**
     * The number of submissions a student is allowed.
     */
    int maxNoSubmissions = 3;
    
    /**
     * The number of submissions the student has completed already.
     */
    int noSubmissionsCompleted;
    
    
    /**
     * This student's socket which is used to communicate with the server.
     */
    private final Socket studentSocket;
    
    /**
     * Stream used to read in messages from the server.
     */
    private final ObjectOutputStream writer;
    
    /**
     * Stream used to write messages to the server.
     */
    private final ObjectInputStream reader;
    
    private boolean connected;
    
    
    //Constructor:
    
    /**
     * Default constructor.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Student() throws IOException, ClassNotFoundException {
            //make socket:
            studentSocket = new Socket(SERVER_NAME, PORT_NO);
            System.out.println("Client port is: " + studentSocket.getLocalPort());
            //Set up streams:
            InputStream is= studentSocket.getInputStream();
            reader= new ObjectInputStream(is); //MESSAGE
            OutputStream os= studentSocket.getOutputStream();
            writer= new ObjectOutputStream(os);   //MESSAGE
            //check connection:
            StudentMessage response = (StudentMessage) reader.readObject();
            connected = (int)(response.getBody())==StudentMessage.RESP_SUCCESS;
    }
    
    
    //Main functionality methods(public):
    
    /*public String login(String studentNumber) {
        try {
            //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            //Get student output:
            Statement st = conn.createStatement();
            String statement = "SELECT * FROM students WHERE StudentNo LIKE '" + studentNumber + "'";   //check for student number in student table
            ResultSet rs = st.executeQuery(statement);
            
            //Check student number exists and load values:
            if (rs.next()) {    //student number exists
                studentNo = (String) rs.getObject("studentNo");
                highestGrade = (int) rs.getObject("highestGrade");
                noSubmissionsCompleted = (int) rs.getObject("noSubmissionsCompleted");
                return SUCCESS_LOGIN;
            }
            return FAIL_LOGIN;  //student number doesn't exist
        }
        catch (SQLException e) { //can't check student number
            System.out.println("Error: Problem checking student number on database for login.");
            System.out.println(e);
            return FAIL_CONNECT;
        } 
        catch (ClassNotFoundException e) {  //can't check student number
            System.out.println("Error: Problem connecting to database/loading driver.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
    }/*
    
    
    
    /**
     * Checks students output from the given statement and saves the formatted
     * String output.
     * @param studentStatement
     * @return true if successfully executed otherwise false (ie. couldn't mark)
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public boolean check(String studentStatement) throws IOException, ClassNotFoundException {
        writer.writeObject(new StudentMessage(StudentMessage.CMD_CHECK, studentStatement));
        
        //get feedback and mark:
        StudentMessage m = (StudentMessage) reader.readObject();
        if (m.getResponse()==StudentMessage.SUCCESS) {
            currentCheckOutput = (String) m.getBody();
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Takes in student answer gets feedback and marks and saves them.
     * @param studentAns
     * @return true if successfully executed otherwise false (ie. couldn't mark)
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public boolean submit(String studentAns) throws IOException, ClassNotFoundException {
        writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, studentAns));
        
        //get feedback and mark:
        StudentMessage m = (StudentMessage) reader.readObject();
        if (m.getResponse()==StudentMessage.SUCCESS) {
            QuestionInfo qi = (QuestionInfo) m.getBody();
            currentFeedback = qi.getFeedback();
            currentMark = qi.getMark();
            currentOutOf = qi.getOutOf();
            nextQuestion = qi.getQuestion();
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean leaveAssignment() throws IOException, ClassNotFoundException {
        writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null));
        
        //get feedback and mark:
        StudentMessage m = (StudentMessage) reader.readObject();
        if (m.getResponse()==StudentMessage.SUCCESS) {
            currentCheckOutput = (String) m.getBody();
            return true;
        }
        else {
            return false;
        }
    }
    
    public File getSchemaImage() {
        return schemaImg;
    }
    
    public String getCurrentFeedback() {
        return currentFeedback;
    }

    public String getCurrentCheckOutput() {
        return currentCheckOutput;
    }

    public String getCurrentMark() {
        return currentMark + "/" + currentOutOf;
    }

    public String getNextQuestion() {
        return nextQuestion;
    }
    
}

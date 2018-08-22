package kiwi.kiwistudent;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    /**
     * Indicates that login was successful.
     */
    public static final String SUCCESS_LOGIN = "PASS";
    
    /**
     * Indicates that login was unsuccessful due to non-existent student number.
     */
    public static final String FAIL_LOGIN = "FAIL";
    
    /**
     * Indicates that login was unsuccessful due to failed database connection.
     */
    public static final String FAIL_CONNECT = "CON_ERR";
    
    
    //Instance Variables:
    
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
    
    /**
     * Checks whether student number given is stored in students table on the
     * database and initializes instance variables if login is successful.
     * @param studentNumber The student number of the student to login.
     * @return Success/error message.
     */
    public String login(String studentNumber) {
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
    }
    
    
    //Getters:
    
    /**
     * Gets student's highest grade.
     * @return Student's highest grade.
     */
    public double getHighestGrade() {
        return highestGrade;
    }
    
    /**
     * Gets student's number of submissions remaining.
     * That is, noSubmissionsAllowed - noSubmissionsCompleted.
     * @return Student's number of submissions remaining.
     */
    public int getNoSubmissionsRemaining() {
        return maxNoSubmissions - noSubmissionsCompleted;
    }
    
    public void decrementSubmissionsAllowed() {
        noSubmissionsCompleted++;
    }
    
    public void updateGrade(double grade) {
        if (grade>highestGrade) {
            highestGrade = grade;
            //update server
        }
    }
}

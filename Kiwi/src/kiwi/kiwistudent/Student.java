package kiwi.kiwistudent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import kiwi.kiwiserver.ServerStartup;
import kiwi.message.QuestionInfo;
import kiwi.message.StudentMessage;
import kiwi.message.StudentStatistics;

/**
 * Creates object to handle access to and control of student's information.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
//TODO: update grade/submissions on server at end of each assignment
public class Student {
    
    
    public static int FAIL_CONNECT = 0;
    public static int FAIL_LOGIN = 1;
    public static int SUCCESS_LOGIN = 2;
    
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
    int noSubmissionsRemaining;
    
    
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
    
    
    /**
     *  The date and the time of the deadline of the assignment
     */
    protected Date deadlineDay;
    protected Time deadlineTime;
    
    //Constructor:
    
    /**
     * Default constructor.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public Student() throws IOException, ClassNotFoundException {
            //make socket:
            studentSocket = new Socket(ServerStartup.SERVER_NAME, ServerStartup.STUDENT_PORT_NUM);
            System.out.println("Client port is: " + studentSocket.getLocalPort());
            //Set up streams:
            InputStream is= studentSocket.getInputStream();
            reader= new ObjectInputStream(is); //MESSAGE
            OutputStream os= studentSocket.getOutputStream();
            writer= new ObjectOutputStream(os);   //MESSAGE
            //check connection:
            StudentMessage response = (StudentMessage) reader.readObject();
            connected = (int)(response.getResponse())==StudentMessage.SUCCESS;
    }
    
    
    //Main functionality methods(public):
         /**
     * This student's socket which is used to communicate with the server.
     */
    public int login(String studentNumber) {
        try {
            //Check student number exists and load values:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, studentNumber));
            StudentMessage loginResponce = (StudentMessage) reader.readObject();
            if (loginResponce.getResponse()==StudentMessage.FAIL_CONNECT)
            {
                System.out.println("Error: Failed to connect.");
                return FAIL_CONNECT;
            }
            else if (loginResponce.getResponse()==StudentMessage.FAIL_INPUT)
            {
                System.out.println("Error: Student is not register in the databease.");
                return FAIL_LOGIN;
            }
            else
            {
                StudentStatistics st = (StudentStatistics) loginResponce.getBody();
                highestGrade = st.getHighestGrade();
                noSubmissionsRemaining = st.getNoSubmissionsRemaining();
                deadlineDay = st.getDeadlineDay();
                deadlineTime = st.getDeadlineTime();
                return SUCCESS_LOGIN;
            }
    
        } catch (ClassNotFoundException e) {  //can't read the return message
            System.out.println("Error: Unable to get responce from server.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
        //can't check student number
         catch (IOException ex) {
             System.out.println("Error: Unable to sent message to server.");
             System.out.println(ex);
             return FAIL_CONNECT;
        }
    }
    
    
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

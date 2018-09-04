package kiwi.kiwistudent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import kiwi.kiwiserver.DBConnectionException;
import kiwi.kiwiserver.ServerStartup;
import kiwi.message.LoginInfo;
import kiwi.message.QuestionInfo;
import kiwi.message.StatementOutput;
import kiwi.message.StudentStatement;
import kiwi.message.StudentMessage;
import kiwi.message.StudentStatistics;

/**
 * Creates object to handle access to and control of student's information.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Student {
    
    //Constants:
    /**
     * Action failed due to erroneous connection to database.
     */
    public static int FAIL_CONNECT = 1;
    
    /**
     * Login details failure. That is, student number not in database.
     */
    public static int FAIL_LOGIN = 2;
    
    /**
     * Action not allowed. 
     */
    public static int FAIL_DENY = 3;
    
    /**
     * Action executed successfully.
     */
    public static int SUCCESS = 0;
    
    /**
     * Action executed successfully.
     */
    public static int ASSIGNMENT_OVERDUE = 4;
    
    /**
     * Action executed successfully.
     */
    public static int FAIL_GEN = 5;
    
    
    //Instance Variables:
    
    //Assignment Running Variables:
    /**
     * Feedback for the latest submitted answer.
     */
    private String currentFeedback;
    
    /**
     * Output for the latest submitted statement to check.
     */
    private String currentCheckOutput;
    
    /**
     * Max available mark for the latest submitted question.
     */
    private int currentOutOf;
    
    /**
     * Mark received for the latest submitted answer.
     */
    private int currentMark;
    
    /**
     * Next question to be answered.
     */
    private String nextQuestion;
    
    /**
     * Number of the next question to be answered.
     */
    private int nextQuestionNo;
    
    /**
     * Final grade at present for the currently running assignment.
     */
    private double currentFinalGrade;
    
    /**
     * Number of questions in the current assignment.
     */
    private int noQuestions;
    
    /**
     * The schema diagram image representing the query data for the assignment.
     */
    private byte [] schemaImg;
    
    
    //Student Information Variables:
    /**
     * The student's student number.
     */
    protected String studentNo;
    
    /**
     * The student's highest recorded assignment grade.
     */
    protected double highestGrade;
    
    /**
     * The number of submissions the student has completed already.
     */
    protected int noSubmissionsRemaining;
    
    /**
     * The student's deadline date for the assignment.
     */
    protected Date deadlineDay;
    
    /**
     * The student's deadline time for the assignment.
     */
    protected Time deadlineTime;
    
    
    //TCP Message Variables:
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
    
    
    //Constructors:
    /**
     * Default constructor for Student object, which sets up the socket
     * connection and socket reader and writer.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws kiwi.kiwiserver.DBConnectionException
     * @author Tala Ross(rsstal002)
     */
    public Student() throws IOException, ClassNotFoundException, DBConnectionException {
        //Set up socket:
        studentSocket = new Socket(ServerStartup.SERVER_NAME, ServerStartup.STUDENT_PORT_NUM);
        
        //Set up streams:
        reader= new ObjectInputStream(studentSocket.getInputStream());
        writer= new ObjectOutputStream(studentSocket.getOutputStream());
        
        //Check connection:
        StudentMessage responseMessage = (StudentMessage) reader.readObject();
        int response = (int)(responseMessage.getResponse());
        
        //Couldn't create connection to database on server:
        if (!(response==StudentMessage.RESP_SUCCESS)) {  
            throw new DBConnectionException("Couldn't create connection to database on server.");
        }
    }
    
    /**
     * Informs server of close and then closes socket connections.
     * @throws IOException
     * @throws java.lang.ClassNotFoundException
     * @author Tala Ross(rsstal002)
     */
    public void closeConnection() throws IOException, ClassNotFoundException {
        writer.writeObject(new StudentMessage(StudentMessage.CMD_CLOSE, null));
        reader.close();
        writer.close();
        studentSocket.close();
    }
    
    
    //Main functionality methods(public):
    /**
     * Attempts to login student with given student number.
     * @param studentNumber The student number to try login.
     * @return SUCCESS, FAIL_LOGIN, or FAIL_CONNECT
     * @author Steve Shun Wang (wngshu003)
     */
    public int login(String studentNumber) {
        try {
            //Check student number exists and load values:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, new LoginInfo(studentNumber)));
            StudentMessage loginResponce = (StudentMessage) reader.readObject();
            switch (loginResponce.getResponse()) {
                case StudentMessage.RESP_FAIL_CONNECT:
                    System.out.println("Error: Failed to connect.");
                    return FAIL_CONNECT;
                case StudentMessage.RESP_FAIL_INPUT:
                    System.out.println("Error: Student is not register in the databease.");
                    return FAIL_LOGIN;
                default:
                    return SUCCESS;
            }
        } catch (ClassNotFoundException e) {  //Can't read the return message.
            System.out.println("Error: Unable to get responce from server.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
        catch (IOException ex) {    //Can't check student number.
             System.out.println("Error: Unable to sent message to server.");
             System.out.println(ex);
             return FAIL_CONNECT;
        }
    }
    
    /**
     * Requests a new assignment from server and loads assignment information,
     * including the total number of question, the first question and the
     * schema diagram image.
     * @return SUCCESS, FAIL_DENY or FAIL_CONNECT
     * @author Tala Ross(rsstal002)
     */
    public int startAssignment() {
        try {
            //Request new assignment and first question:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_START));
            
            //Get assignment information and first question:
            StudentMessage m = (StudentMessage) reader.readObject();
            switch (m.getResponse()) {
                case StudentMessage.RESP_SUCCESS:
                    QuestionInfo qi = (QuestionInfo) m.getBody();
                    noQuestions = qi.getTotalNoQuestions();
                    nextQuestionNo = qi.getQuestionNo();
                    nextQuestion = qi.getQuestion();
                    schemaImg = qi.getSchemaImg();
                    return SUCCESS;
                case StudentMessage.RESP_FAIL_DENY:  //No more submissions allowed
                    return FAIL_DENY;
                case StudentMessage.RESP_ASSIGNMENT_OVERDUE: //Past Assignment Deadline
                    return ASSIGNMENT_OVERDUE;
                case StudentMessage.RESP_FAIL_GEN: //Past Assignment Deadline
                    return FAIL_GEN;
                default:    //Problem getting info from server.
                    return FAIL_CONNECT;    
            }
        } catch (IOException | ClassNotFoundException e) {  //Problem reading messages.
            System.out.println("Error: Problem sending/receiving tcp socket messages in startMessage() method.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
    }
    
    /**
     * Checks students output from the given statement and saves the formatted
     * String output.
     * @param studentStatement The given SQL statement.
     * @return true if successfully executed action otherwise false (ie. couldn't get output)
     * @author Tala Ross(rsstal002)
     */
    public boolean check(String studentStatement) {
        try {
            //Request output from statement execution from server:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_CHECK, new StudentStatement(studentStatement)));
            
            //Get output and save in instance variables:
            StudentMessage m = (StudentMessage) reader.readObject();
            if (m.getResponse()==StudentMessage.RESP_SUCCESS) {
                currentCheckOutput = ((StatementOutput) m.getBody()).getOutput();
                return true;
            }
            else {  //Problem getting info from server.
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {   //Problem reading messages.
            System.out.println("Error: Problem sending/receiving tcp socket messages in check() method.");
            System.out.println(e);
            return false;
        }
    }
    
    /**
     * Takes in student answer gets feedback and marks and saves them.
     * @param studentAns Answer SQL statement.
     * @return true if successfully executed action otherwise false (ie. couldn't mark)
     * @author Tala Ross(rsstal002)
     */
    public int submit(String studentAns) {
        try {    
            //Request marking of answer statement execution from server:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, new StudentStatement(studentAns)));

            //Get feedback and mark and save in instance variables:
            StudentMessage m = (StudentMessage) reader.readObject();
            QuestionInfo qi;
            switch (m.getResponse()) {
                case StudentMessage.RESP_SUCCESS:
                    qi = (QuestionInfo) m.getBody();
                    currentFeedback = ((StatementOutput)qi.getFeedback()).getOutput();
                    currentMark = qi.getMark();
                    currentOutOf = qi.getOutOf();
                    nextQuestion = qi.getQuestion();
                    nextQuestionNo = qi.getQuestionNo();
                    currentFinalGrade = qi.getFinalGrade();
                    return SUCCESS;
                case StudentMessage.RESP_FAIL_INPUT: //Lecturer error in their saved answer
                    qi = (QuestionInfo) m.getBody();
                    currentFeedback = ((StatementOutput)qi.getFeedback()).getOutput();
                    currentOutOf = qi.getOutOf();
                    nextQuestion = qi.getQuestion();
                    nextQuestionNo = qi.getQuestionNo();
                    currentFinalGrade = qi.getFinalGrade();
                    return FAIL_DENY;
                default:    //Problem getting info from server.
                    return FAIL_CONNECT;
            }
        } catch (IOException | ClassNotFoundException e) {  //Problem reading messages.
            System.out.println("Error: Problem sending/receiving tcp socket messages in submit() method.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
    }
    
    /**
     * Tells the server that student is quitting the assignment and the
     * assignment grade must be save.
     * @return true if successfully executed action otherwise false (ie. couldn't save grade)
     * @author Tala Ross(rsstal002)
     */
    public boolean leaveAssignment() {
        try {
            //Tell server student wants to exit the assignment:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT));

            //Check whether grade was saved:
            StudentMessage m = (StudentMessage) reader.readObject();
            return m.getResponse()==StudentMessage.RESP_SUCCESS; //saved or didn't save grades
        } 
        catch (IOException | ClassNotFoundException e) {    //Problem reading messages.
            System.out.println("Error: Problem sending/receiving tcp socket messages in leaveAssignment() method.");
            System.out.println(e);
            return false;
        }
    }
    
    /**
     * Requests an updated version of the students statistics and updates the
     * saved values in instance variables. The statistics include highest grade,
     * number of submissions remaining and deadline info.
     * @return true if successfully executed action otherwise false (ie. couldn't get stats)
     * @author Tala Ross(rsstal002)
     */
    public boolean viewStats() {
        try {
            //Request student statistics from server:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS));

            //Get and save statistics:
            StudentMessage m = (StudentMessage) reader.readObject();
            if (m.getResponse()==StudentMessage.RESP_SUCCESS) {
                StudentStatistics ss = (StudentStatistics) m.getBody();
                highestGrade = ss.getHighestGrade();
                noSubmissionsRemaining = ss.getNoSubmissionsRemaining();
                deadlineDay = ss.getDeadlineDay();
                deadlineTime = ss.getDeadlineTime();
                return true;
            }
            else {  //Didn't manage to access new statistics.
                return false;
            }
        } 
        catch (IOException | ClassNotFoundException e) {     //Problem reading messages.
            System.out.println("Error: Problem sending/receiving tcp socket messages in viewStats() method.");
            System.out.println(e);
            return false;
        }
    }
    
    /**
     * Tells server that student is reporting a question as erroneously marked
     * and suggesting the correct answer.
     * @param suggested The given suggested answer from student.
     * @return true if successfully executed action otherwise false (ie. couldn't report question)
     * @author Tala Ross(rsstal002)
     */
    public boolean report(String suggested) {
        try {
            //Send report to server:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_REPORT, new StudentStatement(suggested)));
            
            //Check that report recorded:
            StudentMessage m = (StudentMessage) reader.readObject();
            return m.getResponse()==StudentMessage.RESP_SUCCESS; //success vs fail
        } 
        catch (IOException | ClassNotFoundException e) {     //Problem reading messages.
            System.out.println("Error: problem connecting to DB to report mistake.");
            System.out.println(e);
            return false;
        }
    }
    
    
    //Getters and Checkers:
    /**
     * Gets the the schema diagram image bytes.
     * @return The schema diagram image bytes
     */
    public byte [] getSchemaImage() {
        return schemaImg;
    }
    
    /**
     * Gets the most recent feedback.
     * @return The most recent feedback.
     */
    public String getCurrentFeedback() {
        return currentFeedback;
    }
    
    /**
     * Gets the most recent output from a statement check.
     * @return The most recent output check.
     */
    public String getCurrentCheckOutput() {
        return currentCheckOutput;
    }

    /**
     * Gets the most recent mark(and what it's out of) as a string
     * representation.
     * @return Current mark as a string.
     */
    public String getCurrentMarkString() {
        return currentMark + "/" + currentOutOf;
    }
    
    /**
     * Gets the most recent mark.
     * @return The most recent mark.
     */
    public int getCurrentMark() {
        return currentMark;
    }

    /**
     * Gets the current assignment's total grade.
     * @return The current assignment's total grade.
     */
    public String getFinalGrade() {
        DecimalFormat d = new DecimalFormat("0.00");
        return d.format(currentFinalGrade);
    }

    /**
     * Gets the next question to process.
     * @return The next question.
     */
    public String getNextQuestion() {
        return nextQuestion;
    }

    /**
     * Gets the next question number.
     * @return The next question number.
     */
    public int getNextQuestionNo() {
        return nextQuestionNo;
    }
    
    /**
     * Gets the number of questions in the current assignment.
     * @return The number of questions in the current assignment.
     */
    public int getNoQuestions() {
        return noQuestions;
    }

    /**
     * Checks whether the assignment is complete.
     * That is, there are no unanswered questions.
     * @return true if it is done and false if not.
     */
    public boolean isAssignmentDone() {
        return nextQuestion==null;
    }
    
    
}

package kiwi.kiwistudent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    
    public static int FAIL_CONNECT = 1;
    public static int FAIL_LOGIN = 2;
    public static int FAIL_DENY = 3;
    public static int SUCCESS = 0;
    
    //Instance Variables:
    
    //Assignment running
    private String currentFeedback;
    private String currentCheckOutput;
    private int currentOutOf;
    private int currentMark;
    private String nextQuestion;
    private int nextQuestionNo;
    
    private double currentFinalGrade;
    private int noQuestions;
    private byte [] schemaImg;
    
    //student info:
    /**
     * The student's student number.
     */
    String studentNo;
    
    /**
     * The student's highest recorded assignment grade.
     */
    protected double highestGrade;
    
    /**
     * The number of submissions the student has completed already.
     */
    protected int noSubmissionsRemaining;
    
    protected Date deadlineDay;
    protected Time deadlineTime;
    
    
    //TCP Messages:
    
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
            studentSocket = new Socket(ServerStartup.SERVER_NAME, ServerStartup.STUDENT_PORT_NUM);
            //System.out.println("Client port is: " + studentSocket.getLocalPort());
            //Set up streams:
            reader= new ObjectInputStream(studentSocket.getInputStream());
            writer= new ObjectOutputStream(studentSocket.getOutputStream());
            //check connection:
            StudentMessage response = (StudentMessage) reader.readObject();
            connected = (int)(response.getResponse())==StudentMessage.SUCCESS;
    }
    
    /**
     * Informs server of close and then closes socket connections.
     * @throws IOException
     */
    public void closeConnection() throws IOException, ClassNotFoundException {
        writer.writeObject(new StudentMessage(StudentMessage.CMD_CLOSE, null));
        reader.close();
        writer.close();
        studentSocket.close();
        connected = false;
    }
    
    
    //Main functionality methods(public):
         /**
     * This student's socket which is used to communicate with the server.
     * @param studentNumber
     * @return indication of whether login was successful or why it failed
     */
    public int login(String studentNumber) {
        try {
            //Check student number exists and load values:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_LOGIN, studentNumber));
            StudentMessage loginResponce = (StudentMessage) reader.readObject();
            switch (loginResponce.getResponse()) {
                case StudentMessage.FAIL_CONNECT:
                    System.out.println("Error: Failed to connect.");
                    return FAIL_CONNECT;
                case StudentMessage.FAIL_INPUT:
                    System.out.println("Error: Student is not register in the databease.");
                    return FAIL_LOGIN;
                default:
                    return SUCCESS;
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
    
    public int startAssignment() {
        try {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_START, null));
            
            //get feedback and mark:
            StudentMessage m = (StudentMessage) reader.readObject();
            switch (m.getResponse()) {
                case StudentMessage.SUCCESS:
                    QuestionInfo qi = (QuestionInfo) m.getBody();
                    noQuestions = qi.getTotalNoQuestions();
                    nextQuestionNo = qi.getQuestionNo();
                    nextQuestion = qi.getQuestion();
                    schemaImg = qi.getSchemaImg();
                    return SUCCESS;
                case StudentMessage.FAIL_DENY:
                    return FAIL_DENY;
                default:
                    return FAIL_CONNECT;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Problem sending/receiving tcp socket messages in startMessage() method.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
    }
    
    /**
     * Checks students output from the given statement and saves the formatted
     * String output.
     * @param studentStatement
     * @return true if successfully executed otherwise false (ie. couldn't mark)
     */
    public boolean check(String studentStatement) {
        try {
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Problem sending/receiving tcp socket messages in check() method.");
            System.out.println(e);
            return false;
        }
    }
    
    /**
     * Takes in student answer gets feedback and marks and saves them.
     * @param studentAns
     * @return true if successfully executed otherwise false (ie. couldn't mark)
     */
    public int submit(String studentAns) {
        try {    
            writer.writeObject(new StudentMessage(StudentMessage.CMD_SUBMIT, studentAns));

            //get feedback and mark:
            StudentMessage m = (StudentMessage) reader.readObject();
            QuestionInfo qi;
            switch (m.getResponse()) {
                case StudentMessage.SUCCESS:
                    qi = (QuestionInfo) m.getBody();
                    currentFeedback = qi.getFeedback();
                    currentMark = qi.getMark();
                    currentOutOf = qi.getOutOf();
                    nextQuestion = qi.getQuestion();
                    nextQuestionNo = qi.getQuestionNo();
                    currentFinalGrade = qi.getFinalGrade();
                    return SUCCESS;
                case StudentMessage.FAIL_INPUT: //lecturer error
                    qi = (QuestionInfo) m.getBody();
                    currentFeedback = qi.getFeedback();
                    currentOutOf = qi.getOutOf();
                    nextQuestion = qi.getQuestion();
                    nextQuestionNo = qi.getQuestionNo();
                    currentFinalGrade = qi.getFinalGrade();
                    return FAIL_DENY;
                default:    //DB connection error
                    return FAIL_CONNECT;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Problem sending/receiving tcp socket messages in submit() method.");
            System.out.println(e);
            return FAIL_CONNECT;
        }
    }
    
    public boolean leaveAssignment() {
        try {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_QUIT, null));

            //get feedback and mark:
            StudentMessage m = (StudentMessage) reader.readObject();
            return m.getResponse()==StudentMessage.SUCCESS; //saved or didn't save grades
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Problem sending/receiving tcp socket messages in leaveAssignment() method.");
            System.out.println(e);
            return false;
        }
    }
    
    public boolean viewStats() {
        try {
            writer.writeObject(new StudentMessage(StudentMessage.CMD_STATS, null));

            //get feedback and mark:
            StudentMessage m = (StudentMessage) reader.readObject();
            if (m.getResponse()==StudentMessage.SUCCESS) {  //saved grades
                StudentStatistics ss = (StudentStatistics) m.getBody();
                highestGrade = ss.getHighestGrade();
                noSubmissionsRemaining = ss.getNoSubmissionsRemaining();
                deadlineDay = ss.getDeadlineDay();
                deadlineTime = ss.getDeadlineTime();
                return true;
            }
            else {  //didn't manage to access new grades
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Problem sending/receiving tcp socket messages in viewStats() method.");
            System.out.println(e);
            return false;
        }
    }
    
    public byte [] getSchemaImage() {
        return schemaImg;
    }
    
    public String getCurrentFeedback() {
        return currentFeedback;
    }

    public String getCurrentCheckOutput() {
        return currentCheckOutput;
    }

    public String getCurrentMarkString() {
        return currentMark + "/" + currentOutOf;
    }
    
    public int getCurrentMark() {
        return currentMark;
    }

    public String getNextQuestion() {
        return nextQuestion;
    }

    public int getNextQuestionNo() {
        return nextQuestionNo;
    }

    public String getFinalGrade() {
        DecimalFormat d = new DecimalFormat("0.00");
        return d.format(currentFinalGrade);
    }

    public int getNoQuestions() {
        return noQuestions;
    }

    public boolean isAssignmentDone() {
        return nextQuestion==null;
    }

    public boolean report(String suggested) {
        try {
            //send report:
            writer.writeObject(new StudentMessage(StudentMessage.CMD_REPORT, suggested));
            //get response:
            StudentMessage m = (StudentMessage) reader.readObject();
            return m.getResponse()==StudentMessage.SUCCESS; //success vs fail
        } 
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: problem connecting to DB to report mistake.");
            System.out.println(e);
            return false;
        }
    }
    
}

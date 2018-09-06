package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining Different types of interaction 
 * between the lecturer and the server.
 * @author Nikai Jagganath (JGGNIK001)
 */
public class LecturerMessage implements Serializable{
    
    //Constants
    /**
     * Command to upload an assignment info set.
     */
    public static final int CMD_ASSIGNMENT_INFO = 0;
    
    /**
     * Command to upload students csv file.
     */
    public static final int CMD_UPLOAD_STUDENTS = 1;
    
    /**
     * Command to upload questions csv file.
     */
    public static final int CMD_UPLOAD_QUESTIONS = 2;
    
    /**
     * Command to upload query data files.
     */
    public static final int CMD_UPLOAD_QUERY = 3;
    
    /**
     * Command to get the student grades in grade descending order.
     */
    public static final int CMD_GRADE_DESC = 4;
    
    /**
     * Command to get the student grades in student number alphabetical order.
     */
    public static final int CMD_GRADE_ALPH = 5;
    
    /**
     * Command to reconnect to the database.
     */
    public static final int CMD_CONNECT = 6;
    
    /**
     * Command to close the connection.
     */
    public static final int CMD_CLOSE = 7;
    
    /**
     * Command to upload a schema image file.
     */
    public static final int CMD_UPLOAD_SCHEMA = 8;
    
    /**
     * Command to update an individual student's deadline.
     */
    public static final int CMD_UPDATE_DEADLINE = 9;
    
    /**
     * Command to reset the students table.
     */
    public static final int CMD_RESET_STUDENTS = 10;
    
    /**
     * Command to reset the questions table.
     */
    public static final int CMD_RESET_QUESTIONS = 11;
    
    /**
     * Command to reset all query data tables.
     */
    public static final int CMD_RESET_QUERY_DATA = 12;
    
    /**
     * Response for successful action execution.
     */
    public static final int RESP_SUCCESS = 0;
    
    /**
     * Response for failed action execution due to database connection error.
     */
    public static final int RESP_FAIL_CONNECT = 1;
    
    /**
     * Response for failed action execution due to missing prerequisites(such as
     * assignment info uploaded).
     */
    public static final int RESP_FAIL_NULL = 2;
    
    /**
     * Response for failed action execution due to incorrect input format.
     */
    public static final int RESP_FAIL_INPUT = 3;
    
    /**
     * Response for failed action execution due to insufficient amount and
     * variety of questions for question csv upload.
     */
    public static final int RESP_FAIL_QUESTIONS = 4;
    
    
    //Instance Variables:
    
    /**
     * The message command, indicating message type.
     */
    private final int cmd;
    
    /**
     * The message body, containing the information to be shared.
     */
    private final Object body;
    
    /**
     * The message response, used by server to indicate the state of the action.
     * That is, was it successful or not.
     */
    private int returnMessage;

    // Constructors:
    /**
     * Construct the Lecturer Message using the parameters provided below 
     * with a return message
     * @param cmd The message command.
     * @param body The message body/contents.
     * @param returnMessage the response type.
     */
    public LecturerMessage(int cmd, Object body, int returnMessage) {
        this.cmd = cmd;
        this.body = body;
        this.returnMessage = returnMessage;
    }

    /**
     * Construct the Lecturer Message using the parameters provided below
     * With no return message
     * @param cmd The message command.
     * @param body The message body/contents.
     */
    public LecturerMessage(int cmd, Object body) {
        this.cmd = cmd;
        this.body = body;
    }

    // Getters:
    /**
     * Get the Command
     * @return The Fixed Variable command
     */
    public int getCmd() {
        return cmd;
    }

    /**
     * Get the body of the message
     * @return the body
     */
    public Object getBody() {
        return body;
    }
    
    /**
     * Get the return message
     * @return message
     */
    public int getMessage() {
        return returnMessage;
    }
}

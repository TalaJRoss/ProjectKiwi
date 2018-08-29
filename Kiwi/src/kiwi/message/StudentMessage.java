package kiwi.message;

import java.io.Serializable;

/**
 * Object encapsulating all information to be sent between the student and
 * lecturer clients and the server.
 * This includes:
 * the command - identifies the type of message
 * the body - contains the information to be shared
 * the response - indicates whether the information could be successfully
 * retrieved or not, and, if not, why not.
 * @author Tala Ross(rsstal002)
 */
public class StudentMessage implements Serializable{
    
    
    //Constants:
    
    //Command Constants:
    /**
     * Identifies a command to start an assignment from student and the server's
     * response messages to this command.
     */
    public static final int CMD_START = 0;
    
    /**
     * Identifies a command to submit an answer statement for the current
     * loaded assignment question from student and the server's response
     * messages to this command.
     */
    public static final int CMD_SUBMIT = 1;
    
    /**
     * Identifies a command to check an answer statement from student and the
     * server's response messages to this command.
     */
    public static final int CMD_CHECK = 2;
    
    /**
     * Identifies a command to get the student's statistics(highest grade etc...)
     * from the student-end and the server's response messages to this command.
     */
    public static final int CMD_STATS = 3;
    
    /**
     * Identifies a command to process a login for a given student number
     * from the student-end and the server's response messages to this command.
     */
    public static final int CMD_LOGIN = 4;
    
    /**
     * Identifies a command to process a database connection setup
     * from the student-end and the server's response messages to this command.
     */
    public static final int CMD_CONNECT = 5;
    
    /**
     * Identifies a command to process an assignment quit and grade save
     * from the student-end and the server's response messages to this command.
     */
    public static final int CMD_QUIT = 6;
    
    /**
     * Identifies a command to process a log off and close connection
     * from the student-end and the server's response messages to this command.
     */
    public static final int CMD_CLOSE = 7;
    
    /**
     * Identifies a command to report the current loaded question as erroneous
     * from the student-end and the server's response messages to this command.
     */
    public static final int CMD_REPORT = 8;
    
    
    //Response Constants:
    /**
     * Indicates that action was successfully completed.
     */
    public static final int RESP_SUCCESS = 0;
    
    /**
     * Indicates that action was unsuccessful due to input error.
     * The context of this may be specific to the type of message and must be
     * clearly indicated by the implementor.
     */
    public static final int RESP_FAIL_INPUT = 1;
    
    /**
     * Indicates that action was unsuccessful due to failed database connection.
     */
    public static final int RESP_FAIL_CONNECT = 2;
    
    /**
     * Indicates that action was denied or disallowed.
     * The context of this may be specific to the type of message and must be
     * clearly indicated by the implementor.
     */
    public static final int RESP_FAIL_DENY = 3;
    
    
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
    private final int response;

    
    //Constructors:
    /**
     * Instantiates all fields of a StudentMessage, setting the response to
     * indicate a successful execution of the command.
     * This should be used when no error occurs when executing the command
     * action on the server.
     * @param cmd The message command.
     * @param body The message body/contents.
     */
    public StudentMessage(int cmd, Object body) {
        this.cmd = cmd;
        this.body = body;
        this.response = RESP_SUCCESS;
    }
    
    /**
     * Instantiates all fields of a StudentMessage.
     * This should be used when an error occurs when executing the command
     * action on the server.
     * @param cmd The message command.
     * @param body The message body/contents.
     * @param response The message response(success/fail).
     */
    public StudentMessage(int cmd, Object body, int response) {
        this.cmd = cmd;
        this.body = body;
        this.response = response;
    }
    
    
    //Getters:
    /**
     * Gets the message command.
     * @return The message command.
     */
    public int getCmd() {
        return cmd;
    }
    
    /**
     * Gets the message body/contents.
     * @return The message body.
     */
    public Object getBody() {
        return body;
    }
    
    /**
     * Gets the command execution response.
     * @return The message response.
     */
    public int getResponse() {
        return response;
    }
    
}

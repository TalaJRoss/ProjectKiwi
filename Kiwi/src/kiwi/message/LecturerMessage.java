/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining Different types of interaction 
 * between the lecturer and the server.
 * @author nikai
 */
public class LecturerMessage implements Serializable{
    
    //Constants
    public static final int CMD_ASSIGNMENT_INFO = 0;
    
    public static final int CMD_UPLOAD_STUDENTS = 1;
    public static final int CMD_UPLOAD_QUESTIONS = 2;
    public static final int CMD_UPLOAD_QUERY = 3;
    
    public static final int CMD_GRADE_DESC = 4;
    public static final int CMD_GRADE_ALPH = 5;
    
    public static final int CMD_CONNECT = 6;
    public static final int CMD_CLOSE = 7;
    
    public static final int CMD_UPLOAD_SCHEMA = 8;
    public static final int CMD_UPDATE_DEADLINE = 9;
    
    public static final int CMD_RESET_STUDENTS = 10;
    public static final int CMD_RESET_QUESTIONS = 11;
    public static final int CMD_RESET_QUERY_DATA = 12;
    
    public static final int RESP_SUCCESS = 0;
    public static final int RESP_FAIL_CONNECT = 1;
    public static final int RESP_FAIL_NULL = 2;
    public static final int RESP_FAIL_INPUT = 3;
    public static final int RESP_FAIL_QUESTIONS = 4;
    
    //Instance Variables:
    private int cmd;
    private Object body;
    private int returnMessage;

    // Constructors:
    /**
     * Construct the Lecturer Message using the parameters provided below 
     * with a return message
     * @param cmd
     * @param body
     * @param returnMessage 
     */
    public LecturerMessage(int cmd, Object body, int returnMessage) {
        this.cmd = cmd;
        this.body = body;
        this.returnMessage = returnMessage;
    }

    /**
     * Construct the Lecturer Message using the parameters provided below
     * With no return message
     * @param cmd
     * @param body 
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

import java.io.Serializable;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class StudentMessage implements Serializable{
    //from student:
    //start_assignment
    //get schema
    //submit answer
    //checkoutput
    //getStats(highest grade/no. subs left/deadline + time remaining)
    
    //Constants:
    public static final int CMD_START = 0;
    public static final int CMD_SUBMIT = 1;
    public static final int CMD_CHECK = 2;
    public static final int CMD_STATS = 3;
    public static final int CMD_LOGIN = 4;
    public static final int CMD_CONNECT = 5;
    public static final int CMD_QUIT = 6;
    
    
    /**
     * Indicates that action was unsuccessful due to input error.
     */
    public static final int SUCCESS = 0;
    
    /**
     * Indicates that action was unsuccessful due to input error.
     */
    public static final int FAIL_INPUT = 1;
    
    /**
     * Indicates that action was unsuccessful due to failed database connection.
     */
    public static final int FAIL_CONNECT = 2;
    
    /**
     * Indicates that action was unsuccessful due to failed database connection.
     */
    public static final int FAIL_DENY = 3;
    
    
    
    //Instance Variables:
    private int cmd;
    private Object body;
    private int response;

    
    //there was an error
    public StudentMessage(int cmd, Object body, int returnMessage) {
        this.cmd = cmd;
        this.body = body;
        this.response = returnMessage;
    }
    
    //no error
    public StudentMessage(int cmd, Object body) {
        this.cmd = cmd;
        this.body = body;
        this.response = SUCCESS;
    }

    public int getCmd() {
        return cmd;
    }

    public Object getBody() {
        return body;
    }
    
    public int getResponse() {
        return response;
    }
    
}

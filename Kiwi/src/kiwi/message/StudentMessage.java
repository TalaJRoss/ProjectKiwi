/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class StudentMessage {
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
    
    public static final int RESP_SUCCESS = 1;
    public static final int RESP_FAIL = 0;
    
    /**
     * Indicates that action was unsuccessful due to input error.
     */
    public static final String FAIL_INPUT = "INP_ERR";
    
    /**
     * Indicates that action was unsuccessful due to failed database connection.
     */
    public static final String FAIL_CONNECT = "CON_ERR";
    
    
    //Instance Variables:
    private int cmd;
    private Object body;
    private String returnMessage;


    public StudentMessage(int cmd, Object body, String returnMessage) {
        this.cmd = cmd;
        this.body = body;
        this.returnMessage = returnMessage;
    }

    public StudentMessage(int cmd, Object body) {
        this.cmd = cmd;
        this.body = body;
    }

    public int getCmd() {
        return cmd;
    }

    public Object getBody() {
        return body;
    }
    
    public String getMessage() {
        return returnMessage;
    }
    
}

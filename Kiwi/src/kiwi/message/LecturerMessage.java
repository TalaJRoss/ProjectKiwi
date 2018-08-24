/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

import java.io.Serializable;

/**
 *
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
    
    public static final int RESP_SUCCESS = 1;
    public static final int RESP_FAIL = 0;
    
    //Instance Variables:
    private int cmd;
    private Object body;
    private int returnMessage;


    public LecturerMessage(int cmd, Object body, int returnMessage) {
        this.cmd = cmd;
        this.body = body;
        this.returnMessage = returnMessage;
    }

    public LecturerMessage(int cmd, Object body) {
        this.cmd = cmd;
        this.body = body;
    }

    public int getCmd() {
        return cmd;
    }

    public Object getBody() {
        return body;
    }
    
    public int getMessage() {
        return returnMessage;
    }
}

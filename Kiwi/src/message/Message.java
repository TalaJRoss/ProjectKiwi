/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class Message implements Serializable{
    
    //Constants from lecturer:
    public static final String CMD_UPLOAD_STUDENTS = "UploadStudents";
    public static final String CMD_UPLOAD_QUESTIONS = "UploadQuestions";
    public static final String CMD_UPLOAD_QUERY = "UploadQuery";
    public static final String CMD_GRADE_DESC = "Descending";
    public static final String CMD_GRADE_ALPH = "Alphabetical";
    public static final String CMD_CONNECT = "Connect";
    
    //Constants from server:
    public static final String RES_SUCCESS = "Success";
    public static final String RES_FAIL = "Fail";
    
    //Instance Variables:
    private File [] csvFiles;  
    private String toReturn;
    private String cmd;
    private String response;    //success vs fail

    public Message(File [] csvFiles, String toReturn, String cmd, String response) {
        this.csvFiles = csvFiles;
        this.toReturn = toReturn;
        this.cmd = cmd;
        this.response = response;
    }

    public String getToReturn() {
        return toReturn;
    }

    public File [] getCsvFiles() {
        return csvFiles;
    }
    
    public File getCsvFile() {
        return csvFiles[0];
    }

    public String getCmd() {
        return cmd;
    }

    public String getResponse() {
        return response;
    }
    
    
    
}

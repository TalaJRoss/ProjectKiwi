/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class LecturerMessage implements Serializable{
    
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
    private ArrayList<byte []> csvFiles;  
    private String [] fileNames;
    private String toReturn;
    private String cmd;
    private String response;    //success vs fail

    public LecturerMessage(ArrayList<byte []> csvFiles, String [] fileNames, String toReturn, String cmd, String response) {
        this.csvFiles = csvFiles;
        this.fileNames = fileNames;
        this.toReturn = toReturn;
        this.cmd = cmd;
        this.response = response;
    }

    public String getToReturn() {
        return toReturn;
    }

    public ArrayList<byte []> getCsvFiles() {
        return csvFiles;
    }
    
    public byte [] getCsvFile() {
        return csvFiles.get(0);
    }

    public String[] getFileNames() {
        return fileNames;
    }
    
    public String getFileName() {
        return fileNames[0];
    }

    public String getCmd() {
        return cmd;
    }

    public String getResponse() {
        return response;
    }
    
    
    
}

/*
 * Contains the information regarding the assignment that must be stored on the server
 */
package kiwi.message;

import java.io.Serializable;

/**
 *
 * @author nikai
 */
public class AssignmentInfo implements Serializable{
    
    //Instance variables
    private String noSubmissions;
    private String noQuestions;
    private String date;
    private String time;
    private boolean closedPrac;

    public AssignmentInfo(String noSubmissions, String noQuestions, String date, String time, boolean closedPrac) {
        this.noSubmissions = noSubmissions;
        this.noQuestions = noQuestions;
        this.date = date;
        this.time = time;
        this.closedPrac = closedPrac;
    }

    public String getNoSubmissions() {
        return noSubmissions;
    }

    public String getNoQuestions() {
        return noQuestions;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isClosedPrac() {
        return closedPrac;
    }
    
}

/*
 * Contains the information regarding the assignment that must be stored on the server
 */
package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a uploading assignment information
 * in a CMD_ASSIGNMENT_INFO
 * message from Lecturer client to server.
 * @author nikai
 */
public class AssignmentInfo implements Serializable{
    
    //Instance variables
    private String noSubmissions;
    private String noQuestions;
    private String date;
    private String time;
    private boolean closedPrac;

    // Constuctors:
    /**
     * Construct the AssignmentInfo using the parameters provided below
     * @param noSubmissions
     * @param noQuestions
     * @param date
     * @param time
     * @param closedPrac 
     */
    public AssignmentInfo(String noSubmissions, String noQuestions, String date, String time, boolean closedPrac) {
        this.noSubmissions = noSubmissions;
        this.noQuestions = noQuestions;
        this.date = date;
        this.time = time;
        this.closedPrac = closedPrac;
    }

    // Getters:
    /**
     * Gets the number of submissions.
     * @return number of submissions
     */
    public String getNoSubmissions() {
        return noSubmissions;
    }

    /**
     * Gets the number of Questions.
     * @return The number of questions
     */
    public String getNoQuestions() {
        return noQuestions;
    }

    /**
     * Gets the date
     * @return  The Data
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time
     * @return The Time 
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the boolean indicating whether the assignment is a closed prac or not
     * @return Boolean of closed prac
     */
    public boolean isClosedPrac() {
        return closedPrac;
    }
    
}

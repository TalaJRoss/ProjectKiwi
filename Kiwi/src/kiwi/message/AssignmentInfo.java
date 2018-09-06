package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a uploading assignment information
 * in a CMD_ASSIGNMENT_INFO
 * message from Lecturer client to server.
 * @author Nikai Jagganath (JGGNIK001)
 */
public class AssignmentInfo implements Serializable{
    
    //Instance variables
    
    /**
     * The number of submissions per student allowed.
     */
    private final String noSubmissions;
    
    /**
     * The number of questions in each assignment.
     */
    private final String noQuestions;
    
    /**
     * The deadline date.
     */
    private final String date;
    
    /**
     * The deadline time.
     */
    private final String time;
    
    /**
     * Whether or not this is a closed practical assignment.
     * true if it is a closed practical otherwise false.
     */
    private final boolean closedPrac;

    // Constuctors:
    /**
     * Construct the AssignmentInfo using the parameters provided below
     * @param noSubmissions the number of submissions per student allowed.
     * @param noQuestions the number of questions in each assignment.
     * @param date deadline date
     * @param time deadline time
     * @param closedPrac true if it is a closed practical otherwise false
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
     * @return true if it is a closed practical otherwise false
     */
    public boolean isClosedPrac() {
        return closedPrac;
    }
    
}

package kiwi.message;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;

/**
 * Message body object containing information about a student's assignment
 * statistics, including their highest grade, number of submissions remaining,
 * deadline day and deadline time.
 * This is used as the body of messages between student clients and the server
 * for a CMD_STATS command.
 * @author Tala Ross(rsstal002)
 */
public class StudentStatistics implements Serializable{
    
    //Instance Variables:
    /**
     * The student's highest grade, currently.
     */
    private final double highestGrade;
    
    /**
     * The number of submissions the student is still allowed.
     */
    private final int noSubmissionsRemaining;
    
    /**
     * The deadline date for the assignment specific to the student.
     * This may differ from the overall student assignment deadline date if it
     * has been specifically updated on the lecturer end.
     */
    private final Date deadlineDay;
    
    /**
     * The deadline time for the assignment specific to the student.
     * This may differ from the overall student assignment deadline time if it
     * has been specifically updated on the lecturer end.
     */
    private final Time deadlineTime;
    
    
    //Constructors:
    /**
     * Initiates all fields in the StudentStatistics object.
     * This object contains information about a student's assignment statistics.
     * @param highestGrade The student's highest grade, currently.
     * @param noSubmissionsRemaining The number of submissions the student is still allowed.
     * @param deadlineDay The deadline date for the assignment specific to the student.
     * @param deadlineTime The deadline time for the assignment specific to the student.
     */
    public StudentStatistics(double highestGrade, int noSubmissionsRemaining, Date deadlineDay, Time deadlineTime) {
        this.highestGrade = highestGrade;
        this.noSubmissionsRemaining = noSubmissionsRemaining;
        this.deadlineDay = deadlineDay;
        this.deadlineTime = deadlineTime;
    }
    
    
    //Getters:
    /**
     * Gets the student's current highest grade. 
     * @return The student's highest grade.
     */
    public double getHighestGrade() {
        return highestGrade;
    }
    
    /**
     * Gets the number of submissions the student is still allowed.
     * @return The number of submissions remaining.
     */
    public int getNoSubmissionsRemaining() {
        return noSubmissionsRemaining;
    }
    
    /**
     * Gets the deadline date for the assignment(specific to the student).
     * @return The deadline date.
     */
    public Date getDeadlineDay() {
        return deadlineDay;
    }
    
    /**
     * Gets the deadline time for the assignment(specific to the student).
     * @return The deadline time.
     */
    public Time getDeadlineTime() {
        return deadlineTime;
    }
    
    
}

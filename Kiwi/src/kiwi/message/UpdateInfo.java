package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a update deadline sent in a CMD_UPDATE_DEADLINE
 * message from Lecturer client to server.
 * @author Steve Shun Wang (WNGSHU003)
 */
public class UpdateInfo implements Serializable{
    
    /**
     * Student number of student who's deadline must be updated.
     */
    String studentNo;
    
    /**
     * Date to change deadline to.
     */
    String date;
    
    /**
     * Time to change deadline to.
     */
    String time;
    
    //Constuctor:
    /**
     * Construct the Update Info with the parameters below
     * @param studentNo student number of student who's deadline must be updated.
     * @param date date to change deadline to.
     * @param time time to change deadline to.
     */
    public UpdateInfo(String studentNo, String date, String time) {
        this.studentNo = studentNo;
        this.date = date;
        this.time = time;
    }
    
    //Getters:
    /**
     * Get the student Number
     * @return the StudentNumber
     */
    public String getStudentNo() {
        return studentNo;
    }

    /**
     * Get the date
     * @return Date
     */
    public String getDate() {
        return date;
    }
    
    /**
     * Get the time
     * @return Time
     */
    public String getTime() {
        return time;
    }
}


package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a update deadline sent in a CMD_UPDATE_DEADLINE
 * message from Lecturer client to server.
 * @author Steven
 */
public class UpdateInfo implements Serializable{

    String studentNo;
    String date;
    String time;
    
    //Constuctor:
    /**
     * Construct the Update Info with the parameters below
     * @param studentNo
     * @param date
     * @param time 
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

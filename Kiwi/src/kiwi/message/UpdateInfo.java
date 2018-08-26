
package kiwi.message;

import java.io.Serializable;

/**
 *
 * @author Steven
 */
public class UpdateInfo implements Serializable{

    String studentNo;
    String date;
    String time;
    
    public UpdateInfo(String studentNo, String date, String time) {
        this.studentNo = studentNo;
        this.date = date;
        this.time = time;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}

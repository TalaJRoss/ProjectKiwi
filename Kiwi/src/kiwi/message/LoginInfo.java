package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a student login sent in a CMD_LOGIN
 * message from student client to server.
 * This effectively wraps the student number string in an studentNo object. 
 This may be used later if additional information, such as a password, needs
 to be sent in a student's CMD_LOGIN message body.
 * @author Tala Ross(rsstal002)
 */
public class LoginInfo implements Serializable {
    
    //Instance Variables:
    /**
     * Student number of student logging in.
     */
    private final String studentNo;
    
    
    //Constructors:
    /**
     * Creates a LoginInfo object with the provided student number.
     * @param studentNo Provided student number.
     */
    public LoginInfo(String studentNo) {
        this.studentNo = studentNo;
    }
    
    
    //Getters:
    /**
     * Gets the student number.
     * @return The student number.
     */
    public String getStudentNo() {
        return studentNo;
    }
    
}

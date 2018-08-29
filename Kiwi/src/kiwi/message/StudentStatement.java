package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a student statement sent in CMD_SUBMIT,
 * CMD_CHECK or CMD_REPORT messages from student client to server.
 * This effectively wraps the student statement string in an statement object. 
 * This may be used later if additional information needs to be sent in a
 * student's CMD_SUBMIT, CMD_CHECK or CMD_REPORT message body.
 * @author Tala Ross(rsstal002)
 */
public class StudentStatement implements Serializable {
    
    //Instance Variables:
    /**
     * Statement w.r.t.the current loaded assignment question provided by student.
     */
    private final String statement;
    
    
    //Constructors:
    /**
     * Creates a StudentStatement object with the student's provided statement.
     * @param statement Provided student statement.
     */
    public StudentStatement(String statement) {
        this.statement = statement;
    }
    
    
    //Getters:
    /**
     * Gets the student's statement.
     * @return The student's statement.
     */
    public String getStatement() {
        return statement;
    }
    
    
}

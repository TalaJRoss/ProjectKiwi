
package kiwi.message;

import java.io.Serializable;

/**
 *
 * @author nikai
 */
public class Grades implements Serializable{
    
    private String grades;

    public Grades(String grades) {
        this.grades = grades;
    }

    public String getGrades() {
        return grades;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class StudentStatistics implements Serializable{
    private double highestGrade;
    private int noSubmissionsRemaining;
    private Date deadlineDay;
    private Time deadlineTime;

    public StudentStatistics(double highestGrade, int noSubmissionsRemaining, Date deadlineDay, Time deadlineTime) {
        this.highestGrade = highestGrade;
        this.noSubmissionsRemaining = noSubmissionsRemaining;
        this.deadlineDay = deadlineDay;
        this.deadlineTime = deadlineTime;
    }

    public double getHighestGrade() {
        return highestGrade;
    }

    public int getNoSubmissionsRemaining() {
        return noSubmissionsRemaining;
    }

    public Date getDeadlineDay() {
        return deadlineDay;
    }

    public Time getDeadlineTime() {
        return deadlineTime;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwistudent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Tala Ross(rsstal002)
 */
//TODO: update grade/submissions on server at end of each assignment
public class Student {
    
    public static final String SUCCESS_LOGIN = "PASS";
    public static final String FAIL_LOGIN = "FAIL";
    public static final String FAIL_CONNECT = "CON_ERR";
    
    //TODO: get from server
    int maxNoSubmissions = 3;
    
    String studentNo;
    int highestGrade;
    int noSubmissionsCompleted;
    
    public String login(String studentNumber) {
        try {
            //Setup connection:
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            // get student output:
            Statement st= conn.createStatement();
            String statement= "SELECT * FROM students WHERE StudentNo LIKE '" + studentNumber + "'";
            ResultSet rs= st.executeQuery(statement);
            
            //check studentNo exists and load values:
            
            if (rs.next()) {
                studentNo = (String) rs.getObject("studentNo");
                highestGrade = (int) rs.getObject("highestGrade");
                noSubmissionsCompleted = (int) rs.getObject("noSubmissionsCompleted");
                return SUCCESS_LOGIN;
            }
            return FAIL_LOGIN;
        }
        catch (SQLException ex) {   
            System.out.println("DEBUG");
            ex.printStackTrace();
            return FAIL_CONNECT;
        } 
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return FAIL_CONNECT;
        }
    }

    public int getHighestGrade() {
        return highestGrade;
    }

    public int getNoSubmissionsRemaining() {
        return maxNoSubmissions - noSubmissionsCompleted;
    }
    
    
    
}

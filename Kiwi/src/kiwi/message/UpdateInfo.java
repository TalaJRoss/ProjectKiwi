/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

/**
 *
 * @author Steven
 */
public class UpdateInfo {

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

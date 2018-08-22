/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

import java.io.Serializable;
import kiwi.kiwiserver.Assignment;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class QuestionInfo implements Serializable{
    
    private int questionNo;
    private int totalNoQuestions;
    private String question;
    private String answer;
    private int mark;
    private int outOf;
    private String feedback;
    private double finalGrade;
    private byte [] schemaImg;

    public QuestionInfo(int questionNo, int totalNoQuestions, String question, String answer, int mark, int outOf, String feedback, double finalGrade, byte [] schemaImg) {
        this.questionNo = questionNo;
        this.totalNoQuestions = totalNoQuestions;
        this.question = question;
        this.answer = answer;
        this.mark = mark;
        this.outOf = outOf;
        this.feedback = feedback;
        this.finalGrade = finalGrade;
        this.schemaImg = schemaImg;
    }
    
    //from student
    public QuestionInfo(int questionNo, String answer) {
        this.questionNo = questionNo;
        this.answer = answer;
    }
    
    //creates return object
    public QuestionInfo(Assignment assignment, String studentAns) {
        this.schemaImg = null;
        this.answer = null;
        
        this.mark = assignment.mark(studentAns);    //mark of current
        this.outOf = assignment.getQuestion().getOutOf();
        this.feedback = assignment.getFeedback(studentAns);
        
        if (assignment.getQuestionNumber()+1>assignment.getNoQuestions()) { //no questions left
            this.question = null;
            this.questionNo = -1;
            this.totalNoQuestions = assignment.getNoQuestions();   //total no. questions
            this.finalGrade = assignment.getGrade();
        }
        else {  //questions left
            this.question = assignment.nextQuestion().getQuestion();    //question text of next q
            this.questionNo = assignment.getQuestionNumber();   //question no. of next q
            this.totalNoQuestions = assignment.getNoQuestions();   //total no. questions
            this.finalGrade = 0;
        }
    }
    
    //create first return obj:
    public QuestionInfo(Assignment assignment, byte [] schemaImg) {
        this.mark = 0;
        this.outOf = 0;
        this.feedback = null;
        this.answer = null;
        this.finalGrade = 0;
        
        this.question = assignment.nextQuestion().getQuestion();    //question text of next q
        this.questionNo = assignment.getQuestionNumber();   //question no. of next q
        this.totalNoQuestions = assignment.getNoQuestions();   //total no. questions
        
        this.schemaImg = schemaImg;
    }

    public int getQuestionNo() {
        return questionNo;
    }

    public int getTotalNoQuestions() {
        return totalNoQuestions;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMark() {
        return mark;
    }

    public int getOutOf() {
        return outOf;
    }

    public String getFeedback() {
        return feedback;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public byte[] getSchemaImg() {
        return schemaImg;
    }
    
    
}

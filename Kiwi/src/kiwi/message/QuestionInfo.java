/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.message;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class QuestionInfo {
    
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

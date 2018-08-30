package kiwi.message;

import java.io.Serializable;
import kiwi.kiwiserver.Assignment;

/**
 * Message body object containing information about the current and next
 * question in the loaded assignment at its current point.
 * This is used as the body of response messages from server to student clients
 * for a CMD_SUBMIT command and for the first question information for a
 * CMD_START command when an assignment is started.
 * @author Tala Ross(rsstal002)
 */
public class QuestionInfo implements Serializable{
    
    //Instance Variables:
    /**
     * The question number of the question who's information is contained.
     * This is initiated when message sent from server-end on CMD_SUBMIT or 
     * CMD_START.
     */
    private int questionNo;
    
    /**
     * The total number of questions in an assignment.
     * This is initiated when message sent from server-end on CMD_START only.
     */
    private int totalNoQuestions;
    
    /**
     * The English question.
     * This is initiated when message sent from server-end on CMD_SUBMIT or 
     * CMD_START.
     */
    private String question;
    
    /**
     * Mark for the previous question.
     * This is initiated when message sent from server-end on CMD_SUBMIT.
     */
    private int mark;
    
    /**
     * Total marks available for the previous question.
     * This is initiated when message sent from server-end on CMD_SUBMIT.
     */
    private int outOf;
    
    /**
     * Feedback for the previous question.
     * This is initiated when message sent from server-end on CMD_SUBMIT.
     */
    private String feedback;
    
    /**
     * Total mark overall for the whole assignment.
     * This is initiated when message sent from server-end on CMD_SUBMIT of the
     * last question in the assignment.
     */
    private double finalGrade;
    
    /**
     * Byte array representing the image of the schema diagram for the query
     * data.
     * This is initiated when message sent from server-end on CMD_START only.
     */
    private byte [] schemaImg;
    
    
    //Constructors:
    
    /**
     * Creates a QuestionInfo object with information from the given assignment
     * object and based on grading using the given student answer.
     * This should be used to create the message body of CMD_SUBMIT response
     * messages.
     * @param assignment The current assignment, who's current/next question, this info object refers to.
     * @param studentAns The student answer for the current question.
     */
    public QuestionInfo(Assignment assignment, String studentAns) {
        //Mark the current question:
        this.mark = assignment.mark(studentAns);
        this.outOf = assignment.getQuestion().getMaxMark();
        this.feedback = assignment.getFeedback(studentAns);
        
        //Get the next question's info or calculate the final grade:
        if (assignment.hasNext()) {  //there are questions left
            this.question = assignment.nextQuestion().getQuestion();    //question text of next question
            this.questionNo = assignment.getQuestionNumber();   //question no. of next qusetion
            this.finalGrade = 0;
        }
        else { //no questions left so get final grade
            this.question = null;
            this.questionNo = totalNoQuestions + 1;     //for student-end processing purposes(not incremented inquestion object)
            this.finalGrade = assignment.getGrade();
        }
    }
    
    /**
     * Creates a QuestionInfo object with information from the given assignment
     * object and the schema image bytes.
     * This should be used to create the message body of CMD_START response
     * messages. That is, this is the first assignment response message.
     * @param assignment The current assignment, who's current/next question, this info object refers to.
     * @param schemaImg The byte array representation of the schema diagram image.
     */
    public QuestionInfo(Assignment assignment, byte [] schemaImg) {
        //Get information for first question:
        this.question = assignment.nextQuestion().getQuestion();    //question text of first question
        this.questionNo = assignment.getQuestionNumber();   //question no. of first question
        this.totalNoQuestions = assignment.getNoQuestions();   //total no. questions in the assignment
        
        //Get schema image bytes:
        this.schemaImg = schemaImg;
    }
    
    
    //Getters:
    
    /**
     * Gets the question number of the question contained.
     * @return The question number.
     */
    public int getQuestionNo() {
        return questionNo;
    }
    
    /**
     * Gets the total number of questions in the assignment.
     * @return The total number of questions.
     */
    public int getTotalNoQuestions() {
        return totalNoQuestions;
    }
    
    /**
     * Gets the question text of the question contained.
     * @return The question text.
     */
    public String getQuestion() {
        return question;
    }
    
    /**
     * Gets the received mark for the previous question.
     * @return The received mark for previous question.
     */
    public int getMark() {
        return mark;
    }
    
    /**
     * Gets the max available mark for the previous question.
     * @return The max available mark for previous question.
     */
    public int getOutOf() {
        return outOf;
    }
    
    /**
     * Gets the feedback based on answer for the previous question.
     * @return The feedback for previous question.
     */
    public String getFeedback() {
        return feedback;
    }
    
    /**
     * Gets the final grade for the whole assignment.
     * @return The final grade.
     */
    public double getFinalGrade() {
        return finalGrade;
    }
    
    /**
     * Gets schema image for the assignment.
     * @return The bytes representing the schema image.
     */
    public byte[] getSchemaImg() {
        return schemaImg;
    }
    
    
}

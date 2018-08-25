package kiwi.kiwiserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Stores information relevant to a question and processes marking and
 * provision of feedback.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Question {
    
    
    //Constants:
    
    /**
     * Base range of marks.
     * 0: didn't compile
     * 1: compiled but incorrect
     * 2: compiled and correct
     */
    public static final int [] MARK_RANGE = {0,1,2};
    
    
    //Instance Variables:
    
    /**
     * English question.
     */
    private String question;
    
    /**
     * SQL statement expected answer.
     */
    private String answer;
    
    /**
     * Difficulty value.
     * Can be 1, 2 or 3.
     */
    private int difficulty;
    
    private String type;
    
    /**
     * Mark student receives after question is marked.
     * Can be 0, difficulty or difficulty*2.
     */
    int mark;
    
    /**
     * Result set from expected sql statement output.
     */
    ResultSet rsExpected;
    
    /**
     * Number of columns in expected sql statement output.
     */
    int expectedColCount;
    
    /**
     * Result set from student's sql statement output.
     */
    ResultSet rsStudent;
    
    /**
     * Number of columns in student's sql statement output.
     */
    int studentColCount;
    
    /**
     * Error message received on student statement execution - if any.
     */
    String errorMessage;
    
    private Connection conn;
    private Connection connLimited;
    
    //Constructor:
    
    /**
     * Creates question object.
     * @param question English question.
     * @param answer SQL statement expected answer.
     * @param difficulty Difficulty value (1, 2 or 3)
     */
    public Question(String question, String answer, int difficulty, String type, Connection conn, Connection connLimited) {
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
        this.type = type;
        this.mark = 0;
        this.conn = conn;
        this.connLimited = connLimited;
    }
    
    /**
     * Default constructor to create a question.
     */
    public Question(Connection conn, Connection connLimited) {
        mark= 0;
        this.conn = conn;
        this.connLimited = connLimited;
    }
    
    
    //Getters:
    
    /**
     * Gets English question.
     * @return English question
     */
    public String getQuestion() {
        return question;
    }
    
    /**
     * Get SQL statement answer.
     * @return SQL statement answer.
     */
    public String getAnswer() {
        return answer;
    }
    
    /**
     * Gets question difficulty.
     * @return Question difficulty.
     */
    public int getDifficulty() {
        return difficulty;
    }
    
    
    //Main functionality methods:
    
    //TODO: check student not trying data manipulation (ie.not select command)
    /**
     * Marks the given student answer. The student answer statement and
     * expected answer statement are run and the outputs compared. Then a mark
     * is returned.
     * @param studentAns SQL statement that student submitted as answer.
     * @return Mark received by student for the question or -1 for lecturer
     * answer err or -2 for db conn/processing err
     */
    public int mark(String studentAns) {
        
        try {
           //Get expected output:
           Statement stExpected = conn.createStatement();
           try {
                rsExpected = stExpected.executeQuery(answer);   //execute expected sql statement
           }
           catch (SQLException e) { //didn't compile
               System.out.println("Error: answer from lecturer wrong!");
               System.out.println(e);
               return -1;
           }
           expectedColCount = rsExpected.getMetaData().getColumnCount();
           
           //Get student output:
           Statement stStudent = connLimited.createStatement();
           try {
                rsStudent = stStudent.executeQuery(studentAns); //execute student's sql statement
           }
           catch (SQLException e) { //didn't compile
               mark = MARK_RANGE[0];
               
               //Process error message:
               errorMessage = e.toString().substring("java.sql.".length()); //remove "java.sql." from error name
               if (errorMessage.contains("kiwidb.")) {
                   errorMessage= errorMessage.replace("kiwidb.","");    //remove "kiwidb." from table name
               }
               
               return mark;
           }
           studentColCount= rsStudent.getMetaData().getColumnCount();
           
           //Wrong if not same no. columns:
           if (studentColCount!= expectedColCount) {
               mark = MARK_RANGE[1];
               return mark;
           }
           
           //For same no. columns:
           else {
               //Wrong if different column sql types:
                for (int i=1; i<=expectedColCount; i++) {    //all expected columns
                    if (rsStudent.getMetaData().getColumnType(i)!= rsExpected.getMetaData().getColumnType(i)){  //not same type
                        mark = MARK_RANGE[1];
                        return mark;
                    }
                }
                
                //Compare rows of expected and received output:
                while(rsExpected.next()) {  //all expected rows
                    if (rsStudent.next()) { //there is another student row
                        
                        //Compare each column value in rows:
                        for (int i=1; i<=expectedColCount; i++) {   //fields in given expected row
                            if (rsStudent.getObject(i)==null || rsExpected.getObject(i)==null) {    //at least one field null
                                if (rsStudent.getObject(i)!=null || rsExpected.getObject(i)!=null) {    //not both null
                                    mark = MARK_RANGE[1];
                                    return mark;
                                }
                            }
                            else if (!rsStudent.getObject(i).equals(rsExpected.getObject(i))) {   //fields not equal
                                mark = MARK_RANGE[1];
                                return mark;
                            }
                        }
                    }
                    else {   //student output has too few rows
                        mark = MARK_RANGE[1];
                        return mark;
                    }
                }
                if (rsStudent.next()) {  //student output too many rows
                    mark = MARK_RANGE[1];
                    return mark;
                }
                
                //Output is exactly the same:
                mark = MARK_RANGE[2]*difficulty;
                return mark;
            }
        }
        catch (SQLException e) {
            System.out.println("Error: Problem comparing sql result set outputs.");
            System.out.println(e);
            return -2;
        }
    }
    
    //TODO: Show what the differences in output are?
    /**
     * Creates feedback, once student submits answer, which shows what the
     * expected and received sql statements and output are.
     * @param studentAns The statement received from student as answer.
     * @return Formatted String showing expected and received sql statements
     * and output or null if db conneection/processing error occurs
     */
    public String getFeedback(String studentAns) {
        
        //Check that lecturer sql statement executed:
        if (rsExpected==null) {
            return "Error: Couldn't run lecturer's sql statement. Please contact your lecturer about this.";
        }
        
        //Show expected and received answer statements:
        String toReturn= "Expected SQL: " + answer + "\n"
            + "Your SQL: " + studentAns + "\n"
            + "\n";
        
        //Show expected output:
        toReturn+= "Expected Output:\n";
        try {
            rsExpected.beforeFirst();   //move cursor to start of result set
            
            //Get column names:
            for (int i=1; i<=expectedColCount; i++) {   
                    toReturn+= rsExpected.getMetaData().getColumnName(i) + "\t";
                }
            toReturn+="\n";
            
            //Get row entries:
            while(rsExpected.next()) {  //each row 
                for (int i=1; i<=expectedColCount; i++) {   //each field in row
                    toReturn+= rsExpected.getObject(i) + "\t";
                }
                toReturn+="\n";
            }
            toReturn+="\n"; //end of expected output
        } 
        catch (SQLException e) {
            System.out.println("Error: Couldn't read student output.");
            System.out.println(e);
            return null;
        }
        
        //Show student output:
        toReturn+= "Your Output:\n";
        
        if (mark==Question.MARK_RANGE[0]) {  //answer statement didn't compile
            toReturn+= "SQL Statement did not compile.\n";
            toReturn+= errorMessage+"\n";
        }
        else {  //answer statement did compile
            try {
                rsStudent.beforeFirst();    //move cursor to start of result set
                
                //Get column names:
                for (int i=1; i<=studentColCount; i++) {   
                        toReturn+= rsStudent.getMetaData().getColumnName(i) + "\t";
                    }
                toReturn+="\n";
                
                //Get row entries:
                while(rsStudent.next()) {  //each row
                    for (int i=1; i<=studentColCount; i++) {    //each field in row
                        toReturn+= rsStudent.getObject(i) + "\t";
                    }
                    toReturn+="\n";
                }
                toReturn+="\n"; //end of student's output
            } 
            catch (SQLException e) {
                System.out.println("Error: Couldn't read student output.");
                System.out.println(e);
                return null;
            }
        }
        return toReturn;    //all feedback
    }
    
    public int getMark() {
        return mark;
    }
   
    public int getMaxMark() {
        return MARK_RANGE[2]*difficulty;
    }

    public int getOutOf() {
        return difficulty*MARK_RANGE[2];
    }
    
}

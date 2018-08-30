package kiwi.kiwiserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

/**
 * Stores information relevant to a question and also processes marking and
 * provision of feedback.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 */
public class Question {
    
    //Constants:
    
    //Question Type Constants:
    /**
     * Type of question involving select statements.
     */
    public static final String TYPE_SELECT = "Select";
    
    /**
     * Type of question involving arithmetic such as count() or avg() etc.
     */
    public static final String TYPE_ARITHMETIC = "Arithmetic";
    
    /**
     * Type of question involving update statements(update, insert, delete).
     */
    public static final String TYPE_UPDATE = "Update";
    
    
    //Difficulty Constants:
    /**
     * Easy question difficulty.
     */
    public static final int DIFF_EASY = 1;
    
    /**
     * Medium question difficulty.
     */
    public static final int DIFF_MEDIUM = 2;
    
    /**
     * Hardest question difficulty.
     */
    public static final int DIFF_HARD = 3;
    
    
    //Mark Base Constants:
    /**
     * Mark when statement doesn't compile or isn't permitted(wrong command).
     */
    public static final int MARK_BASE_ERR = 0;
    
    /**
     * Mark when statement compiles but is incorrect.
     */
    public static final int MARK_BASE_COMPILED = 1;
    
    /**
     * Mark when statement compiles and is correct.
     */
    public static final int MARK_BASE_CORRECT = 2;
    
    /**
     * Mark when lecturers provided answer doesn't compile.
     */
    public static final int MARK_BASE_LECTURER_ERR = -1;
    
    /**
     * Mark when question cannot be marked due to connection error.
     */
    public static final int MARK_BASE_CONN_ERR = -2;
    
    
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
     * Difficulty value, which can be DIFF_EASY, DIFF_MEDIUM or DIFF_HARD.
     */
    private int difficulty;
    
    /**
     * Question type, which can be TYPE_SELECT, TYPE_UPDATE or TYPE_ARITHMETIC.
     */
    private String type;
    
    /**
     * The question number/question ID of this question in the database.
     */
    private int questionDBNo;
    
    /**
     * Mark student receives after question is marked.
     * Can be MARK_ERR, MARK_COMPILE or MARK_CORRECT*difficulty.
     */
    private int mark;
    
    //Variables for marking:
    /**
     * Result set from expected sql statement output.
     */
    private ResultSet rsExpected;
    
    /**
     * Rows affected from expected sql statement update.
     */
    private int raExpected;
    
    /**
     * Number of columns in expected sql statement output.
     */
    private int expectedColCount;
    
    /**
     * Result set from student's sql statement output.
     */
    private ResultSet rsStudent;
    
    /**
     * Rows affected from student's sql statement update.
     */
    private int raStudent;
    
    /**
     * Number of columns in student's sql statement output.
     */
    private int studentColCount;
    
    /**
     * Error message received on student statement execution - if any.
     */
    private String errorMessage;
    
    //Database Connection Variables:
    /**
     * Database connection, not allowed access to student and question info
     * tables.
     */
    private final Connection connLimited;
    
    /**
     * Database connection, allowed access to student and question info tables.
     */
    private Connection conn;
    
    
    //Constructors:
    /**
     * Creates question object.
     * @param question English question.
     * @param answer SQL statement expected answer.
     * @param difficulty Difficulty value, which can be DIFF_EASY, DIFF_MEDIUM or DIFF_HARD.
     * @param type Question type, which can be TYPE_SELECT, TYPE_UPDATE or TYPE_ARITHMETIC.
     * @param questionDBNo Question number/ID in the system's database.
     * @param connLimited Database connection, not allowed access to system's tables.
     * @param conn Database connection, allowed access to system's tables.
     */
    public Question(String question, String answer, int difficulty, String type, int questionDBNo, Connection connLimited, Connection conn){
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
        this.type = type;
        this.mark = 0;
        this.connLimited = connLimited;
        this.conn = conn;
        this.questionDBNo = questionDBNo;
    }
    
    /**
     * Default constructor to create a question.
     * @param connLimited Database connection, not allowed access to system's tables.
     */
    public Question(Connection connLimited) {
        mark= 0;
        this.connLimited = connLimited;
    }
    
    
    //Main functionality methods:
    
    /**
     * Marks the given student answer.
     * @param studentAns SQL statement that student submitted as answer.
     * @return Mark received by student for the question or -1 for lecturer
     * answer err or -2 for db conn/processing err
     * @author Tala Ross(rsstal002)
     * @author Nikai Jagganath (jggnik001)
     */
    public int mark(String studentAns) {
        if (type.toLowerCase().equals(TYPE_UPDATE.toLowerCase())) {  //update question
            return markUpdateQuestion(studentAns);
        }
        else { //query question
            return markQueryQuestion(studentAns);
        }
    }
    
    /**
     * Mark the query question by comparing the column names and types of the
     * result sets received after each statement execution and then comparing
     * each entry in the result sets for the expected and student statements.
     * @param studentAns The given answer SQL statement.
     * @return The mark received.
     * @author Tala Ross(RSSTAL002)
     * @author Nikai Jagganath(JGGNIK001)
     */
    private int markQueryQuestion(String studentAns) {
        try {
            //Get expected output:
            //Check expected query statement command:
            if (!answer.toUpperCase().startsWith("SELECT")) {//lecturer answer does not execute as expected
                //Mark question as Not Permitted:
                String reportStatement = "UPDATE questions "
                        + "SET Problem='Not Permitted' "
                        + "WHERE QuestionNo=" + questionDBNo + ";";
                
                Statement st = conn.createStatement();
                conn.setAutoCommit(false);  //start transaction
                Savepoint sp = conn.setSavepoint();
                try {
                    st.executeUpdate(reportStatement);
                }
                catch (SQLException ex) {
                    conn.rollback(sp);
                    conn.setAutoCommit(true);   //end transaction
                    System.out.println("Error: Problem adding not permitted marker.");
                    System.out.println(ex); 
                }
                conn.commit();
                conn.setAutoCommit(true);
                
                System.out.println("Error: answer from lecturer is not executing a permitted SQL DML statement!");
                mark = MARK_BASE_LECTURER_ERR;
                return mark;
            }
            
            Statement stExpected = connLimited.createStatement();
            try {
                rsExpected = stExpected.executeQuery(answer);   //execute expected sql statement
            }
            catch (SQLException e) { //lecturer statement didn't compile
                //Mark question as Not Compile:
                String reportStatement = "UPDATE questions "
                        + "SET Problem='Not Compile' "
                        + "WHERE QuestionNo=" + questionDBNo + ";";
                
                Statement st = conn.createStatement();
                conn.setAutoCommit(false);  //start transaction
                Savepoint sp = conn.setSavepoint();
                try {
                    st.executeUpdate(reportStatement);
                }
                catch (SQLException ex) {
                    conn.rollback(sp);
                    conn.setAutoCommit(true);   //end transaction
                    System.out.println("Error: Problem adding not compile marker.");
                    System.out.println(ex); 
                }
                conn.commit();
                conn.setAutoCommit(true);
               
                System.out.println("Error: answer from lecturer wrong!");
                System.out.println(e);
                mark = MARK_BASE_LECTURER_ERR;
                return mark;
            }
            
            expectedColCount = rsExpected.getMetaData().getColumnCount();
           
            //Get student output:
            //Check expected query statement command:
            if (!studentAns.toUpperCase().startsWith("SELECT")) {   //student answer does not execute as expected
                errorMessage = "Statement provided is not executing a permitted SQL query statement!\n"
                        + "i.e. SELECT";
                mark = MARK_BASE_ERR;
                return mark;
            }
            
            Statement stStudent = connLimited.createStatement();
            try {
                rsStudent = stStudent.executeQuery(studentAns); //execute student's sql statement
            }
            catch (SQLException e) { //didn't compile
                mark = MARK_BASE_ERR;
               
                //Process error message:
                errorMessage = "SQL Statement did not compile.\n"
                        + e.toString().substring("java.sql.".length()).replace("kiwidb.",""); //remove "java.sql." from error name and "kiwidb." from table name
               
                return mark;
            }
            studentColCount= rsStudent.getMetaData().getColumnCount();
           
            //Wrong if not same no. columns:
            if (studentColCount!= expectedColCount) {
                mark = MARK_BASE_COMPILED;
                return mark;
            }
           
            //For same no. columns:
            else {
                //Wrong if different column sql types:
                for (int i=1; i<=expectedColCount; i++) {    //all expected columns
                    if (rsStudent.getMetaData().getColumnType(i)!= rsExpected.getMetaData().getColumnType(i)){  //not same type
                        mark = MARK_BASE_COMPILED;
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
                                    mark = MARK_BASE_COMPILED;
                                    return mark;
                                }
                            }
                            else if (!rsStudent.getObject(i).equals(rsExpected.getObject(i))) {   //fields not equal
                                mark = MARK_BASE_COMPILED;
                                return mark;
                            }
                        }
                    }
                    else {   //student output has too few rows
                        mark = MARK_BASE_COMPILED;
                        return mark;
                    }
                }
                if (rsStudent.next()) {  //student output too many rows
                    mark = MARK_BASE_COMPILED;
                    return mark;
                }
                
                //Output is exactly the same:
                mark = MARK_BASE_CORRECT*difficulty;
                return mark;
            }
        }
        catch (SQLException e) {
            System.out.println("Error: Problem comparing sql result set outputs.");
            System.out.println(e);
            mark = MARK_BASE_CONN_ERR;
            return mark;
        }
    }
    
    /**
     * Mark the update question by comparing the rows affected for each update
     * and then target tables after the expected and given update statements.
     * @param studentAns The given answer SQL statement.
     * @return The mark received.
     * @author Tala Ross(RSSTAL002)
     */
    private int markUpdateQuestion(String studentAns) {
        try {
            //Get expected output:
            Statement stExpected = connLimited.createStatement();
           
            //Get table being updated in expected sql update:
            String tblNameExp = "";
            if (answer.toUpperCase().startsWith("INSERT")) {
                if (answer.split(" ").length>2) {   //will catch error later
                    tblNameExp = answer.split(" ")[2];  //insert into <table>
                }
            }
            else if (answer.toUpperCase().startsWith("UPDATE")) {
                if (answer.split(" ").length>1) {
                    tblNameExp = answer.split(" ")[1];  //update <table>
                }
            }
            else if (answer.toUpperCase().startsWith("DELETE")) {
                if (answer.split(" ").length>2) {
                    tblNameExp = answer.split(" ")[2];  //delete from <table>
                }
            }
            else {   //lecturer answer does not execute as expected
                //Mark question as Not Permitted:
                String reportStatement = "UPDATE questions "
                        + "SET Problem='Not Permitted' "
                        + "WHERE QuestionNo=" + questionDBNo + ";";
                
                Statement st = conn.createStatement();
                conn.setAutoCommit(false);  //start transaction
                Savepoint sp = conn.setSavepoint();
                try {
                    st.executeUpdate(reportStatement);
                }
                catch (SQLException ex) {
                    conn.rollback(sp);
                    conn.setAutoCommit(true);   //end transaction
                    System.out.println("Error: Problem adding not permitted marker.");
                    System.out.println(ex); 
                }
                conn.commit();
                conn.setAutoCommit(true);
                
                System.out.println("Error: answer from lecturer is not executing a permitted SQL DML statement!");
                mark = MARK_BASE_LECTURER_ERR;
                return mark;
            }
           
            //Start transaction:
            connLimited.setAutoCommit(false);
            Savepoint spExp = connLimited.setSavepoint();

            //Expected update:
            try {
                 raExpected = stExpected.executeUpdate(answer);   //execute expected sql update and get rows affeted
            }
            catch (SQLException e) { //lecturer statement didn't compile
                //Mark question as Not Compile:
                String reportStatement = "UPDATE questions "
                        + "SET Problem='Not Compile' "
                        + "WHERE QuestionNo=" + questionDBNo + ";";
                
                Statement st = conn.createStatement();
                conn.setAutoCommit(false);  //start transaction
                Savepoint sp = conn.setSavepoint();
                try {
                    st.executeUpdate(reportStatement);
                }
                catch (SQLException ex) {
                    conn.rollback(sp);
                    conn.setAutoCommit(true);   //end transaction
                    System.out.println("Error: Problem adding not compile marker.");
                    System.out.println(ex); 
                }
                conn.commit();
                conn.setAutoCommit(true);
               
                System.out.println("Error: answer from lecturer wrong!");
                System.out.println(e);
                mark = MARK_BASE_LECTURER_ERR;
                return mark;
            }
           
            //Expected new table:
            try {
                 rsExpected = stExpected.executeQuery("SELECT * FROM " + tblNameExp + ";");
            }
            catch (SQLException e) { //didn't compile
                connLimited.rollback(spExp);
                connLimited.setAutoCommit(true);
                System.out.println("Error: couldn't get lecturer output!");
                System.out.println(e);
                mark = MARK_BASE_CONN_ERR;
                return mark;
            }
           
            //End transaction:
            connLimited.rollback(spExp);
            connLimited.setAutoCommit(true);

            expectedColCount = rsExpected.getMetaData().getColumnCount();

            /////////////////////////

            //Get student output:
            Statement stStudent = connLimited.createStatement();

            //Get table being updated in student sql update:
            String tblNameStu = "";
            if (studentAns.toUpperCase().startsWith("INSERT")) {
                if (studentAns.split(" ").length>2) {   //will catch error later
                    tblNameStu = studentAns.split(" ")[2];  //insert into <table>
                }
            }
            else if (studentAns.toUpperCase().startsWith("UPDATE")) {
                if (studentAns.split(" ").length>1) {
                    tblNameStu = studentAns.split(" ")[1];  //update <table>
                }
            }
            else if (studentAns.toUpperCase().startsWith("DELETE")) {
                if (studentAns.split(" ").length>2) {
                    tblNameStu = studentAns.split(" ")[2];  //delete from <table>
                }
            }
            else {   //lecturer answer does not execute as expected
                errorMessage = "Statement provided is not executing a permitted SQL DML statement!\n"
                        + "i.e. UPDATE, INSERT or DELETE";
                mark = MARK_BASE_ERR;
                return mark;
            }

            //Start transaction:
            connLimited.setAutoCommit(false);
            Savepoint spStu = connLimited.setSavepoint();

            //Expected update:
            try {
                 raStudent = stStudent.executeUpdate(studentAns); //execute student's sql statement
            }
            catch (SQLException e) { //didn't compile
                 connLimited.rollback(spStu);
                 connLimited.setAutoCommit(true);

                 //Process error message:
                 errorMessage = "SQL Statement did not compile.\n"
                        + e.toString().substring("java.sql.".length()).replace("kiwidb.",""); //remove "java.sql." from error name and "kiwidb." from table name

                 mark = MARK_BASE_ERR;
                 return mark;
            }

            //Expected new table:
            try {
                rsStudent = stStudent.executeQuery("SELECT * FROM " + tblNameStu + ";");
            }
            catch (SQLException e) {
                connLimited.rollback(spStu);
                connLimited.setAutoCommit(true);
                System.out.println("Error: couldn't get student output!");
                System.out.println(e);
                mark = -2;
                return mark;
            }

            //End transaction:
            connLimited.rollback(spStu);
            connLimited.setAutoCommit(true);

            studentColCount= rsStudent.getMetaData().getColumnCount();

            /////////////////////////

            //Wrong if didn't update same table:
            if (!tblNameExp.toLowerCase().equals(tblNameStu.toLowerCase())) { 
                mark = MARK_BASE_COMPILED;
                return mark;
            }

            //For same tables:
            else {
                //Compare rows of expected and received output:
                while(rsExpected.next()) {  //all expected rows
                    if (rsStudent.next()) { //there is another student row
                        
                        //Compare each column value in rows:
                        for (int i=1; i<=expectedColCount; i++) {   //fields in given expected row
                            if (rsStudent.getObject(i)==null || rsExpected.getObject(i)==null) {    //at least one field null
                                if (rsStudent.getObject(i)!=null || rsExpected.getObject(i)!=null) {    //not both null
                                    mark = MARK_BASE_COMPILED;
                                    return mark;
                                }
                            }
                            else if (!rsStudent.getObject(i).equals(rsExpected.getObject(i))) {   //fields not equal
                                mark = MARK_BASE_COMPILED;
                                return mark;
                            }
                        }
                    }
                    else {   //student output has too few rows
                        mark = MARK_BASE_COMPILED;
                        return mark;
                    }
                }
                if (rsStudent.next()) {  //student output too many rows
                    mark = MARK_BASE_COMPILED;
                    return mark;
                }
                
                //Output is exactly the same:
                mark = MARK_BASE_CORRECT*difficulty;
                return mark;
            }
        }
        catch (SQLException e) {
            System.out.println("Error: Problem comparing sql result set outputs.");
            System.out.println(e);
            mark = MARK_BASE_CONN_ERR;
            return mark;
        }
    }
    
    
    //Getters:
    
    /**
     * Gets the feedback for the marked question.
     * @return Formatted String showing expected and received output or null if
     * DB connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    public String getFeedback() {
        //Check that lecturer sql statement executed:
        if (rsExpected==null) {
            return "Error: Couldn't run lecturer's sql statement. Please contact your lecturer about this.";
        }
        
        //Get feedback:
        if (!type.equals(TYPE_UPDATE)) { //query question
            return getQueryFeedback(); 
        }
        else {  //update question
            return getUpdateFeedback();
        }
        
    }
    
    /**
     * Gets the feedback for a query question.
     * @return Formatted String showing expected and received output or null if
     * DB connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private String getQueryFeedback() {
        //Show expected output:
        String toReturn = "Expected Output:\n";
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
            System.out.println("Error: Couldn't read lecturer output.");
            System.out.println(e);
            return null;
        }
        
        //Show student output:
        toReturn+= "Your Output:\n";
        
        if (mark==MARK_BASE_ERR) {  //answer statement didn't compile
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
    
    /**
     * Gets the feedback for an update question.
     * @return Formatted String showing expected and received output after
     * update and the number of affected rows or null if DB
     * connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private String getUpdateFeedback() {
        //Show expected output:
        String toReturn = "Expected table contents after update:\n";
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
            System.out.println("Error: Couldn't read lecturer output.");
            System.out.println(e);
            return null;
        }
        
        //Show student output:
        toReturn+= "Your table contents after update:\n";
        
        if (mark==MARK_BASE_ERR) {  //answer statement didn't compile
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
     * @return Question difficulty, which can be DIFF_EASY, DIFF_MEDIUM or DIFF_HARD.
     */
    public int getDifficulty() {
        return difficulty;
    }
    
    /**
     * Gets the received mark for the question.
     * @return The received mark for the question.
     */
    public int getMark() {
        return mark;
    }
   
    /**
     * Gets the max mark available for the question.
     * @return The max mark available for the question.
     */
    public int getMaxMark() {
        return MARK_BASE_CORRECT*difficulty;
    }
    
    /**
     * Gets the questions ID/number in the system's database.
     * @return The questions ID/number in the system's database.
     */
    public int getQuestionDBNo() {
        return questionDBNo;
    }
    
    
}

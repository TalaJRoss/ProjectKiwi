package kiwi.kiwiserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import kiwi.message.StatementOutput;

/**
 * Stores information relevant to a question and also processes marking and
 * provision of feedback.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 */
public class Question {
    
    //Constants:
    
    /**
     * Number of types of questions.
     */
    public static final int NO_TYPES = 3;
    
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
     * Number of difficulty levels associated with questions.
     */
    public static final int NO_DIFFICULTY_LEVELS = 3;
    
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
    
    
    //Feedback Constants:
    /**
     * The percentage of the correct statement shown as feedback when student
     * answer doesn't compile for a DIFF_EASY question.
     */
    private static final double HELP_FACTOR_EASY = 0.7;
    
    /**
     * The percentage of the correct statement shown as feedback when student
     * answer doesn't compile for a DIFF_MEDIUM question.
     */
    private static final double HELP_FACTOR_MEDIUM = 0.4;
    
    /**
     * The percentage of the correct statement shown as feedback when student
     * answer doesn't compile for a DIFF_HARD question.
     */
    private static final double HELP_FACTOR_HARD = 0.0;
    
    /**
     * Indicates that student question received a MARK_BASE_ERR because
     * the SQL statement was using a command which is not permitted. 
     */
    private static final String STUDENT_ERR_PERMIT = "Not Permitted";
    
    /**
     * Indicates that student question received a MARK_BASE_ERR because
     * the SQL statement provided did not compile. 
     */
    private static final String STUDENT_ERR_COMPILE = "Couldn't Compile";
    
    
    //Instance Variables:
    
    //Main information:
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
     * The name of the table being updated by the answer update statement.
     */
    private String tblNameExpected;
    
    /**
     * The row of the expected table where the first difference occurred.
     */
    private String diffRowExpected;
    
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
     * The name of the table being updated by a student update statement.
     */
    private String tblNameStudent;
    
    /**
     * The row of the student table where the first difference occurred.
     */
    private String diffRowStudent;
    
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
     * @return Mark received by student for the question or MARK_BASE_LECTURER_ERR for lecturer
     * answer err or MARK_BASE_CONN_ERR for db conn/processing err
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
                    mark = MARK_BASE_LECTURER_ERR;
                    return mark;
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
                    mark = MARK_BASE_LECTURER_ERR;
                    return mark;
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
                errorMessage = STUDENT_ERR_PERMIT;
                mark = MARK_BASE_ERR;
                return mark;
            }
            
            Statement stStudent = connLimited.createStatement();
            try {
                rsStudent = stStudent.executeQuery(studentAns); //execute student's sql statement
            }
            catch (SQLException e) { //didn't compile
                errorMessage = STUDENT_ERR_COMPILE;
                mark = MARK_BASE_ERR;
                return mark;
            }
            studentColCount= rsStudent.getMetaData().getColumnCount();
           
            //Mark the question:
            
            //Wrong if not same no. columns:
            if (studentColCount!= expectedColCount) {
                //Get column labels for expected output:
                String expRow = "";
                for (int i=1; i<=expectedColCount; i++) {   //fields in given expected row
                        expRow+= rsExpected.getMetaData().getColumnLabel(i) + "\t";
                    }
                diffRowExpected = expRow.substring(0, expRow.length() - 1);

                //Get column labels from student output:
                String stuRow = "";
                for (int i=1; i<=studentColCount; i++) {   //fields in given expected row
                        stuRow+= rsStudent.getMetaData().getColumnLabel(i) + "\t";
                    }
                diffRowStudent = stuRow.substring(0, stuRow.length() - 2);
                
                mark = MARK_BASE_COMPILED;
                return mark;
            }
           
            //For same no. columns:
            else {
                //Wrong if different column sql types:
                String expLabels = "";
                String stuLabels = "";
                for (int i=1; i<=expectedColCount; i++) {    //all expected columns
                    expLabels+= rsExpected.getMetaData().getColumnLabel(i) + "\t";
                    stuLabels+= rsStudent.getMetaData().getColumnLabel(i) + "\t";
                    
                    if (rsStudent.getMetaData().getColumnType(i)!= rsExpected.getMetaData().getColumnType(i)){  //not same type
                        diffRowExpected = expLabels.substring(0, expLabels.length() - 1);
                        diffRowStudent = stuLabels.substring(0, stuLabels.length() - 1);
                        
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
                                    
                                    //Get first row from expected output:
                                    String expRow = "";
                                    for (int j=1; j<=expectedColCount; j++) {   //fields in given expected row
                                        expRow+= rsExpected.getObject(j).toString() + "\t";
                                    }
                                    diffRowExpected = expRow.substring(0, expRow.length() - 1);

                                    //Get first row from expected output:
                                    String stuRow = "";
                                    for (int j=1; j<=studentColCount; j++) {   //fields in given student row
                                        stuRow+= rsStudent.getObject(j).toString() + "\t";
                                    }
                                    diffRowStudent = stuRow.substring(0, stuRow.length() - 1);
                                    
                                    mark = MARK_BASE_COMPILED;
                                    return mark;
                                }
                            }
                            else if (!rsStudent.getObject(i).equals(rsExpected.getObject(i))) {   //fields not equal
                                
                                //Get first row from expected output:
                                String expRow = "";
                                for (int j=1; j<=expectedColCount; j++) {   //fields in given expected row
                                    expRow+= rsExpected.getObject(j).toString() + "\t";
                                }
                                diffRowExpected = expRow.substring(0, expRow.length() - 1);

                                //Get first row from expected output:
                                String stuRow = "";
                                for (int j=1; j<=studentColCount; j++) {   //fields in given student row
                                    stuRow+= rsStudent.getObject(j).toString() + "\t";
                                }
                                diffRowStudent = stuRow.substring(0, stuRow.length() - 1);
                                
                                mark = MARK_BASE_COMPILED;
                                return mark;
                            }
                        }
                    }
                    else {   //student output has too few rows
                        
                        //Get first row from expected output:
                        String expRow = "";
                        for (int i=1; i<=expectedColCount; i++) {   //fields in given expected row
                            expRow+= rsExpected.getObject(i).toString() + "\t";
                        }
                        diffRowExpected = expRow.substring(0, expRow.length() - 1);

                        //Get first row from expected output: doesn't exist
                        diffRowStudent = "";
                        
                        mark = MARK_BASE_COMPILED;
                        return mark;
                    }
                }
                if (rsStudent.next()) {  //student output too many rows
                    
                    //Get first row from expected output: doesn't exist
                    diffRowExpected = "";

                    //Get first row from expected output:
                    String stuRow = "";
                    for (int j=1; j<=studentColCount; j++) {   //fields in given student row
                        stuRow+= rsStudent.getObject(j).toString() + "\t";
                    }
                    diffRowStudent = stuRow.substring(0, stuRow.length() - 1);
                    
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
            if (answer.toUpperCase().startsWith("INSERT")) {
                if (answer.split(" ").length>2) {   //will catch error later
                    tblNameExpected = answer.split(" ")[2];  //insert into <table>
                }
            }
            else if (answer.toUpperCase().startsWith("UPDATE")) {
                if (answer.split(" ").length>1) {
                    tblNameExpected = answer.split(" ")[1];  //update <table>
                }
            }
            else if (answer.toUpperCase().startsWith("DELETE")) {
                if (answer.split(" ").length>2) {
                    tblNameExpected = answer.split(" ")[2];  //delete from <table>
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
                    tblNameExpected = null;
                    mark = MARK_BASE_LECTURER_ERR;
                    return mark;
                }
                conn.commit();
                conn.setAutoCommit(true);   //end transaction
                
                //Set appropriate mark:
                System.out.println("Error: answer from lecturer is not executing a permitted SQL DML statement!");
                tblNameExpected = null;
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
                    mark = MARK_BASE_LECTURER_ERR;
                    return mark;
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
                 rsExpected = stExpected.executeQuery("SELECT * FROM " + tblNameExpected + ";");
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

            ///////////////////////// End of lecturer update and get new table

            //Get student output:
            Statement stStudent = connLimited.createStatement();

            //Get table being updated in student sql update:
            if (studentAns.toUpperCase().startsWith("INSERT")) {
                if (studentAns.split(" ").length>2) {   //will catch error later
                    tblNameStudent = studentAns.split(" ")[2];  //insert into <table>
                }
            }
            else if (studentAns.toUpperCase().startsWith("UPDATE")) {
                if (studentAns.split(" ").length>1) {
                    tblNameStudent = studentAns.split(" ")[1];  //update <table>
                }
            }
            else if (studentAns.toUpperCase().startsWith("DELETE")) {
                if (studentAns.split(" ").length>2) {
                    tblNameStudent = studentAns.split(" ")[2];  //delete from <table>
                }
            }
            else {   //student answer does not execute as expected
                errorMessage = STUDENT_ERR_PERMIT;
                mark = MARK_BASE_ERR;
                return mark;
            }

            //Start transaction:
            connLimited.setAutoCommit(false);
            Savepoint spStu = connLimited.setSavepoint();

            //Student update:
            try {
                 raStudent = stStudent.executeUpdate(studentAns); //execute student's sql statement
            }
            catch (SQLException e) { //didn't compile
                 connLimited.rollback(spStu);
                 connLimited.setAutoCommit(true);
                 errorMessage = STUDENT_ERR_COMPILE;
                 mark = MARK_BASE_ERR;
                 return mark;
            }

            //Student's new table:
            try {
                rsStudent = stStudent.executeQuery("SELECT * FROM " + tblNameStudent + ";");
            }
            catch (SQLException e) {
                connLimited.rollback(spStu);
                connLimited.setAutoCommit(true);
                System.out.println("Error: couldn't get student output!");
                System.out.println(e);
                mark = MARK_BASE_CONN_ERR;
                return mark;
            }

            //End transaction:
            connLimited.rollback(spStu);
            connLimited.setAutoCommit(true);

            studentColCount= rsStudent.getMetaData().getColumnCount();

            ///////////////////////// End of student update and get new table
            
            //Mark the question:
            
            //Wrong if didn't update same table:
            if (!tblNameExpected.toLowerCase().equals(tblNameStudent.toLowerCase())) { 
                mark = MARK_BASE_COMPILED;
                return mark;
            }
            
            //Wrong if didn't affect the same number of rows:
            if (raExpected!=raStudent) { 
                mark = MARK_BASE_COMPILED;
                return mark;
            }

            //For same tables and number of rows affected check that changes were the same:
            else {
                //Compare rows of expected and received output:
                while(rsExpected.next()&&rsStudent.next()) {  //all expected rows and student rows (same number of rows)
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
                
                //Output is exactly the same:
                mark = MARK_BASE_CORRECT*difficulty;
                return mark;
            }
        }
        catch (SQLException e) {    //Problem comparing sql ResultSets
            System.out.println("Error: Problem comparing sql result set outputs.");
            System.out.println(e);
            mark = MARK_BASE_CONN_ERR;
            return mark;
        }
    }
    
    
    //Getters:
    
    /**
     * Gets the feedback for the marked question and gives help if full marks
     * weren't received, suggesting differences, and supplies a partial correct
     * statement if student answer didn't compile.
     * @return Formatted String giving a message and showing expected and
     * received output if answer wasn't correct, a statement suggestion if
     * answer didn't compile or null if DB connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    public StatementOutput getFeedback() {
        try {
            if (rsExpected==null) { //check that lecturer sql statement executed
                return new StatementOutput("Error: Couldn't run lecturer's sql statement.");
            }
            else if (mark==getMaxMark()) {   //got full marks so return basic message
                return new StatementOutput(getFeedbackMessage());
            }
            else if (mark==MARK_BASE_ERR) { //answer didn't compile so show suggested partial statement
                return new StatementOutput(getAdditionalHelp());
            }
            else {  //didn't get full marks so return help(compiled but incorrect)
                if (type.toLowerCase().equals(TYPE_UPDATE.toLowerCase())) { //update question
                    return getUpdateFeedback();
                }
                else {  //query question
                    return getQueryFeedback(); 
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Gets the feedback for a query question, with contents dependent on
     * this question difficulty.
     * That is, if the question is DIFF_EASY then the expected and received
     * output are both shown.
     * If the question is DIFF_MEDIUM then the differences in a single row are
     * shown if any exist.
     * And, if the question is DIFF_HARD then no differences in output after
     * the update are shown and only a basic message is displayed.
     * @return Formatted String showing feedback with contents dependent on
     * question difficulty, or null if a connection/processing error occurs
     * @author Tala Ross(RSSTAL002)
     */
    private StatementOutput getQueryFeedback() {
        //Get feedback and its level of help based on the question difficulty:
        try {
            switch (difficulty) {
                case DIFF_EASY:
                    return getQueryFeedbackComplete();
                case DIFF_MEDIUM:
                    return getQueryFeedbackSubset();
                default:
                    return new StatementOutput(getFeedbackMessage());
            }
        }
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Gets the feedback for a query question when the question difficulty is
     * DIFF_EASY.
     * @return Formatted String showing expected and received output after
     * of statement or null if DB connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private StatementOutput getQueryFeedbackComplete() {
        try {
            //Show expected output:
            return new StatementOutput(getFeedbackMessage(), "Expected Output:", rsExpected, "Your Output:", rsStudent);
        }
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Gets the feedback for a query question, when question difficulty is
     * DIFF_MEDIUM.
     * @return Formatted String showing the differences in a single row if any
     * exist or null if DB connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private StatementOutput getQueryFeedbackSubset() {
        try {
            String feedback = "An example row difference is shown below.\n"
                    + "Expected Output: " + diffRowExpected + "\n"
                    + "Your Output: " + diffRowStudent + "\n";
            
            //All feedback with a basic message at start and some help if it didn't compile:
            return new StatementOutput(getFeedbackMessage() + "\n" + feedback);
        } 
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Gets the feedback for an update question, with contents dependent on
     * this question difficulty. 
     * That is, if the question is DIFF_EASY then the expected and received
     * output after updates are both shown.
     * If the question is DIFF_MEDIUM then the differences in amount of rows
     * affected and the tables being affected are shown.
     * And, if the question is DIFF_HARD then no differences in output after
     * the update are shown and only a basic message is displayed.
     * @return Formatted String showing feedback with contents dependent on
     * question difficulty, or null if a connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private StatementOutput getUpdateFeedback() {
        //Get feedback and its level of help based on the question difficulty:
        try {
            switch (difficulty) {
                case DIFF_EASY:
                    return getUpdateFeedbackComplete();
                case DIFF_MEDIUM:
                    return getUpdateFeedbackSubset();
                default:
                    return new StatementOutput(getFeedbackMessage());
            }
        }
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Gets the feedback for an update question .
     * @return Formatted String showing expected and received output after
     * update and the number of affected rows or null if DB
     * connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private StatementOutput getUpdateFeedbackComplete() {
        try {
            String feedback = "The expected changes after the update:\n"
                            + tblNameExpected + " - " + raExpected + "row(s) affected\n\n"
                            + "Your changes after the update:\n"
                            + tblNameStudent + " - " + raStudent + "row(s) affected\n";
            return new StatementOutput(getFeedbackMessage() + "\n" + feedback, 
                                    "Expected table contents after update:", rsExpected, 
                                    "Your changes after the update:", rsStudent);  
        }
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Gets the feedback for an update question, when difficulty is DIFF_MEDIUM.
     * Only the difference in tables being updated and the number of affected
     * rows is shown.
     * @return Formatted String showing expected table updated and the rows
     * affected and the received table updated and the rows affected.
     * connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private StatementOutput getUpdateFeedbackSubset() {
        try {
            String feedback = "The expected changes after the update:\n"
                            + tblNameExpected + " - " + raExpected + "row(s) affected\n\n"
                            + "Your changes after the update:\n"
                            + tblNameStudent + " - " + raStudent + "row(s) affected\n";
            return new StatementOutput(getFeedbackMessage() + "\n" + feedback);  
        }
        catch (SQLException e) {
            System.out.println("Error: Couldn't read output.");
            System.out.println(e);
            return null;
        } 
    }
    
    /**
     * Gets a basic return message or returns an empty String if the question
     * couldn't be marked or the lecturer statement didn't compile.
     * This is used when difficulty is DIFF_HARD
     * @return A basic return message based on question mark or an empty String
     * if lecturer/connection/processing error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    private String getFeedbackMessage() {
        if (mark==MARK_BASE_ERR) {
            if (errorMessage.equals(STUDENT_ERR_COMPILE)) { //didn't compile
                return "Oops! Your statement didn't compile.\n";
            }
            else {  //didn't use permitted SQL command
                return "Oops! Your statement used a SQL command which wasn't permitted.\n";
            }
        }
        else if (mark==MARK_BASE_COMPILED) {
            return "Almost! Your statement compiled but wasn't correct.\n";
        }
        else if (mark==this.getMaxMark()) {
            return "Well done! Your statement compiled and was 100% correct.\n";
        }
        else {  //lecturer statement didn't compile/wasn't able to mark
            return "";
        }
    }
    
    /**
     * Gets a string showing additional help to the student, based on the
     * questions difficulty's corresponding help factor, if the student's
     * answer didn't compile. That is, the amount of help provided decreases
     * with difficulty.
     * @return Formatted help String.
     * @author Tala Ross (RSSTAL002)
     */
    private String getAdditionalHelp() {
        //Get help factor:
        double helpFactor;
        switch (difficulty) {
            case DIFF_EASY:
                helpFactor = HELP_FACTOR_EASY;
                break;
            case DIFF_MEDIUM:
                helpFactor = HELP_FACTOR_MEDIUM;
                break;
            case DIFF_HARD:
                helpFactor = HELP_FACTOR_HARD;
                break;
            default:
                return getFeedbackMessage();
        }
        
        String additionalHelp = "";
        
        //Get help message for help factor greater than zero:
        if (helpFactor>0) {
            //Split the answer up into a words array and get the number of words to show from this:
            String [] words = answer.trim().split(" ");
            int noToShow = (int)(words.length*helpFactor);
            if (noToShow==0) {  //ust show at least 1 word
                noToShow = 1;
            }

            //Create the partial suggestion statement:
            String partialStatement = "";
            for (int i=0; i<noToShow; i++) {
                partialStatement+= words[i] + " ";
            }
            additionalHelp = "\nTip: Try a statement that starts like the statement below.\n"
                                + partialStatement.substring(0, partialStatement.length() - 1) + "...\n\n";
        }
        
        return getFeedbackMessage() + additionalHelp;
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

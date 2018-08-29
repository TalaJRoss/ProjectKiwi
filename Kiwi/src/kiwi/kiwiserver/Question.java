package kiwi.kiwiserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import kiwi.message.StudentMessage;

/**
 * Stores information relevant to a question and processes marking and
 * provision of feedback.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Question {
    
    public static final String TYPE_SELECT = "Select";
    public static final String TYPE_ARITHMETIC = "Arithmetic";
    public static final String TYPE_JOIN = "Join";
    public static final String TYPE_UPDATE = "Update";
    
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
    
    private int questionDBNo;
    
    /**
     * Mark student receives after question is marked.
     * Can be 0, difficulty or difficulty*2.
     */
    int mark;
    
    /**
     * Result set from expected sql statement output.
     */
    ResultSet rsExpected;
    int raExpected;
    
    /**
     * Number of columns in expected sql statement output.
     */
    int expectedColCount;
    
    /**
     * Result set from student's sql statement output.
     */
    ResultSet rsStudent;
    int raStudent;
    
    /**
     * Number of columns in student's sql statement output.
     */
    int studentColCount;
    
    /**
     * Error message received on student statement execution - if any.
     */
    String errorMessage;
    
    private Connection connLimited;
    private Connection conn;
    
    //Constructor:
    
    /**
     * Creates question object.
     * @param question English question.
     * @param answer SQL statement expected answer.
     * @param difficulty Difficulty value (1, 2 or 3)
     * @param type
     * @param questionDBNo
     * @param connLimited
     * @param conn
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
     */
    public Question(Connection connLimited) {
        mark= 0;
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
    
    /**
     * Marks the given student answer. The student answer statement and
     * expected answer statement are run and the outputs compared. Then a mark
     * is returned.
     * @param studentAns SQL statement that student submitted as answer.
     * @return Mark received by student for the question or -1 for lecturer
     * answer err or -2 for db conn/processing err
     */
    public int mark(String studentAns) {
        if (type.toLowerCase().equals(TYPE_UPDATE.toLowerCase())) {  //update question
            return markUpdateQuestion(studentAns);
        }
        else { //query question
            return markQueryQuestion(studentAns);
        }
    }
    
    private int markQueryQuestion(String studentAns) {
        try {
            //Get expected output:
            
            //Check expected query statement command:
            String tblNameExp = "";
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
                mark = -1;
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
                mark = -1;
                return mark;
            }
            
            expectedColCount = rsExpected.getMetaData().getColumnCount();
           
            //Get student output:
            
            //Check expected query statement command:
            if (!studentAns.toUpperCase().startsWith("SELECT")) {   //student answer does not execute as expected
                errorMessage = "Statement provided is not executing a permitted SQL query statement!\n"
                        + "i.e. SELECT";
                mark = MARK_RANGE[0];
                return mark;
            }
            
            Statement stStudent = connLimited.createStatement();
            try {
                rsStudent = stStudent.executeQuery(studentAns); //execute student's sql statement
            }
            catch (SQLException e) { //didn't compile
                mark = MARK_RANGE[0];
               
                //Process error message:
                errorMessage = "SQL Statement did not compile.\n"
                        + e.toString().substring("java.sql.".length()).replace("kiwidb.",""); //remove "java.sql." from error name and "kiwidb." from table name
               
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
            mark = -2;
            return mark;
        }
    }
    
    
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
                mark = -1;
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
                mark = -1;
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
                mark = -2;
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
                mark = MARK_RANGE[0];
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

                 mark = MARK_RANGE[0];
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
                mark = MARK_RANGE[1];
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
            mark = -2;
            return mark;
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
        
        if (mark==Question.MARK_RANGE[0]) {  //answer statement didn't compile
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
        
        if (mark==Question.MARK_RANGE[0]) {  //answer statement didn't compile
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

    public int getQuestionDBNo() {
        return questionDBNo;
    }
    
}

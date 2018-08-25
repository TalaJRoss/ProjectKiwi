package kiwi.kiwiserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates assignment interface.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Assignment {
    
    
    /**
     * Database connection.
     */
    private Connection conn;
    
    //TODO: update to get from server:
    /**
     * Number of questions in an assignment.
     */
    private int noQuestions = 9;
    
    //TODO: load questions into list when multiple question assignment functionality created
    /**
     * List of questions in assignment.
     */
    private ArrayList<Question> questionList;
    
    /**
     * Current question being used.
     */
    private int currentPos;
    
    /**
     * Static Array of types of questions
     */
    public static final String [] Types = {"select","arithmetic","update"};
    
    
    public Assignment(Connection conn, String studentNo, int noSubmissionsCompleted) throws SQLException {
        this.conn = conn;
        this.questionList = new ArrayList<>();
        this.currentPos = -1;
        generateQuestions(studentNo,noSubmissionsCompleted);
    }
    
   
    //TODO: make fair question selection
    //TODO: make based on student no.
    /**
     * Generates list of questions that make up the assignment.
     * @return The list of questions.
     */
    private void generateQuestions(String studentNo, int noSubmissionsCompleted) {
        
        try {
            //Check no submissions remaining:
            //String query = "SELECT MaxNoSubmissions";
            Statement st = conn.createStatement();
            
            //Get the total number of questions in the questions table:
            String query = "SELECT COUNT(*) AS noRows FROM Questions";
            ResultSet rs = st.executeQuery(query);
            rs.next();  //take cursor to first row
            int noRows = rs.getInt("noRows");
            
            //Get number of different types of questions there are
            /*query = "SELECT COUNT(DISTINCT type) AS numType FROM Questions";
            rs = st.executeQuery(query);
            rs.next();
            int numType = rs.getInt("numType");*/
            int numType =3;
            
            //Calculate number of question per type and difficulty
            int numQperTypeDiff = noQuestions/(3*numType);
            int numQRemaining = noQuestions-(numQperTypeDiff*(3*numType));
            boolean [] TotalUsed = new boolean [noRows];
            
            //Setup:
            String seed = studentNo + noSubmissionsCompleted;
            System.out.println("DEbug: "+seed);
            Random rnd = new Random(seed.hashCode());
            
            System.out.println(""+numQperTypeDiff);
            //Get list of questions:
            //For each difficulty of the questions
            for (int i=1; i<4; i++) {
                //For each types of this question type
                System.out.println("for 1");
                for (int j=0; j<numType; j++)
                {
                    System.out.println("for 2");
                    //Get the number of questions that has difficulty i and type j
                    query = "SELECT COUNT(*) AS numQ FROM questions WHERE difficulty="
                            + i
                            +" and type like '"
                            + Types[j]
                            +"'";
                    System.out.println(query);
                    rs = st.executeQuery(query);
                    rs.next();
                    int numQ = rs.getInt("numQ");
                    
                    System.out.println("executed query");
                    //For number of questions we need for each of these type and difficulty
                    System.out.println(""+numQperTypeDiff);
                    for (int k=0; k<numQperTypeDiff; k++) 
                    {
                        
                        System.out.println("*");
                        boolean [] usedQuestions = new boolean [numQ];    //true if used otherwise false (used to avoid duplicates)
                        int random = 0;
                        
                        boolean used = true;
                        while (used) {  //loop until unused question number found
                            System.out.println(""+numQ);
                            random = rnd.nextInt(numQ)+1;
                            if (!usedQuestions[random-1])
                            {
                                usedQuestions[random-1] = true;
                                used = false;
                            }
                        }
                        
                        //Get the question from the table
                        query = "SELECT * FROM (SELECT * FROM questions WHERE difficulty="
                                + i
                                + " AND type LIKE '"
                                + Types[j]
                                + "') as t limit "
                                + (random-1)
                                + ",1";
                        rs = st.executeQuery(query);
                        
                        //Create question object from the result set
                        Question question = new Question (conn);
                        int Qid = 0;
                        while(rs.next())
                        {
                            Qid = rs.getInt("QuestionNo");
                            String tempQ = rs.getString("Question");
                            String answer = rs.getString("Answer");
                            int difficulty = rs.getInt("Difficulty");
                            String type = rs.getString("Type");
                            question = new Question(tempQ, answer, difficulty, type, conn);
                        }
                        
                        questionList.add(question);
                        System.out.println("added");
                        
                        // set the availibilty of this question to false
                        TotalUsed[Qid-1] = true;
                    }
                }
            }
            
            int random =0;
            //For the questions left over
            for (int i=0; i<numQRemaining; i++)
            {
                //Get question number to query:
                boolean used = true;
                while (used)
                {
                    random =  rnd.nextInt(noRows) + 1;
                    if (!TotalUsed[random-1]) {
                        TotalUsed[random-1] = true;
                        used = false;
                    }
                }
                
                //Get question from questions table:
                query = "SELECT * FROM Questions WHERE QuestionNo LIKE '" + random + "'";
                rs = st.executeQuery(query);
                
                //Create question object:
                Question question = new Question(conn);
                while (rs.next()) {
                    String tempQuestion = rs.getString("Question");
                    String answer = rs.getString("Answer");
                    int difficulty = rs.getInt("Difficulty");
                    String type = rs.getString("Type");
                    question = new Question(tempQuestion, answer, difficulty, type, conn);
                }
                
                //Add question to list:
                questionList.add(question);
            }
            System.out.println(""+questionList.size());
            //Clean up:
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(Assignment.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    //beware null
    /**
     * Returns the next question on the list of questions.
     * @return the next question
     */
    public Question nextQuestion() {
        if (questionList.size()-1 == currentPos) {
            return null;
        }
        currentPos++;
        return questionList.get(currentPos);
    }
    
    public boolean hasNext() {
        return questionList.size()-1 < currentPos;
    }
    
    public Question getQuestion()  {
        return questionList.get(currentPos);
    }
    
    public int getDifficulty() {
        return questionList.get(currentPos).getDifficulty();
    }
    
    public String getQuestionText() {
        return questionList.get(currentPos).getQuestion();
    }
    
    public String getAnswer() {
        return questionList.get(currentPos).getAnswer();
    }
    
    public int mark(String studentAns) {
        return questionList.get(currentPos).mark(studentAns);
    }
    
    public String getFeedback(String studentAns) {
        return questionList.get(currentPos).getFeedback();
    }
    
    //TODO: update grade on server
    public double getGrade() {
        int outOf = 0;
        int received = 0;
        for (Question q: questionList) {
            //if lecturers code does not compile or can't compare results then don't include question in calculation:
            if (q.getMark()>=0)    {   
                outOf+= q.getMaxMark();
                received+= q.getMark();
            }
        }
        double grade = ((double)received)/outOf*100;
        return grade;
    }

    public int getQuestionNumber() {
        return currentPos + 1;
    }

    public int getNoQuestions() {
        return noQuestions;
    }
    
    //TOD): update questions + transacts
    /**
     * Runs given student sql statement and returns string representation of
     * the output.
     * @param statement Student's sql statement.
     * @return returns string representing output or null if connection error occurs
     */
    public String check(String statement) {
        String toReturn = "";
        try { 
            //Get table being updated in expected sql update:
            String tblName = "";
            if (statement.toUpperCase().startsWith("INSERT")) {
                if (statement.split(" ").length>2) {
                    tblName = statement.split(" ")[2];  //insert into <table>    
                }
            }
            else if (statement.toUpperCase().startsWith("UPDATE")) {
                if (statement.split(" ").length>1) {
                    tblName = statement.split(" ")[1];  //update <table>
                }
            }
            else if (statement.toUpperCase().startsWith("DELETE")) {
                if (statement.split(" ").length>2) {
                    tblName = statement.split(" ")[2];  //delete from <table>
                }
            }
            else if (statement.toUpperCase().startsWith("SELECT")) {
                tblName = null;
            }
            else {   //not permitted statement
                System.out.println("Error: statement from lecturer is not executing a permitted SQL DML statement!");
                return "Statement provided is not executing a permitted SQL DML statement.\n"
                        + "That is, SELECT, INSERT, UPDATE or DELETE.";
            }
            
            if (tblName==null) { //select statement
               //Get student output:
                Statement st = conn.createStatement();
                ResultSet rs;
                try {   //check it compiles 
                    rs = st.executeQuery(statement);
                }
                catch (SQLException e) {//didn't compile
                    //Process error message:
                    String errMessage = e.toString().substring("java.sql.".length()); //remove "java.sql." from error name
                    if (errMessage.contains("kiwidb.")) {
                        errMessage= errMessage.replace("kiwidb.","");    //remove "kiwidb." from table name
                    }
                    //System.out.println(e);   //DEBUG
                    return "SQL Statement did not compile.\n" + errMessage + "\n";
                }

                //Put output in string:
                //Get column names:
                int noColumns = rs.getMetaData().getColumnCount();
                for (int i=1; i<=noColumns; i++) {   //each column
                    toReturn+= rs.getMetaData().getColumnName(i) + "\t";
                }
                toReturn+="\n";

                //Get row entries
                while(rs.next()) {  //each row
                    for (int i=1; i<=noColumns; i++) {  //each field in row
                        toReturn+= rs.getObject(i) + "\t";
                    }
                    toReturn+="\n";     //end of row
                } 
                toReturn+="\n";     //end of output

                return toReturn;
            }
            else { //update statement 
                Statement st = conn.createStatement();
                ResultSet rs;
                int ra;
                conn.setAutoCommit(false);
                Savepoint sp = conn.setSavepoint();

                //Update:
                try {
                     ra = st.executeUpdate(statement);   //execute expected sql update and get rows affeted
                }
                catch (SQLException e) { //didn't compile
                    conn.rollback(sp);
                    conn.setAutoCommit(true);
                    String errMessage = e.toString().substring("java.sql.".length()); //remove "java.sql." from error name
                    if (errMessage.contains("kiwidb.")) {
                        errMessage= errMessage.replace("kiwidb.","");    //remove "kiwidb." from table name
                    }
                    //System.out.println(e);   //DEBUG
                    return "SQL Statement did not compile.\n" + errMessage + "\n";
                }

                //Expected new table:
                try {
                    rs = st.executeQuery("SELECT * FROM " + tblName + ";");
                }
                catch (SQLException e) {
                    conn.rollback(sp);
                    conn.setAutoCommit(true);
                    System.out.println("Error: Problem reading students updated table for check.");
                    System.out.println(e);
                    return null;
                }

                //End transaction:
                conn.rollback(sp);
                conn.setAutoCommit(true);
                
                //Put output in string:
                //Get column names:
                toReturn+= ra + " row(s) affected";
                toReturn+= "Table, " + tblName + ", after statement execution:\n";
                int noColumns = rs.getMetaData().getColumnCount();
                for (int i=1; i<=noColumns; i++) {   //each column
                    toReturn+= rs.getMetaData().getColumnName(i) + "\t";
                }
                toReturn+="\n";

                //Get row entries
                while(rs.next()) {  //each row
                    for (int i=1; i<=noColumns; i++) {  //each field in row
                        toReturn+= rs.getObject(i) + "\t";
                    }
                    toReturn+="\n";     //end of row
                } 
                toReturn+="\n";     //end of output

                return toReturn;
            }  
        }
        catch (SQLException e) {    //connection error
            System.out.println("Error: Problem reading output.");
            System.out.println(e); 
            return null;
        } 
    }
}

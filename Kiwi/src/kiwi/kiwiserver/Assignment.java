package kiwi.kiwiserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import kiwi.message.StatementOutput;

/**
 * Creates assignment interface.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Assignment {
    
    //Constants:
    /**
     * Static Array of types of questions.
     */
    public static final String [] QUESTION_TYPES = {Question.TYPE_SELECT, Question.TYPE_ARITHMETIC, Question.TYPE_UPDATE};
    
    
    //Instance Variables:
    
    //Database Connection Variables:
    /**
     * Database connection, not allowed access to student and question info
     * tables.
     */
    private final Connection connLimited;
    
    /**
     * Database connection, allowed access to student and question info tables.
     */
    private final Connection conn;
    
    
    //Assignment Details Variables:
    /**
     * Number of questions in an assignment.
     */
    private final int noQuestions;
    
    /**
     * List of questions in assignment.
     */
    private ArrayList<Question> questionList;
    
    /**
     * Current position in the question list.
     * That is, the index of the question being used.
     */
    private int currentPos;
    
    
    //Constructors:
    /**
     * Creates an assignment object specific to the student it is being created
     * for or not if the assignment must be a closed practical.
     * The database connections are passed in and then the closed practical
     * option and number of questions contained information are fetched from the database.
     * @param connLimited Database connection, not allowed access to system's tables.
     * @param conn Database connection, allowed access to system's tables.
     * @param studentNo The student number of the student the assignment is being created for.
     * @param noSubmissionsCompleted The number of submissions already completed by the student the assignment is being created for.
     * @throws java.sql.SQLException
     */
    public Assignment(Connection conn, Connection connLimited, String studentNo, int noSubmissionsCompleted) throws SQLException{
        //Initialise connections: 
        this.conn = conn;
        this.connLimited = connLimited;

        //Initialize question info:
        this.questionList = new ArrayList<>();
        this.currentPos = -1;

        //Get number of questions in an assignment from assignmentInfo table in the database
        Statement st = conn.createStatement();
        String query = "SELECT NoQuestions FROM assignmentinfo";
        ResultSet rs = st.executeQuery(query);
        rs.next();
        noQuestions = rs.getInt("NoQuestions");

        // Determine wheather it is a close prac or an assignment:
        query = "SELECT ClosedPrac FROM assignmentinfo";
        rs = st.executeQuery(query);
        rs.next();
        int closedPrac = rs.getInt("ClosedPrac");

        //Clean Up:
        rs.close();
        st.close();

        //Generate Questions:
        generateQuestions(studentNo,noSubmissionsCompleted, closedPrac);
    }
    
   
    /**
     * Generates list of questions that make up the assignment.
     * This generates assignments using a random generator seed based on
     * student number and submissions completed for assignments so that each
     * assignment for each student is unique. 
     * And, for closed pracs a constant seed is used for question selection so
     * each student has the same question as each other and for very submission.
     * An even ratio of questions of each type-difficulty combination are
     * selected randomly based on the given seeds.
     * @author Steve Shun Wang(WNGSHU003)
     */
    private void generateQuestions(String studentNo, int noSubmissionsCompleted, int closedPrac) throws SQLException {
        
        // Using int closed prac to determine the seed for random()
        String seed; 
        if (closedPrac == 1)
        {
            seed = "1234";
        }
        else
        {
            seed = studentNo + noSubmissionsCompleted;
        }

        //Check no submissions remaining:
        //String query = "SELECT MaxNoSubmissions";

        //Setup with database:
        Statement st = conn.createStatement();

        //Get the total number of questions in the questions table:
        String query = "SELECT COUNT(*) AS noRows FROM Questions";
        ResultSet rs = st.executeQuery(query);
        rs.next();  //take cursor to first row
        int noRows = rs.getInt("noRows");
        
        //Calculate number of question per type and difficulty
        int numQperTypeDiff = noQuestions/(Question.NO_DIFFICULTY_LEVELS*Question.NO_TYPES);
        //int numQRemaining = noQuestions-(numQperTypeDiff*(Question.NO_DIFFICULTY_LEVELS*Question.NO_TYPES));
        boolean [] TotalUsed = new boolean [noRows];

        //Setup:
        Random rnd = new Random(seed.hashCode());

        //Get list of questions:
        //For each difficulty of the questions
        for (int i=1; i<Question.NO_DIFFICULTY_LEVELS+1; i++) {
            //For each types of this question type
            for (int j=0; j<Question.NO_TYPES; j++)
            {
                //Get the number of questions that has difficulty i and type j
                query = "SELECT COUNT(*) AS numQ FROM questions WHERE difficulty="
                        + i
                        +" and type LIKE '"
                        + QUESTION_TYPES[j]
                        +"'";
                rs = st.executeQuery(query);
                rs.next();
                int numQ = rs.getInt("numQ");

                boolean [] usedQuestions = new boolean [numQ];    //true if used otherwise false (used to avoid duplicates)
                int random = 0;
                
                //For number of questions we need for each of these type and difficulty
                for (int k=0; k<numQperTypeDiff; k++) 
                {
                    boolean used = true;
                    while (used) {  //loop until unused question number found
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
                            + QUESTION_TYPES[j]
                            + "') as t limit "
                            + (random-1)
                            + ",1";
                    rs = st.executeQuery(query);

                    //Create question object from the result set
                    Question question = new Question (connLimited);
                    int Qid = 0;
                    String problem = "";
                    while(rs.next())
                    {
                        Qid = rs.getInt("QuestionNo");
                        String tempQ = rs.getString("Question");
                        String answer = rs.getString("Answer");
                        int difficulty = rs.getInt("Difficulty");
                        String type = rs.getString("Type");
                        problem = rs.getString("Problem");
                        question = new Question(tempQ, answer, difficulty, type, Qid, connLimited, conn);
                    }
                    
                    // Add question to the question list
                    // Check that if the question has any error with it
                    if ( problem == null)       //no error
                    {
                        questionList.add(question);
                    }
                    else        
                    {   // There is an error with the choosen question therefore need to loop through again for a new question 
                        k--; 
                        usedQuestions[random-1] = true; // Do not choose the current question again 
                    }

                    // set the availibilty of this question to false
                    TotalUsed[Qid-1] = true;
                    
                    // Check if there are any questions availiable to satisfy the current type and difficulty 
                    int NoUsedQuestion = 0;
                    for (int x=0; x< usedQuestions.length;x++)
                    {
                        if (!usedQuestions[x])
                        {
                            break;
                        }
                        else
                        {
                            NoUsedQuestion++;
                        }
                        
                    }
                    // There are no more question for this category so exit the loop 
                    if (NoUsedQuestion==usedQuestions.length)
                    {
                        k = numQperTypeDiff; 
                    }
                }
            }
        }
        
        // Check how many question are still required
        //(weren't enough type-diff pair questions without errors or remainder ie. didn't evenly divide between type-diff.)
        int numQRemaining = noQuestions - questionList.size();
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
            String problem ="";
            Question question = new Question(connLimited);
            while (rs.next()) {
                int Qid = rs.getInt("QuestionNo");
                String tempQuestion = rs.getString("Question");
                String answer = rs.getString("Answer");
                int difficulty = rs.getInt("Difficulty");
                String type = rs.getString("Type");
                problem = rs.getString("Problem");
                question = new Question(tempQuestion, answer, difficulty, type, Qid, connLimited, conn);
            }

            //Add question to list:
            //First check that there are no question in the list
            if ( problem == null)
            {
                questionList.add(question);
            }
            else
            {
                i--;
                TotalUsed[random-1] = true;
            }
            
            if (questionList.size()<noQuestions) {    //then there aren't enough questions without errors
                questionList=null;
            }
            
            //Now that we have the complete quetion list, randomise its order:
            Collections.shuffle(questionList);
        }
        //Clean up:
        rs.close();
        st.close();
            
    }
    
    /**
     * Runs given student SQL statement and returns string representation of
     * the output.
     * @param statement Student's SQL statement.
     * @return returns string representing output or null if connection error occurs.
     * @author Tala Ross(RSSTAL002)
     */
    public StatementOutput check(String statement) {
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
            else if (statement.toUpperCase().startsWith("SELECT")) {    //select statement
                tblName = null;
            }
            else {   //not permitted statement
                System.out.println("Error: statement from lecturer is not executing a permitted SQL DML statement!");
                String message = "Statement provided is not executing a permitted SQL DML statement.\n"
                                + "That is, SELECT, INSERT, UPDATE or DELETE.";
                return new StatementOutput(message);
            }
            
            //Process a select statement:
            if (tblName==null) {
               //Get student output:
                Statement st = connLimited.createStatement();
                ResultSet rs;
                try {   //check it compiles 
                    rs = st.executeQuery(statement);
                }
                catch (SQLException e) {//didn't compile
                    String message = "SQL Statement did not compile or was not allowed.";
                    return new StatementOutput(message);
                }
                
                //Return output object:
                return new StatementOutput("The output of the statement, " + statement + ", is:\n", rs);
            }
            
            //Process update statement:
            else {
                //Setup:
                Statement st = connLimited.createStatement();
                ResultSet rs;
                int ra;
                
                //Start transaction:
                connLimited.setAutoCommit(false);
                Savepoint sp = connLimited.setSavepoint();

                //Run the update and get rows affeted:
                try {
                     ra = st.executeUpdate(statement);
                }
                catch (SQLException e) { //didn't compile
                    connLimited.rollback(sp);
                    connLimited.setAutoCommit(true);
                    String message = "SQL Statement did not compile or was not allowed.";
                    return new StatementOutput(message);
                }

                //Get the new table after the update:
                try {
                    rs = st.executeQuery("SELECT * FROM " + tblName + ";");
                }
                catch (SQLException e) {
                    connLimited.rollback(sp);
                    connLimited.setAutoCommit(true);
                    System.out.println("Error: Problem reading students updated table for check.");
                    System.out.println(e);
                    return null;
                }

                //End transaction:
                connLimited.rollback(sp);
                connLimited.setAutoCommit(true);
                
                //Return output object:
                String message = tblName + ": " + ra + " row(s) affected\n";
                String intro = "Table, " + tblName + ", after statement execution:\n";
                return new StatementOutput(message, intro, rs);
            }  
        } 
        catch (SQLException e) {    //connection error
            System.out.println("Error: Problem reading output.");
            System.out.println(e); 
            return null;
        }
    }
    
    
    //Getters and Checkers:
    /**
     * Gets the next question on the list of questions, or returns null if no
     * questions remain, and updates the current position.
     * @return The next question on the question list.
     */
    public Question nextQuestion() {
        if (questionList.size()-1 == currentPos) {
            return null;
        }
        currentPos++;
        return questionList.get(currentPos);
    }
    
    /**
     * Checks whether there are unanswered questions remaining.
     * @return true if there are questions remaining otherwise false.
     */
    public boolean hasNext() {
        return currentPos < questionList.size()-1;
    }
    
    /**
     * Gets the current question object.
     * @return The current question object.
     */
    public Question getQuestion()  {
        return questionList.get(currentPos);
    }
    
    /**
     * Gets the difficulty of the current question.
     * @return The difficulty of the current question.
     */
    public int getDifficulty() {
        return questionList.get(currentPos).getDifficulty();
    }
    
    /**
     * Gets the text representation of the current question.
     * @return The text representation of the current question.
     */
    public String getQuestionText() {
        return questionList.get(currentPos).getQuestion();
    }
    
    /**
     * Gets the expected answer SQL statement for the current question.
     * @return The expected answer SQL statement for the current question.
     */
    public String getAnswer() {
        return questionList.get(currentPos).getAnswer();
    }
    
    /**
     * Gets the mark received for the current question.
     * @param studentAns The answer provided by student to the current question.
     * @return The the mark received for the current question.
     */
    public int mark(String studentAns) {
        return questionList.get(currentPos).mark(studentAns);
    }
    
    /**
     * Gets the feedback received for the current question.
     * @param studentAns The answer provided by student to the current question.
     * @return The the feedback received for the current question.
     */
    public StatementOutput getFeedback(String studentAns) {
        return questionList.get(currentPos).getFeedback();
    }
    
    /**
     * Gets the final grade for the assignment by adding all marks for each
     * question, or giving 0 if the assignment is incomplete(ie.there are
     * unanswered questions).
     * @return The final grade for the assignment.
     */
    public double getGrade() {
        int outOf = 0;
        int received = 0;
        for (Question q: questionList) {
            //If lecturers code does not compile for some reason we can't
            //compare results then don't include question in calculation:
            if (q.getMark()>=0)    {    //question was able to be marked  
                outOf+= q.getMaxMark();
                received+= q.getMark();
            }
        }
        double grade = ((double)received)/outOf*100;
        return grade;
    }
    
    /**
     * Gets the question number of the question.
     * That is the current list position + 1 (for real world representation).
     * @return The question number(student-end representation).
     */
    public int getQuestionNumber() {
        return currentPos + 1;
    }
    
    /**
     * Gets the question number(ie.ID) of the question in the database.
     * @return The question number/ID of the question in the database.(server-end representation).
     */
    public int getQuestionID() {
        return questionList.get(currentPos).getQuestionDBNo();
    }
    
    /**
     * Gets the total number of questions in the assignment.
     * @return The total number of questions in the assignment.
     */
    public int getNoQuestions() {
        return noQuestions;
    }
    
    /**
     * Gets whether or not a complete question list has been generated.
     * @return true if the list was generated and false otherwise
     */
    public boolean couldGenerate() {
        return questionList!=null;
    }
}

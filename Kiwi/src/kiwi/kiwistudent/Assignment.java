package kiwi.kiwistudent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Creates assignment interface.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Assignment {
    
    /**
     * Student object containing and controlling information relevant to logged
     * in student.
     */
    private Student student;
    
    //TODO: update to get from server:
    /**
     * Number of questions in an assignment.
     */
    private int noQuestions = 3;
    
    //TODO: load questions into list when multiple question assignment functionality created
    /**
     * List of questions in assignment.
     */
    private ArrayList<Question> questionList;
    
    /**
     * Current question being used.
     */
    private int currentPos;

    public Assignment(Student student) {
        this.student = student;
        this.questionList = new ArrayList<>();
        this.currentPos = -1;
        generateQuestions();
        student.decrementSubmissionsAllowed();
    }
    
    
    
    
    //TODO: make fair question selection
    //TODO: make based on student no.
    /**
     * Generates list of questions that make up the assignment.
     * @return The list of questions.
     */
    private void generateQuestions() {
        
        try {
            //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            //Get the number of questions in the questions table:
            String query = "SELECT COUNT(*) AS noRows FROM Questions";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            rs.next();  //take cursor to first row
            int noRows = rs.getInt("noRows");
            
            //Setup:
            boolean [] usedQuestions = new boolean [noRows];    //true if used otherwise false (used to avoid duplicates)
            int random = 0;
            Random rnd = new Random();
            
            //Get list of questions:
            for (int i=0; i<noQuestions; i++) {
            
                //Get question number to query:
                boolean used = true;
                while (used) {  //loop until unused question number found
                    random =  rnd.nextInt(noRows) + 1;
                    if (!usedQuestions[random-1]) {
                        usedQuestions[random-1] = true;
                        used = false;
                    }
                }

                //Get question from questions table:
                query = "SELECT * FROM Questions WHERE QuestionNo LIKE '" + random + "'";
                rs = st.executeQuery(query);

                //Create question object:
                Question question = new Question();
                while (rs.next()) {
                    String tempQuestion = rs.getString("Question");
                    String answer = rs.getString("Answer");
                    int difficulty = rs.getInt("Difficulty");
                    question = new Question(tempQuestion, answer, difficulty);
                    System.out.format("%s, %s, %s\n", question, answer, difficulty );   //DEBUG
                }

                //Add question to list:
                questionList.add(question);

                }
            
            //Clean up:
            rs.close();
            st.close();
            conn.close();
            
            }
        catch (SQLException e) {
            System.out.println("Error: Problem query database or reading result set output.");
            System.out.println(e); 
            }
        catch (Exception e) {
            System.out.println("Error: Problem connecting to database/loading driver.");
            System.out.println(e);
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
        return questionList.get(currentPos).getFeedback(studentAns);
    }
    
    //TODO: update grade on server
    public String getGrade() {
        int outOf = 0;
        int received = 0;
        for (Question q: questionList) {
            outOf+= q.getMaxMark();
            received+= q.getMark();
        }
        double grade = ((double)received)/outOf*100;
        student.updateGrade(grade);
        DecimalFormat d = new DecimalFormat("0.00");
        return d.format(grade);
    }
    
}

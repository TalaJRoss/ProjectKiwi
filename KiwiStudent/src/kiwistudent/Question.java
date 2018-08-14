package kiwistudent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class Question {
    
    //Constants:
    public static final int [] markRange = {0,1,2};
    
    //Instance Variables:
    private String question;
    private String answer;
    private int difficulty;
    int mark;
    
    ResultSet rsExpected;
    int expectedColCount;
    ResultSet rsStudent;
    int studentColCount;
    String errorMessage;
    
    
    public Question(String question, String answer, int difficulty) {
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
        this.mark = 0;
    }

    public Question() {
        mark= 0;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getDifficulty() {
        return difficulty;
    }
    
    //TODO: check student not trying data manipulation (ie.not select command)
    public int mark(String studentAns) {
        try {
           // create our mysql database connection
           Class.forName("com.mysql.cj.jdbc.Driver");
           String myUrl = "jdbc:mysql://localhost/KiwiDB";
           Connection conn = DriverManager.getConnection(myUrl, "root", "mysql");
           
           //get expected output:
           Statement st2 = conn.createStatement();
           try {
                rsExpected = st2.executeQuery(answer);
           }
           catch (SQLException e) { //didn't compile
               System.out.println("ERROR: answer from lecturer wrong!");
               System.out.println(e.toString());
               return -1;
           }
           expectedColCount= rsExpected.getMetaData().getColumnCount();
           
           // get student output:
           Statement st1 = conn.createStatement();
           try {
                rsStudent = st1.executeQuery(studentAns);
           }
           catch (SQLException e) { //didn't compile
               mark = markRange[0];
               errorMessage = e.toString().substring("java.sql.".length());
               if (errorMessage.contains("kiwidb.")) {
                   errorMessage= errorMessage.replace("kiwidb.","");
               }
               return mark;
           }
           studentColCount= rsStudent.getMetaData().getColumnCount();
           
           //wrong if not same no. columns:
           if (studentColCount!=expectedColCount) {
               mark = markRange[1];
               return mark;
           }
           //same no. columns:
           else {
               //wrong if different column sql types:
               for (int i=1; i<=expectedColCount; i++) {
                    if (rsStudent.getMetaData().getColumnType(i)!=rsExpected.getMetaData().getColumnType(i)){
                        mark = markRange[1];
                        return mark;
                    }
                }
               //compare rows of expected and received output:
               while(rsExpected.next()) {
                   if (rsStudent.next()) {
                       //compare each column value in rows:
                       for (int i=1; i<=expectedColCount; i++) {
                           if(!rsStudent.getObject(i).equals(rsExpected.getObject(i))) {
                                mark = markRange[1];
                                return mark;
                           }
                       }
                   }
                   else {   //too few rows
                       mark = markRange[1];
                       return mark;
                   }
               }
               if (rsStudent.next()) {  //too many rows
                   mark = markRange[1];
                   return mark;
               }
                mark = markRange[2]*difficulty;
                return mark;
           }
        }
        catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
        }
        return -1;
    }
    
    //TODO: differences in output
    public String getFeedback(String studentAns) {
        //check that lecturer sql ran:
        if (rsExpected==null) {
            return "ERROR: Couldn't run lecturer's sql answer!";
        }
        //show expected and received answer:
        String toReturn= "Expected SQL: " + answer + "\n"
            + "Your SQL: " + studentAns + "\n"
            + "\n";
        //show expected output:
        toReturn+= "Expected Output:\n";
        try {
            rsExpected.beforeFirst();
            for (int i=1; i<=expectedColCount; i++) {   //column names
                    toReturn+= rsExpected.getMetaData().getColumnName(i) + "\t";
                }
            toReturn+="\n";
            while(rsExpected.next()) {  //column entries
                for (int i=1; i<=expectedColCount; i++) {
                    toReturn+= rsExpected.getObject(i) + "\t";
                }
                toReturn+="\n";
            }
            toReturn+="\n";
        } 
        catch (SQLException ex) {
            Logger.getLogger(Question.class.getName()).log(Level.SEVERE, null, ex);
        }
        //show student output:
        toReturn+= "Your Output:\n";
        if (mark==Question.markRange[0]) {
            toReturn+= "SQL Statement did not compile.\n";
            toReturn+= errorMessage+"\n";
        }
        else {
            try {
                rsStudent.beforeFirst();
                for (int i=1; i<=studentColCount; i++) {   //column names
                        toReturn+= rsStudent.getMetaData().getColumnName(i) + "\t";
                    }
                toReturn+="\n";
                while(rsStudent.next()) {  //column entries
                    for (int i=1; i<=studentColCount; i++) {
                        toReturn+= rsStudent.getObject(i) + "\t";
                    }
                    toReturn+="\n";
                }
                toReturn+="\n";
            } 
            catch (SQLException ex) {
                Logger.getLogger(Question.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
        return toReturn;
    }
    
    public static String check(String statement) {
        String toReturn= "";
        try {
            //Setup connection:
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            // get student output:
            Statement st= conn.createStatement();
            ResultSet rs= st.executeQuery(statement);
            
            //put output in string:
            int noCol= rs.getMetaData().getColumnCount();
            for (int i=1; i<=noCol; i++) {   //column names
                toReturn+= rs.getMetaData().getColumnName(i) + "\t";
            }
            toReturn+="\n";
            while(rs.next()) {  //column entries
                for (int i=1; i<=noCol; i++) {
                    toReturn+= rs.getObject(i) + "\t";
                }
                toReturn+="\n";
            }    
        }
        catch (SQLException ex) {   
            String errorMessage = ex.toString().substring("java.sql.".length());
               if (errorMessage.contains("kiwidb.")) {
                   errorMessage= errorMessage.replace("kiwidb.","");
               }
               ex.printStackTrace();
            return "SQL Statement did not compile.\n" + errorMessage + "\n";
        } 
        catch (ClassNotFoundException ex) {
            return "ERROR: Couldn't load database driver.";
        }
        
        return toReturn;
    }
    
}

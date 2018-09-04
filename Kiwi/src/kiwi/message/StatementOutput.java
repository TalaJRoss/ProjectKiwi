package kiwi.message;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains the information regarding the output and marking response/check
 * output response of an SQL statement execution as well as methods for
 * processing this information.
 * @author Tala Ross(rsExpectedstal002)
 */
public class StatementOutput implements Serializable{
    
    /**
     * The amount of characters to pad/extend each column of a table by, beyond
     * its longest field character length.
     */
    public static final int COLUMN_PADDING = 3;
    
    /**
     * The main marking/output check message at the top of the output String.
     */
    private String message;
    
    /**
     * The introduction message above the expected table output.
     */
    private String tblIntroExpected;
    
    /**
     * The array of format Strings for each expected table column, where the
     * array entry i gives the column String format for column i+1.
     */
    private String [] tblFormatsExpected;
    
    /**
     * The array of column labels for each expected table column, where the
     * array entry i gives the column label for column i+1.
     */
    private String [] tblHeadingsExpected;
    
    /**
     * The 2D list of of table entries for the expected table, where the
     * entry i,j gives the entry for row i+1 and column j+1.
     */
    private ArrayList<String []> tblDataExpected;
    
    /**
     * The total character width of each row in the expected table output.
     */
    private int totalWidthExpected;
    
    /**
     * The introduction message above the student table output.
     */
    private String tblIntroStudent;
    
    /**
     * The array of format Strings for each student table column, where the
     * array entry i gives the column String format for column i+1.
     */
    private String [] tblFormatsStudent;
    
    /**
     * The array of label for each student table column, where the
     * array entry i gives the column label for column i+1.
     */
    private String [] tblHeadingsStudent;
    
    /**
     * The 2D list of of table entries for the student table, where the
     * entry i,j gives the entry for row i+1 and column j+1.
     */
    private ArrayList<String []> tblDataStudent;
    
    /**
     * The total character width of each row in the student table output.
     */
    private int totalWidthStudent;
    
    /**
     * Creates a StatementOutput object using given inputs, which stores
     * information for all messages and both table outputs.
     * This is typically used for storing complete question marking output.
     * @param message The main marking/output check message.
     * @param tblIntroExpected The introduction message above the expected table output.
     * @param rsExpected The ResultSet representing the expected output.
     * @param tblIntroStudent The introduction message above the student table output.
     * @param rsStudent The ResultSet representing the student output.
     * @throws SQLException 
     */
    public StatementOutput(String message, String tblIntroExpected, ResultSet rsExpected, String tblIntroStudent, ResultSet rsStudent) throws SQLException {
        this.message = message;
        this.tblIntroExpected = tblIntroExpected;
        this.tblIntroStudent = tblIntroStudent;
        
        //Construct expected arrays:
        rsExpected.beforeFirst();
        int noColumnsExpected = rsExpected.getMetaData().getColumnCount();
        int [] maxColWidths = new int [noColumnsExpected];
        
        //Get headings:
        tblHeadingsExpected = new String [noColumnsExpected];
        for (int i=1; i<=noColumnsExpected; i++) { 
            tblHeadingsExpected[i-1] = rsExpected.getMetaData().getColumnName(i);
            maxColWidths[i-1] = tblHeadingsExpected[i-1].length();   //get initial max width value
        }

        //Get row entries:
        tblDataExpected = new ArrayList<>();
        while(rsExpected.next()) {  //each row 
            String [] temp = new String [noColumnsExpected];
            for (int i=1; i<=noColumnsExpected; i++) {   //each field in row
                temp[i-1] = "" + rsExpected.getObject(i);
                if (temp[i-1].length()>maxColWidths[i-1]) { //update max column width
                    maxColWidths[i-1] = temp[i-1].length();
                }
            }
            tblDataExpected.add(temp);
        }   //end of expected output
        
        //Get formats:
        tblFormatsExpected = new String [noColumnsExpected];
        totalWidthExpected = 0;
        for (int i=0; i<noColumnsExpected; i++) {
            totalWidthExpected+= maxColWidths[i] + COLUMN_PADDING;
            tblFormatsExpected[i] = "%-" + (maxColWidths[i]+ COLUMN_PADDING) + "s";   //gives for example "%-11s"
        }
        
        
        //Construct student arrays:
        rsStudent.beforeFirst();
        int noColumnsStudent = rsStudent.getMetaData().getColumnCount();
        int [] maxColWidths2 = new int [noColumnsStudent];
        
        //Get headings:
        tblHeadingsStudent = new String [noColumnsStudent];
        for (int i=1; i<=noColumnsStudent; i++) { 
            tblHeadingsStudent[i-1] = rsStudent.getMetaData().getColumnName(i);
            maxColWidths2[i-1] = tblHeadingsStudent[i-1].length();   //get initial max width value
        }

        //Get row entries:
        tblDataStudent = new ArrayList<>();
        while(rsStudent.next()) {  //each row 
            String [] temp = new String [noColumnsStudent];
            for (int i=1; i<=noColumnsStudent; i++) {   //each field in row
                temp[i-1] = "" + rsStudent.getObject(i);
                if (temp[i-1].length()>maxColWidths2[i-1]) { //update max column width
                    maxColWidths2[i-1] = temp[i-1].length();
                }
            }
            tblDataStudent.add(temp);
        }   //end of student output
        
        //Get formats:
        tblFormatsStudent = new String [noColumnsStudent];
        totalWidthStudent = 0;
        for (int i=0; i<noColumnsStudent; i++) {
            totalWidthStudent+= maxColWidths2[i] + COLUMN_PADDING;
            tblFormatsStudent[i] = "%-" + (maxColWidths2[i]+ COLUMN_PADDING) + "s";   //gives for example "%-11s"
        }
    }
    
    /**
     * Creates a StatementOutput object using given inputs, which stores
     * information for the main message, student output messages and student
     * outputs.
     * This is typically used for storing check output information for update
     * statements.
     * @param message The main marking/output check message.
     * @param tblIntroStudent The introduction message above the student table output.
     * @param rsStudent The ResultSet representing the student output.
     * @throws SQLException 
     */
    public StatementOutput(String message, String tblIntroStudent, ResultSet rsStudent) throws SQLException {
        this.message = message;
        this.tblIntroStudent = tblIntroStudent;
        
        //Construct student arrays:
        rsStudent.beforeFirst();
        int noColumnsStudent = rsStudent.getMetaData().getColumnCount();
        int [] maxColWidths2 = new int [noColumnsStudent];
        
        //Get headings:
        tblHeadingsStudent = new String [noColumnsStudent];
        for (int i=1; i<=noColumnsStudent; i++) { 
            tblHeadingsStudent[i-1] = rsStudent.getMetaData().getColumnName(i);
            maxColWidths2[i-1] = tblHeadingsStudent[i-1].length();   //get initial max width value
        }

        //Get row entries:
        tblDataStudent = new ArrayList<>();
        while(rsStudent.next()) {  //each row 
            String [] temp = new String [noColumnsStudent];
            for (int i=1; i<=noColumnsStudent; i++) {   //each field in row
                temp[i-1] = "" + rsStudent.getObject(i);
                if (temp[i-1].length()>maxColWidths2[i-1]) { //update max column width
                    maxColWidths2[i-1] = temp[i-1].length();
                }
            }
            tblDataStudent.add(temp);
        }   //end of student output
        
        //Get formats:
        tblFormatsStudent = new String [noColumnsStudent];
        totalWidthStudent = 0;
        for (int i=0; i<noColumnsStudent; i++) {
            totalWidthStudent+= maxColWidths2[i] + COLUMN_PADDING;
            tblFormatsStudent[i] = "%-" + (maxColWidths2[i]+ COLUMN_PADDING) + "s";   //gives for example "%-11s"
        }
    }
    
    /**
     * Creates a StatementOutput object using given inputs, which stores
     * only student output messages and student outputs.
     * This is typically used for storing student mark information for sending
     * to the lecturer-end.
     * @param tblIntroStudent The introduction message above the table output.
     * @param rsStudent The ResultSet representing the output.
     * @throws SQLException 
     */
    public StatementOutput(String tblIntroStudent, ResultSet rsStudent) throws SQLException {
        this.tblIntroStudent = tblIntroStudent;
        
        //Construct student arrays:
        rsStudent.beforeFirst();
        int noColumnsStudent = rsStudent.getMetaData().getColumnCount();
        int [] maxColWidths2 = new int [noColumnsStudent];
        
        //Get headings:
        tblHeadingsStudent = new String [noColumnsStudent];
        for (int i=1; i<=noColumnsStudent; i++) { 
            tblHeadingsStudent[i-1] = rsStudent.getMetaData().getColumnName(i);
            maxColWidths2[i-1] = tblHeadingsStudent[i-1].length();   //get initial max width value
        }

        //Get row entries:
        tblDataStudent = new ArrayList<>();
        while(rsStudent.next()) {  //each row 
            String [] temp = new String [noColumnsStudent];
            for (int i=1; i<=noColumnsStudent; i++) {   //each field in row
                temp[i-1] = "" + rsStudent.getObject(i);
                if (temp[i-1].length()>maxColWidths2[i-1]) { //update max column width
                    maxColWidths2[i-1] = temp[i-1].length();
                }
            }
            tblDataStudent.add(temp);
        }   //end of student output
        
        //Get formats:
        tblFormatsStudent = new String [noColumnsStudent];
        totalWidthStudent = 0;
        for (int i=0; i<noColumnsStudent; i++) {
            totalWidthStudent+= maxColWidths2[i] + COLUMN_PADDING;
            tblFormatsStudent[i] = "%-" + (maxColWidths2[i]+ COLUMN_PADDING) + "s";   //gives for example "%-11s"
        }
    }
    
    /**
     * Creates a StatementOutput object using given inputs, which stores
     * only the student table output.
     * This is typically used for storing check output information for query
     * statements.
     * @param rsStudent The ResultSet representing the student output.
     * @throws SQLException 
     */
    public StatementOutput(ResultSet rsStudent) throws SQLException {
        //Construct student arrays:
        rsStudent.beforeFirst();
        int noColumnsStudent = rsStudent.getMetaData().getColumnCount();
        int [] maxColWidths2 = new int [noColumnsStudent];
        
        //Get headings:
        tblHeadingsStudent = new String [noColumnsStudent];
        for (int i=1; i<=noColumnsStudent; i++) { 
            tblHeadingsStudent[i-1] = rsStudent.getMetaData().getColumnName(i);
            maxColWidths2[i-1] = tblHeadingsStudent[i-1].length();   //get initial max width value
        }

        //Get row entries:
        tblDataStudent = new ArrayList<>();
        while(rsStudent.next()) {  //each row 
            String [] temp = new String [noColumnsStudent];
            for (int i=1; i<=noColumnsStudent; i++) {   //each field in row
                temp[i-1] = "" + rsStudent.getObject(i);
                if (temp[i-1].length()>maxColWidths2[i-1]) { //update max column width
                    maxColWidths2[i-1] = temp[i-1].length();
                }
            }
            tblDataStudent.add(temp);
        }   //end of student output
        
        //Get formats:
        tblFormatsStudent = new String [noColumnsStudent];
        totalWidthStudent = 0;
        for (int i=0; i<noColumnsStudent; i++) {
            totalWidthStudent+= maxColWidths2[i] + COLUMN_PADDING;
            tblFormatsStudent[i] = "%-" + (maxColWidths2[i]+ COLUMN_PADDING) + "s";   //gives for example "%-11s"
        }
    }
    
    /**
     * Creates a StatementOutput object using given inputs, which stores
     * only the main message.
     * This is typically used for storing question marking output for non-easy
     * difficulty questions.
     * @param message The main marking/output check message.
     * @throws SQLException 
     */
    public StatementOutput(String message) throws SQLException {
        this.message = message;
    }
    
    /**
     * Gets the String formatted output, with the intro message first, then the
     * expected output and finally the student output, where these outputs are
     * each proceeded by their intro messages.
     * Each of these sections are only included if the exist.
     * @return The String formatted output.
     */
    public String getOutput() {
        String output = "";
        //Get top message:
        if (message!=null) {
            output = message
                    + "\n";
        }
        
        //Get expected table and its intro:
        if (tblIntroExpected!=null) {
            output+= tblIntroExpected
                + "\n";
        }
        if (tblDataExpected!=null) {
            output+= getExpectedTableString()
                + "\n";
        }
        
        //Get student table and its intro:
        if (tblIntroStudent!=null) {
            output+= tblIntroStudent
                + "\n";
        }
        if (tblDataStudent!=null) {
            output+= getStudentTableString();
        }
        
        return output;
    }
    
    /**
     * Gets the String formatted version of the expected table.
     * @return The String formatted version of the expected table.
     */
    private String getExpectedTableString() {
        return getTableString(tblFormatsExpected, tblHeadingsExpected, tblDataExpected, totalWidthExpected);
    }
    
    /**
     * Gets the String formatted version of the student table.
     * @return The String formatted version of the student table.
     */
    private String getStudentTableString() {
        return getTableString(tblFormatsStudent, tblHeadingsStudent, tblDataStudent, totalWidthStudent);
    }
    
    /**
     * Gets the String formatted version of the output table, based on the
     * given column formats, column labels and data entries.
     * @return The String formatted version of the output table.
     */
    private static String getTableString(String [] formats, String [] headings, ArrayList<String []> data, int totalWidth) {
        int noColumns = formats.length;
        
        //Get headings:
        String tblString = new String(new char[totalWidth-1]).replace("\0", "-") + "\n";   //adds a "---" bar
        for (int i=0; i<noColumns; i++) { 
            tblString+= String.format(formats[i], headings[i]);
        }
        tblString+= "\n" + new String(new char[totalWidth-1]).replace("\0", "-") + "\n";   //adds a "---" bar
        
        //Get data:
        while (!data.isEmpty()) {   //each row
            String [] rowArray = data.remove(0);
            String row = "";
            for (int i=0; i<noColumns; i++) {   //each field
                row+= String.format(formats[i], rowArray[i]);
            }
            tblString+= row
                    + "\n";
        }   //end of data
        
        return tblString;
    }
}

package kiwi.message;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Tala Ross(rsExpectedstal002)
 */
public class StatementOutput implements Serializable{
    
    public static final int COLUMN_PADDING = 3;
    
    private String message;
    
    private String tblIntroExpected;
    private String [] tblFormatsExpected;
    private String [] tblHeadingsExpected;
    private ArrayList<String []> tblDataExpected;
    private int totalWidthExpected;
    
    private String tblIntroStudent;
    private String [] tblFormatsStudent;
    private String [] tblHeadingsStudent;
    private ArrayList<String []> tblDataStudent;
    private int totalWidthStudent;

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
    
    public StatementOutput(String message) throws SQLException {
        this.message = message;
    }
    
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

    private String getExpectedTableString() {
        return getTableString(tblFormatsExpected, tblHeadingsExpected, tblDataExpected, totalWidthExpected);
    }

    private String getStudentTableString() {
        return getTableString(tblFormatsStudent, tblHeadingsStudent, tblDataStudent, totalWidthStudent);
    }
    
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

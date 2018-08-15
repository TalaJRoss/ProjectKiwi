package kiwilecturer;

import com.opencsv.CSVReader;
import java.sql.Statement;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains static lecturer functionality methods for uploading csv files.
 * @author Tala Ross(rsstal002)
 */
public class Lecturer {
    
    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";
    private static final String STUDENT_NO = "StudentNo";
    private static final String GRADE = "HighestGrade";
    
    
    //Main methods(public):
    
    /**
     * Uploads student information contained in the given csv file to the
     * students table in the database on the server.
     * Expected csv file format:
     * studentNo,highestGrade,noSubmissionsCompleted
     * @param csv The csv file containing student information.
     */
    public static void uploadStudents(File csv) {
        createTable("students", csv); 
    }
    
    /**
     * Uploads question-answer pairs contained in the given csv file to the
     * questions table in the database on the server.
     * Expected csv file format:
     * questionNo,question,answer,difficulty
     * @param csv The csv file containing question-answer pairs.
     */
    public static void uploadQuestions(File csv) {
        createTable("questions", csv);
    }
    
    /**
     * Creates tables on database to represent the query data contained in the
     * given csv files and uploads the contained information to the database on
     * the server.
     * Expected csv file format:
     * File name: nameOfTable.csv.
     * First line: "columnLabel columnType(length) isPrimaryKey(1/0)",...
     * This is comma-separated for each column.
     * Actual row entries: fields are comma-separated and enclosed in quotes
     * and lines are terminated by '\r\n'.
     * @param csvs Array of csv files containing query data.
     */
    public static void uploadQueryData(File [] csvs) {
        for (File file: csvs) {
            createTable(file.getName().substring(0, file.getName().lastIndexOf(".")), file);
        }
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to ascending student number.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * ascending student number.
     */
    public static String viewGradeAscStudent() {
        return getGrades(STUDENT_NO, ASCENDING);
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to descending student number.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * descending student number.
     */
    public static String viewGradeDescStudent() {
        return getGrades(STUDENT_NO, DESCENDING);
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to ascending grade.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * ascending grade.
     */
    public static String viewGradeAscGrade() {
        return getGrades(GRADE, ASCENDING);
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to descending grade.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * descending grade.
     */
    public static String viewGradeDescGrade() {
        return getGrades(GRADE, DESCENDING);
    }
    
    
    //Helper Methods(private):
    
    /**
     * Creates table in the database if table of given name doesn't exist and
     * loads information from given csv file into the table.
     * Expected csv format:
     * File name: nameOfTable.csv.
     * First line: "columnLabel columnType(length) isPrimaryKey(1/0)",...
     * This is comma-separated for each column.
     * For example:
     * 1. "studentNo varchar(10) 1","grade integer 0"
     * 2. "id integer 1","fullName varchar(50) 0"
     * Actual row entries: fields are comma-separated and enclosed in quotes
     * and lines are terminated by '\r\n'.
     * For example:
     * 1. abcabc001,90
     * 2. 001,"Jon Smith"
     * @param tblName Name of the table to create and update.
     * @param csv File containing row entries information to add to table.
     */
    private static void createTable(String tblName, File csv) {
        try {
            //read in csv column info:
            FileReader fileReader = new FileReader(csv);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] headings = csvReader.readNext();
            
            //get name of table:
            //String tblName = csv.getName().substring(0, csv.getName().lastIndexOf("."));
            
            //process headings and create sql create statement and load statement
            //load statement:
            String uploadStatement = "LOAD DATA INFILE \'"+csv.getName()+"\' INTO TABLE " + tblName
                    + " FIELDS OPTIONALLY ENCLOSED BY \'\"\' TERMINATED BY \',\' LINES TERMINATED BY \'\\r\\n\' IGNORE 1 ROWS";
            String cols= " (";
            //create statement
            String sql = "CREATE TABLE IF NOT EXISTS " + tblName + " (";
            String pkString= "PRIMARY KEY (";
            int noCol = 0;
            for (String cell : headings) {
                String [] info = cell.split(" ");
                String name = info[0];
                cols+= name + ", ";
                String type = info[1];
                boolean isPK = Integer.parseInt(info[2])==1;
                if (isPK) {
                    pkString+=name+", ";
                }
                sql+= name+" "+type+", ";
            }
            cols= cols.substring(0,cols.length()-2);
            uploadStatement+= cols+");";
            pkString= pkString.substring(0,pkString.length()-2);
            sql+=pkString+"))";
            
            fileReader.close();
            csvReader.close();
                    
            // create database connection
           Class.forName("com.mysql.cj.jdbc.Driver");
           Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");
            
            //create table
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            
            //load csv:
            //file must be in mysql database root directory (local not enabeled in ini)
            System.out.println(uploadStatement);
            st.executeUpdate(uploadStatement);
            
        }
         catch (ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(Lecturer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * Accesses the database on the server, gets table of student grades and
     * orders entries according to the given conditions. The ordering conditions
     * include:
     * 1. student number, ascending
     * 2. student number, descending
     * 3. highest grade, ascending
     * 4. highest grade, descending
     * @param column The column to order the table based on.
     * @param order The way to order table(ascending/descending).
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades.
     */
    private static String getGrades(String column, String order) {
        String toReturn= "";
        try {
            //Setup connection:
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            // get student output:
            Statement st= conn.createStatement();
            String sql= "SELECT StudentNo, HighestGrade FROM STUDENTS ORDER BY " + column + " " + order;
            ResultSet rs= st.executeQuery(sql);
            
            //put output in string:
            toReturn = "StudentNo\tHighestGrade\n";
            while(rs.next()) {  //column entries
                for (int i=1; i<=2; i++) {
                    toReturn+= rs.getObject(i) + "\t";
                }
                toReturn+="\n";
            }    
        }
        catch (SQLException ex) {   
            System.out.println(ex.toString());
        } 
        catch (ClassNotFoundException ex) {
            return "ERROR: Couldn't load database driver.";
        }
        
        return toReturn;
    }
    
}

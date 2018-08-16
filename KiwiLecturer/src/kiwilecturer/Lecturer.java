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

//TODO: get assignment deadline and no. submissions from lecturer
//TODO: update create students table to just require studentNo fields in csv and not require formated headings row
//TODO: update create questions table to not require formated headings row
/**
 * Contains static lecturer functionality methods for uploading csv files.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class Lecturer {
    
    
    //Constants for ordering grades table:
    
    /**
     * Ascending order createStatement command.
     */
    private static final String ASCENDING = "ASC";
    
    /**
     * Descending order createStatement command.
     */
    private static final String DESCENDING = "DESC";
    
    /**
     * Order according to student number.
     */
    private static final String STUDENT_NO = "StudentNo";
    
    /**
     * Order according to students' highest grades.
     */
    private static final String GRADE = "HighestGrade";
    
    /**
     * Indicates successful create/upload table from csv file.
     */
    private static final String SUCCESS = "Yayyy";
    
    /**
     * Indicates failed create/upload table from csv file.
     */
    private static final String FAIL = "Error";
    
    
    //Main functionality methods(public):
    
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
    private static String createTable(String tblName, File csv) {
        
        try {
            //read in csv column info:
            FileReader fileReader = new FileReader(csv);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] headings = csvReader.readNext();
            
            //process headings and create createStatement create statement and load statement:
            //load statement:
            String uploadStatement = "LOAD DATA INFILE \'"+csv.getName()+"\' INTO TABLE " + tblName
                    + " FIELDS OPTIONALLY ENCLOSED BY \'\"\' TERMINATED BY \',\' LINES TERMINATED BY \'\\r\\n\' IGNORE 1 ROWS";
            String columns = " (";
            String setString = "SET ";
            
            //create statement:
            String createStatement = "CREATE TABLE IF NOT EXISTS " + tblName + " (";
            String pkString = "PRIMARY KEY (";
            
            //get column info(name, type, if PK) from first csv row:
            for (String cell : headings) {  //each cell represents all info for 1 column
                String [] info = cell.split(" ");
                
                String name = info[0];  //name of column
                String type = info[1];  //datatype eg. integer/varchar(20)
                boolean isPK = Integer.parseInt(info[2])==1;    //1 if column is primary key
                
                if (isPK) {
                    pkString+=name+", ";    //list of primary keys for end of CREATE statement
                }
                columns+= "@" + name + ", ";  //specify columns for LOAD statement
                setString+= name + " = nullif(@" + name + ", ''), ";
                createStatement+= name+" "+type+", ";   //add column info to CREATE statement
            }
            
            //remove trailing ", " and add columns to LOAD statement:
            columns= columns.substring(0,columns.length()-2);
            uploadStatement+= columns+")";
            
            //remove trailing ", " and add set part to LOAD statement:
            setString= setString.substring(0,setString.length()-2);
            uploadStatement+= setString+";";
            
            //remove trailing ", " and add PKs to CREATE statement:
            pkString= pkString.substring(0,pkString.length()-2);
            createStatement+= pkString+"))";
            
            //clean up:
            fileReader.close();
            csvReader.close();
                    
            //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");
            
            //create table:
            Statement st = conn.createStatement();
            //System.out.println(createStatement);  //DEBUG
            st.executeUpdate(createStatement);
            System.out.println("Created table: " + tblName);
            
            //load csv:
            //file must be in mysql database root directory (local not enabeled in ini)
            //System.out.println(uploadStatement);  //DEBUG
            st.executeUpdate(uploadStatement);
            System.out.println("Uploaded data from csv to table: " + tblName);
            
            return SUCCESS;     //successful upload
        }
        
        catch (ClassNotFoundException e) {
            System.out.println("Error: Problem connecting to database/loading driver: " + csv.getName() + ".");
            System.out.println(e);
            return FAIL + ": Problem connecting to database/loading driver: " + csv.getName() + ".";
        }
        catch (IOException e) {
            System.out.println("Error: Problem reading file: " + csv.getName() + ".");
            System.out.println(e);
            return FAIL + ": Problem reading file: " + csv.getName() + ".";
        }
        catch (SQLException  e) {
            System.out.println("Error: Problem creating table/loading csv to database: " + csv.getName() + ".");
            System.out.println(e);
            return FAIL + ": Problem creating table/loading csv to database: " + csv.getName() + ".";
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
        
        try {
            //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            //get student output: ordering based on input
            Statement st = conn.createStatement();
            String selectStatement = "SELECT StudentNo, HighestGrade FROM STUDENTS ORDER BY " + column + " " + order;
            ResultSet rs = st.executeQuery(selectStatement);
            
            //put output in string:
            String toReturn = "StudentNo\tHighestGrade\n";
            while(rs.next()) {  //row entries
                for (int i=1; i<=2; i++) {  //column entries
                    toReturn+= rs.getObject(i) + "\t";
                }
                toReturn+="\n"; //next row
            }
            
            return toReturn;    
        }
        
        catch (SQLException e) { 
            System.out.println("Error: Problem reading grades from database.");
            System.out.println(e);
            return "Error: Problem reading grades from database.";
        } 
        catch (ClassNotFoundException e) {
            System.out.println("Error: Problem connecting to database/loading driver.");
            System.out.println(e);
            return "Error: Problem connecting to database/loading driver.";
        }
    }
    
}

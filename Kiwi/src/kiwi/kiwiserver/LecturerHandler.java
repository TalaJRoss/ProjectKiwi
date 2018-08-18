/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.kiwiserver;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.LecturerMessage;

/**
 *
 * @author Tala Ross(rsstal002)
 */
final class LecturerHandler extends Thread{
    //Constants for ordering grades table:
    
    private static final String DB_PATH = "C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Data\\kiwidb\\";
    
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
    
    
    
    /**
     * User name for database connection.
     */
    //TODO: change to "lecturer"
    public static final String USER_NAME = "root";
    
    /**
     * Password for database connection.
     */
    //TODO: change to secure pwd
    public static final String PASSWORD = "mysql";
    
    /**
     * Socket on client who's communications this thread is managing.
     */
    private Socket lecturerSocket;
    
    /**
     * Input stream for server socket (reader) which reads in client messages.
     */
    private ObjectInputStream reader;   
    
    /**
     * Output stream for server socket (writer) which writes messages to client.
     */
    private ObjectOutputStream writer;
    
    /**
     * Connection to DB.
     */
    private Connection conn;
    
    //Constructor:
    
    /**
     * Creates new client handler thread to relay message and process status
     * updates and hashtag join/leaves.
     * @param server server that client connects to and thread services
     * @param clientSocket socket on client side linking server to client
     */
    public LecturerHandler(Socket lecturerSocket) {
        this.lecturerSocket = lecturerSocket;
        try {
            OutputStream os= lecturerSocket.getOutputStream();
            writer= new ObjectOutputStream(os);   //MESSAGE
            InputStream is= lecturerSocket.getInputStream();
            reader= new ObjectInputStream(is); //MESSAGE
        } 
        catch (IOException ex) {
            Logger.getLogger(LecturerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
    public void connectToDB() throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", USER_NAME, PASSWORD);
            System.out.println("conn: " + conn);
            LecturerMessage m = new LecturerMessage(null, null, "", LecturerMessage.CMD_CONNECT, LecturerMessage.RES_SUCCESS);
            System.out.println("DEBUG: "+m.getResponse());
            writer.writeObject(m);
        } catch (SQLException | ClassNotFoundException ex) {
            writer.writeObject(new LecturerMessage(null, null, "", LecturerMessage.CMD_CONNECT, LecturerMessage.RES_FAIL));
        }
    }
    
    @Override
    public void run() {
        
        try {
            connectToDB();
        } catch (IOException ex) {
            Logger.getLogger(LecturerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            LecturerMessage m = (LecturerMessage) reader.readObject();   //MESSAGE
            String command;
            
            while (m!= null)
            {
                command = m.getCmd();
                switch (command) 
                {
                    case LecturerMessage.CMD_CONNECT:  //retry connect
                        connectToDB();
                        break;
                    case LecturerMessage.CMD_GRADE_ALPH:   //view grades
                        viewGradeAscStudent();
                        break;
                    case LecturerMessage.CMD_GRADE_DESC:   //view grades
                        viewGradeDescGrade();
                        break;
                    case LecturerMessage.CMD_UPLOAD_STUDENTS: //upload file
                        uploadStudents(m.getCsvFile());
                        break;
                    case LecturerMessage.CMD_UPLOAD_QUESTIONS: //upload file
                        uploadQuestions(m.getCsvFile());
                        break;
                    case LecturerMessage.CMD_UPLOAD_QUERY: //upload file
                        uploadQueryData(m.getCsvFiles(), m.getFileNames());
                        break;
                    default:    //nothing else
                        break;
                }
                writer.flush();
                m= (LecturerMessage) reader.readObject();
            }
            lecturerSocket.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(LecturerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Main functionality methods(public):
    
    /**
     * Uploads student information contained in the given csv file to the
     * students table in the database on the server.
     * Expected csv file format:
     * studentNo,highestGrade,noSubmissionsCompleted
     * @param csv The csv file containing student information.
     */
    public void uploadStudents(byte [] csv) {
        createTable("students", csv); 
    }
    
    /**
     * Uploads question-answer pairs contained in the given csv file to the
     * questions table in the database on the server.
     * Expected csv file format:
     * questionNo,question,answer,difficulty
     * @param csv The csv file containing question-answer pairs.
     */
    public void uploadQuestions(byte [] csv) {
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
    public void uploadQueryData(ArrayList<byte []> csvs, String [] names) {
        for (int i=0; i<csvs.size(); i++) {
            createTable(names[i], csvs.get(i));
        }
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to ascending student number.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * ascending student number.
     */
    public void viewGradeAscStudent() throws IOException {
        writer.writeObject(new LecturerMessage(null, null, getGrades(STUDENT_NO, ASCENDING), LecturerMessage.CMD_GRADE_ALPH, LecturerMessage.RES_SUCCESS));
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to descending grade.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * descending grade.
     */
    public void viewGradeDescGrade() throws IOException {
        writer.writeObject(new LecturerMessage(null, null, getGrades(GRADE, DESCENDING), LecturerMessage.CMD_GRADE_DESC, LecturerMessage.RES_SUCCESS));
    }
    
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
    private String createTable(String tblName, byte [] bytes) {
        
        try {
            //save file:
            try (FileOutputStream fos = new FileOutputStream(DB_PATH + tblName + ".csv")) {
                fos.write(bytes);
            }
        
            //read in csv column info:
            FileReader fileReader = new FileReader(new File(DB_PATH + tblName + ".csv"));
            CSVReader csvReader = new CSVReader(fileReader);
            String[] headings = csvReader.readNext();
            
            //process headings and create createStatement create statement and load statement:
            //load statement:
            String uploadStatement = "LOAD DATA INFILE \'"+tblName+".csv\' INTO TABLE " + tblName
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
        catch (IOException e) {
            System.out.println("Error: Problem reading file: " + tblName + ".");
            System.out.println(e);
            return FAIL + ": Problem reading file: " + tblName + ".";
        }
        catch (SQLException  e) {
            System.out.println("Error: Problem creating table/loading csv to database: " + tblName + ".");
            System.out.println(e);
            return FAIL + ": Problem creating table/loading csv to database: " + tblName + ".";
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
    private String getGrades(String column, String order) {
        
        try {

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
    }
    
}

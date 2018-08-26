/*
 * Server-side handling of the Lecturer
 */
package kiwi.kiwiserver;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kiwi.message.*;

/**
 *
 * @author Tala Ross(rsstal002)
 */
final class LecturerHandler extends Thread{
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
    private static final String SUCCESS = "Success";
    
    /**
     * Indicates failed create/upload table from csv file.
     */
    private static final String FAIL = "Error";
    
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
            OutputStream os = lecturerSocket.getOutputStream();
            writer = new ObjectOutputStream(os);   //MESSAGE
            InputStream is = lecturerSocket.getInputStream();
            reader = new ObjectInputStream(is); //MESSAGE
        } 
        catch (IOException e) {
            System.out.println("Problem setting up lecturer socket.");
            System.out.println(e);
        }
    }
    
    
    
    //Setup database connection: requires mysql "KiwiDB" named database on host with user="root" and pass="mysql"
    public void connectToDB() throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", ServerStartup.ROOT_NAME, ServerStartup.ROOT_PWD);
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_CONNECT, null, LecturerMessage.RESP_SUCCESS));
        } catch (SQLException | ClassNotFoundException ex) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_CONNECT, null, LecturerMessage.RESP_FAIL_CONNECT));
        }
    }
    
    @Override
    public void run() {
        try {
            connectToDB();
        } 
        catch (IOException e) {
            System.out.println("Problem connecting to database on intial run.");
            System.out.println(e);
        }
        try {
            LecturerMessage m;
            int command;
            
            ONLINE:
            while ((m = (LecturerMessage) reader.readObject())!= null)
            {
                command = m.getCmd();
                switch (command) 
                {
                    case LecturerMessage.CMD_ASSIGNMENT_INFO: //upload assignment info (noSubmissions, deadline etc.)
                        uploadAssignmentInfo((AssignmentInfo)m.getBody());
                        break;
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
                        uploadStudents(((CSVFiles)m.getBody()).getCsvFile());
                        break;
                    case LecturerMessage.CMD_UPLOAD_QUESTIONS: //upload file
                        uploadQuestions(((CSVFiles)m.getBody()).getCsvFile());
                        break;
                    case LecturerMessage.CMD_UPLOAD_QUERY: //upload file
                        uploadQueryData(((CSVFiles)m.getBody()).getCsvFiles(), ((CSVFiles)m.getBody()).getFileNames());
                        break;
                    case LecturerMessage.CMD_UPLOAD_SCHEMA: //upload the schema for the query data
                        uploadSchema((Schema)m.getBody());
                        break;
                    case LecturerMessage.CMD_UPDATE_DEADLINE: //update the assignment submission deadline for a student
                        updateDeadline((UpdateInfo)m.getBody());
                        break;
                    case LecturerMessage.CMD_RESET_STUDENTS: //reset the students table
                        resetStudents();
                        break;
                    case LecturerMessage.CMD_RESET_QUESTIONS: //reset the questions table
                        resetQuestions();
                        break;
                    case LecturerMessage.CMD_RESET_QUERY_DATA: //reset the questions table
                        resetQueryData();
                        break;                        
                    case LecturerMessage.CMD_CLOSE: //close sockets
                        closeConnection();
                        break ONLINE;
                    default:    //nothing else
                        break;
                }
            }
            lecturerSocket.close();
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Problem processing lecturer message.");
            System.out.println(e);
        }
    }
    
    //Main functionality methods(public):
    
    /**
     * Create a table with the noSubmissions, noQuestions, DeadlineDate, DeadlineTime and whether it is a ClosedPrac or not.
     * @param assignmentInfo an AssignmentInfo object with the details of an assignment.
     * @throws IOException 
     */
    public void uploadAssignmentInfo(AssignmentInfo assignmentInfo) throws IOException{
        
        String tblName = "AssignmentInfo";
        
        //drop table statement:
        String dropStatement = "DROP TABLE IF EXISTS " + tblName;
        
        //create table statement:
        String createStatement = "CREATE TABLE AssignmentInfo(" +
                                 "ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                                 "NoSubmissions INT, " +
                                 "NoQuestions INT, " +
                                 "DeadlineDate DATE, " +
                                 "DeadlineTime TIME, " +
                                 "ClosedPrac BOOL);";
 
        //insert into table statement:
        String closedPrac = "FALSE";
        if(assignmentInfo.isClosedPrac()){
            closedPrac = "TRUE";
        }
        
        String insertStatement =  "INSERT INTO AssignmentInfo VALUES (1, " +
                                   assignmentInfo.getNoSubmissions()+ ", "+
                                   assignmentInfo.getNoQuestions() + ", \""+
                                   assignmentInfo.getDate() + "\", \"" +
                                   assignmentInfo.getTime() + "\", " +
                                   closedPrac+ ");";
        
        System.out.println("Drop statement:\n"+dropStatement);
        System.out.println("Create statement:\n"+createStatement);
        System.out.println("Insert statement:\n"+insertStatement);
        
        Statement st;
        
        try {
            st = conn.createStatement();
            
            //drop table:
            st.executeUpdate(dropStatement);
            System.out.println("Dropped table if exists: " + tblName);
            
            //create table:
            st.executeUpdate(createStatement);
            System.out.println("Created table: "+tblName);
            
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            try {
                st.executeUpdate(insertStatement);
            }
            catch (SQLException e) {
                conn.rollback(sp);
                conn.setAutoCommit(true);
                writer.writeObject(new LecturerMessage(LecturerMessage.CMD_ASSIGNMENT_INFO, null, LecturerMessage.RESP_FAIL_CONNECT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);   //end transaction
            
            
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_ASSIGNMENT_INFO, null, LecturerMessage.RESP_SUCCESS));
                
            
        } catch (SQLException | IOException ex) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_ASSIGNMENT_INFO, null, LecturerMessage.RESP_FAIL_CONNECT));
        }
    }
    
    /**
     * Save the schema for the query data to the DB directory.
     * @param schema the Schema object containing the bytes for the image.
     * @throws IOException 
     */
    public void uploadSchema(Schema schema) throws IOException{
        //save schema:
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(ServerStartup.DB_PATH + "schema.jpg");
            fos.write(schema.getSchemaImg());
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_SCHEMA, null, LecturerMessage.RESP_SUCCESS));
        } catch (IOException ex) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_SCHEMA, null, LecturerMessage.RESP_FAIL_CONNECT));
            System.out.println("Error: problem saving schema image");
        } 
    }
    
    //TODO:check studentNumber of format aaaaaa000
    /**
     * Uploads student information contained in the given csv file to the
     * students table in the database on the server.
     * Expected csv file format:
     * studentNo,highestGrade,noSubmissionsCompleted
     * @param csv The csv file containing student information.
     */
    public void uploadStudents(byte [] csv) throws IOException {
        try {
            //Save students file to DB root:
            try (FileOutputStream fos = new FileOutputStream(ServerStartup.DB_PATH + "students.csv")) {
                fos.write(csv);
            }
            
            Statement st = conn.createStatement();
            ResultSet rs;
            
            //Create students table:
            String createStatement = "CREATE TABLE IF NOT EXISTS students (" +
                                        "StudentNo varchar(9) NOT NULL, " +
                                        "HighestGrade double DEFAULT NULL, " +
                                        "NoSubmissionsCompleted int(11) DEFAULT NULL, " +
                                        "DeadlineDate date DEFAULT NULL, " +
                                        "DeadlineTime time DEFAULT NULL, " +
                                        "PRIMARY KEY (StudentNo) " +
                                        ") ENGINE=InnoDB;";
            st.executeUpdate(createStatement);
            
            //Get deadline date and time:
            String deadlineStatement = "SELECT DeadlineDate, DeadlineTime FROM AssignmentInfo WHERE ID=1;";
            try {
                rs = st.executeQuery(deadlineStatement);
            }
            catch (SQLException e) {    //lecturer hasn't loaded assignment info yet (including deadline info)
                System.out.println("Error: Couldn't load students csv. Assignment info hasn't been loaded.");
                System.out.println(e);
                writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_STUDENTS, null, LecturerMessage.RESP_FAIL_NULL));
                return;
            }
            rs.next();
            Date deadlineDate = rs.getDate("DeadlineDate");
            Time deadlineTime = rs.getTime("DeadlineTime");
            
            /*Upload students to table with initial field entries as 0 for submissions
            completed and highest grade and with deadline fields from assignment info table:
            */
            String uploadStatement = "LOAD DATA INFILE 'students.csv' INTO TABLE students " +
                                        "FIELDS OPTIONALLY ENCLOSED BY '\"' " +
                                        "TERMINATED BY ',' " +
                                        "LINES TERMINATED BY '\\r\\n' " +
                                        "IGNORE 1 ROWS " +
                                        "(studentNo) " +
                                        "SET HighestGrade=0, " +
                                        "NoSubmissionsCompleted=0, " +
                                        "DeadlineDate='" + deadlineDate.toString() + "', " +
                                        "DeadlineTime='" + deadlineTime.toString() + "';";
            
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            try { 
                st.executeUpdate(uploadStatement);
            }
            catch (SQLException e) {    //csv file format wrong
                conn.rollback(sp);
                conn.setAutoCommit(true);
                System.out.println("Error: Problem loading csv to database: students.");
                System.out.println(e);
                writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_STUDENTS, null, LecturerMessage.RESP_FAIL_INPUT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);   //end transaction
            
            //Successfully loaded table:
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_STUDENTS, null, LecturerMessage.RESP_SUCCESS));
            
        }
        catch (SQLException e) {
            System.out.println("Error: Problem loading csv to database: students.");
            System.out.println(e);
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_STUDENTS, null, LecturerMessage.RESP_FAIL_CONNECT));
        }
    }
    
    
    //TODO: check difficulty=1,2 or 3 and type=select, arithmetic or update
    /**
     * Uploads question-answer pairs contained in the given csv file to the
     * questions table in the database on the server.
     * Expected csv file format:
     * questionNo,question,answer,difficulty
     * @param csv The csv file containing question-answer pairs.
     */
    public void uploadQuestions(byte [] csv) throws IOException {
        try {
            //Save questions file to DB root:
            try (FileOutputStream fos = new FileOutputStream(ServerStartup.DB_PATH + "questions.csv")) {
                fos.write(csv);
            }
            
            Statement st = conn.createStatement();
            
            //Create questions table:
            String createStatement = "CREATE TABLE IF NOT EXISTS questions (" +
                                        "QuestionNo int(11) NOT NULL AUTO_INCREMENT, " +
                                        "Question varchar(500) DEFAULT NULL, " +
                                        "Answer varchar(500) DEFAULT NULL, " +
                                        "Difficulty int(11) DEFAULT NULL, " +
                                        "Type varchar(20) DEFAULT NULL, " +
                                        "Problem varchar(20) DEFAULT NULL, " +
                                        "PRIMARY KEY (QuestionNo) " +
                                        ") ENGINE=InnoDB;";
            st.executeUpdate(createStatement);
            
            /*Upload questions to table with initial field entries as null for
            the problem field and question number auto incrementing:
            */
            String uploadStatement = "LOAD DATA INFILE 'questions.csv' INTO TABLE questions " +
                                        "FIELDS OPTIONALLY ENCLOSED BY '\"' " +
                                        "TERMINATED BY ',' " +
                                        "LINES TERMINATED BY '\\r\\n' " +
                                        "IGNORE 1 ROWS " +
                                        "(Question, Answer, Difficulty, Type) " +
                                        "SET Problem=null;";
            
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            try { 
                st.executeUpdate(uploadStatement);
            }
            catch (SQLException e) {    //csv file format wrong
                conn.rollback(sp);
                conn.setAutoCommit(true);
                System.out.println("Error: Problem loading csv to database: questionss.");
                System.out.println(e);
                writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUESTIONS, null, LecturerMessage.RESP_FAIL_INPUT));
                return;
            }
            conn.commit();
            conn.setAutoCommit(true);   //end transaction
            
            /*Increment question numbers(not auto incremented since this
            causes incorrect numbers if incorrect input is given first):
            */
            String incrementStatement = "ALTER TABLE questions MODIFY COLUMN QuestionNo INT AUTO_INCREMENT;";
            st.executeUpdate(incrementStatement);
            
            //Successfully loaded table:
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUESTIONS, null, LecturerMessage.RESP_SUCCESS));
            
        }
        catch (SQLException e) {
            System.out.println("Error: Problem loading csv to database: students.");
            System.out.println(e);
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUESTIONS, null, LecturerMessage.RESP_FAIL_CONNECT));
        }
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
    public void uploadQueryData(ArrayList<byte []> csvs, String [] names) throws IOException {
        
        String flush = "FLUSH PRIVILEGES;";
        
        for (int i=0; i<csvs.size(); i++) {
            String response = createTable(names[i], csvs.get(i));
            if (response.equals(SUCCESS)) {
                writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUERY, null, LecturerMessage.RESP_SUCCESS));
                
                //grant students access to all successfully uploaded query data tables
                String studentPermissions = "GRANT SELECT, INSERT, UPDATE, DELETE ON "+ServerStartup.DB_NAME+"."+names[i]+" TO '"+ServerStartup.STUDENT_NAME+"'@'localhost';";
                String[] cmd = new String[]{ServerStartup.MYSQL_PATH,ServerStartup.DB_NAME,"--user=" + ServerStartup.ROOT_NAME,"--password=" + ServerStartup.ROOT_PWD,"-e",studentPermissions};
                Runtime.getRuntime().exec(cmd);
                String[] cmdFlush = new String[]{ServerStartup.MYSQL_PATH,ServerStartup.DB_NAME,"--user=" + ServerStartup.ROOT_NAME,"--password=" + ServerStartup.ROOT_PWD,"-e",flush};
                Runtime.getRuntime().exec(cmdFlush);
                System.out.println("Granted student permissions for "+names[i]);
                
            } else {
                writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPLOAD_QUERY, null, LecturerMessage.RESP_FAIL_CONNECT));
            }
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
        String response = getGrades(STUDENT_NO, ASCENDING);
        if (response.equals(FAIL)) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_GRADE_ALPH, null, LecturerMessage.RESP_FAIL_CONNECT));
        } else {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_GRADE_ALPH, new Grades(response), LecturerMessage.RESP_SUCCESS));
        }
        
    }
    
    /**
     * Accesses the database on the server and returns table of student grades
     * ordered according to descending grade.
     * @return string representation of the the table containing student
     * numbers mapped to the student's highest grades ordered according to
     * descending grade.
     */
    public void viewGradeDescGrade() throws IOException {
        String response = getGrades(GRADE, DESCENDING);
        if (response.equals(FAIL)) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_GRADE_DESC, null, LecturerMessage.RESP_FAIL_CONNECT));
        } else {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_GRADE_DESC, new Grades(response), LecturerMessage.RESP_SUCCESS));
        }
    }
    
    /**
     * Updates an individual student's deadline for submitting the assignment by performing an SQL update on the Students table.
     * @param updateInfo the object containing the details (studentNo, date and time) for the update.
     */
    
    public void updateDeadline(UpdateInfo updateInfo) throws IOException {
        
        //update statement
        String updateStatement = "UPDATE students "+
                                 "SET DeadlineDate = '"+updateInfo.getDate()+"', DeadlineTime = ' "+updateInfo.getTime()+"'"+
                                  "WHERE StudentNo = '"+updateInfo.getStudentNo()+"';";
        
        System.out.println("Deadline update statement: "+updateStatement);
        
        Statement st;
        
        try {
             st = conn.createStatement();
             st.executeUpdate(updateStatement);
             writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPDATE_DEADLINE, null, LecturerMessage.RESP_SUCCESS));
             st.close();
        } catch (SQLException ex) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_UPDATE_DEADLINE, null, LecturerMessage.RESP_FAIL_CONNECT));
        }
        
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
            try (FileOutputStream fos = new FileOutputStream(ServerStartup.DB_PATH + tblName + ".csv")) {
                fos.write(bytes);
            }
        
            //read in csv column info:
            FileReader fileReader = new FileReader(new File(ServerStartup.DB_PATH + tblName + ".csv"));
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
            createStatement+= pkString+")) ENGINE=InnoDB;";
            
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
            conn.setAutoCommit(false);  //start transaction
            Savepoint sp = conn.setSavepoint();
            try { 
                st.executeUpdate(uploadStatement);
            }
            catch (SQLException e) {
                conn.rollback(sp);
                conn.setAutoCommit(true);
                System.out.println("Error: Problem loading csv to database: " + tblName + ".");
                System.out.println(e);
                return FAIL + ": Problem loading csv to database: " + tblName + ".";
            }
            conn.commit();
            conn.setAutoCommit(true);   //end transaction
            System.out.println("Uploaded data from csv to table: " + tblName);
            
            return SUCCESS;     //successful upload
        }
        catch (IOException e) {
            System.out.println("Error: Problem reading file: " + tblName + ".");
            System.out.println(e);
            return FAIL + ": Problem reading file: " + tblName + ".";
        }
        catch (SQLException  e) {
            System.out.println("Error: Problem creating table in database: " + tblName + ".");
            System.out.println(e);
            return FAIL + ": Problem creating table in database: " + tblName + ".";
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
            return FAIL;
        } 
    }
    /**
     * Drops the Students table, if it exists, in the database.
     * @return success/fail response depending on Students table drop.
     * @throws IOException 
     */
    private String resetStudents() throws IOException{
        
        String dropStatement = "DROP TABLE IF EXISTS Students;";
        
        Statement st;
        
        try {
            st = conn.createStatement();
            st.execute(dropStatement);
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_RESET_STUDENTS, null, LecturerMessage.RESP_SUCCESS));
            st.close();
            return SUCCESS; //drop successful
        } catch (SQLException ex) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_RESET_STUDENTS, null, LecturerMessage.RESP_FAIL_CONNECT));
            System.out.println("Error: Problem dropping Students table.");
            System.out.println(ex);
            return FAIL;
        }
    
    }
    
    /**
     * Drops the Questions table, if it exists, in the database.
     * @return success/fail response depending on Students table drop.
     * @throws IOException 
     */
    private String resetQuestions() throws IOException{
        
        String dropStatement = "DROP TABLE IF EXISTS Questions;";
        
        Statement st;
        
        try {
            st = conn.createStatement();
            st.execute(dropStatement);
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_RESET_QUESTIONS, null, LecturerMessage.RESP_SUCCESS));
            st.close();
            return SUCCESS; //drop successful
        } catch (SQLException ex) {
            writer.writeObject(new LecturerMessage(LecturerMessage.CMD_RESET_QUESTIONS, null, LecturerMessage.RESP_FAIL_CONNECT));
            System.out.println("Error: Problem dropping Questions table.");
            System.out.println(ex);
            return FAIL;
        }
    
    }
    
    /**
     * Drops all QueryData tables, if they exist, in the database.
     * @return success/fail response depending on whether all query data tables dropped.
     */
    private String resetQueryData(){
        //TODO: still to implement functionality
        return null;
    }
    
    private void closeConnection() throws IOException {
        reader.close();
        writer.close();
        lecturerSocket.close();
        System.out.println("Lecturer connection closed.");
    }
    
}

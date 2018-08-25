package kiwi.kiwiserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServerStartup used to connect the lecturer and student applications to database,
 generate assignments for students and generate additional questions/data for
 the lecturer. This must always be running.
 * @author talaj
 */
public class ServerStartup {
    
    //Constants:
    /*
    "localhost" specifies that the server is running on the same computer as the client.
    TO DO: change "localhost" to take a host address to connect to a remote server
    */
    public static final String SERVER_NAME = "localhost";
    public static final int STUDENT_PORT_NUM = 1024; //listening for student connection
    public static final int LECTURER_PORT_NUM = 2048;    //listening for lecturer connection
    
    //DB information
    protected static final String DB_NAME = "KiwiDB";
    protected static final String DB_PATH = "C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Data\\kiwidb\\";
    
    protected static final String MYSQL_PATH = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql";
    
    //Name and password for the users
    protected static final String ROOT_NAME = "root"; 
    protected static final String ROOT_PWD = "mysql";
    
    protected static final String STUDENT_NAME = "student";
    protected static final String STUDENT_PWD = "student";
    //Main Method:
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setUpDB();
        LecturerListener lecturerListener = new LecturerListener();
        lecturerListener.start();
        StudentListener studentListener= new StudentListener();
        studentListener.start();
    }

    public static void setUpDB(){
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", ROOT_NAME, ROOT_PWD)) {
                Statement st = conn.createStatement();
                
                //create database 'KiwiDB'
                String createDB = "CREATE DATABASE IF NOT EXISTS "+DB_NAME+";";
                st.execute(createDB);
                st.close();
                
                //create users
                //String createLecturer = "CREATE USER IF NOT EXISTS '+"+LECTURER_NAME+"'@'localhost'"+"IDENTIFIED BY"+"'"+LECTURER_PWD+"';";
                String createStudent = "CREATE USER IF NOT EXISTS '"+STUDENT_NAME+"'@'localhost'"+"IDENTIFIED BY"+"'"+STUDENT_PWD+"';";
                
                //set permissions for lecturer
                //String lecturerPermissions = "GRANT ALL PRIVILEGES ON "+DB_NAME+".* TO '"+LECTURER_NAME+"'@'localhost';";
                //String flush = "FLUSH PRIVILEGES;";                  
                                    
                String[] cmd = new String[]{MYSQL_PATH,DB_NAME,"--user=" + ROOT_NAME,"--password=" + ROOT_PWD,"-e",createStudent};
                Runtime.getRuntime().exec(cmd);
                
            } catch (IOException ex) {
                System.out.println("Error creating student user");
                Logger.getLogger(ServerStartup.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ServerStartup.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}

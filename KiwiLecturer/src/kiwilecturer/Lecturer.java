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
 *
 * @author Tala Ross(rsstal002)
 */
public class Lecturer {
    
    
    public static void uploadStudents(File csv) {
        createTable("students", csv); 
    }
    
    public static void uploadQuestions(File csv) {
        createTable("questions", csv);
    }
    
    public static void uploadQueryData(File [] csvs) {
        for (File file: csvs) {
            createTable(file.getName().substring(0, file.getName().lastIndexOf(".")), file);
        }
    }
    
    
    
    //Format for csv:
    //table name is filename
    //varchar(20)
    //double
    //"<column name> <type> <isPK (0/1)>", ...
    //<row fields>
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
   
   
    public static String viewGrades() {
        String toReturn= "";
        try {
            //Setup connection:
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/KiwiDB", "root", "mysql");

            // get student output:
            Statement st= conn.createStatement();
            String sql= "SELECT StudentNo, HighestGrade FROM STUDENTS";
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
    
    public static void main(String[] args) {
        //try {
            
        // Create an object of filereader
        // class with CSV file as a parameter.
        /*FileReader filereader = new FileReader("C:\\Users\\talaj\\OneDrive\\UCT\\Third Year\\CSC3003S\\Capstone\\test.csv");
        File f = new File("C:\\Users\\talaj\\OneDrive\\UCT\\Third Year\\CSC3003S\\Capstone\\test.csv");
        System.out.println(f.getName());
        String uploadStatement = "LOAD DATA INFILE \'"+f.getAbsolutePath()+"\'INTO TABLE "+"tbl"
                    + " FIELDS TERMINATED BY \',\' ENCLOSED BY \'\"\' LINES TERMINATED BY \'\\n\' IGNORE 1 ROWS";
            System.out.println(uploadStatement);
        // create csvReader object passing
        // file reader as a parameter
        CSVReader csvReader = new CSVReader(filereader);
        String[] nextRecord;
 
        // we are going to read data line by line
        while ((nextRecord = csvReader.readNext()) != null) {
            for (String cell : nextRecord) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }
    catch (Exception e) {
        e.printStackTrace();
    }*/
       /*File f = new File("C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Data\\kiwidb\\test.csv");
        createTable(f);*/
        
    }
    
}

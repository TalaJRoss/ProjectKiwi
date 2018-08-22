package kiwi.kiwiserver;

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
    public static final String SERVER_NAME= "localhost";
    public static final int STUDENT_PORT_NUM= 1024; //listening for student connection
    public static final int LECTURER_PORT_NUM= 2048;    //listening for lecturer connection
    
    
    //Main Method:
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LecturerListener lecturerListener = new LecturerListener();
        lecturerListener.start();
        StudentListener studentListener= new StudentListener();
        studentListener.start();
    }
    
}

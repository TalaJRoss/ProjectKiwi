/*
 * Contains data for uploading csv files to the server
 */
package kiwi.message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Contains information pertaining to files the lecturer uploads to the database 
 * in CMD_UPLOAD_STUDENTS, CMD_UPLOAD_QUESTIONS and CMD_UPLOAD_QUERY 
 * message from Lecturer client to server.
 * @author nikai
 */
public class CSVFiles implements Serializable{
    
    //Instance Variables:
    private ArrayList<byte []> csvFiles;  
    private String [] fileNames;

    // Constructors:
    /**
     * Constructs the CSVFiles using the parameters provided below
     * @param csvFiles
     * @param fileNames 
     */
    public CSVFiles(ArrayList<byte[]> csvFiles, String[] fileNames) {
        this.csvFiles = csvFiles;
        this.fileNames = fileNames;
    }
    
    // Getters:
    /**
     * Gets the CSV File 
     * @return ArrayList of bite array containing the data of CSV files
     */
    public ArrayList<byte []> getCsvFiles() {
        return csvFiles;
    }
    
    /**
     * Gets the CSV File
     * @return Bite array represent of the CSV File 
     */
    public byte [] getCsvFile() {
        return csvFiles.get(0);
    }

    /**
     * Get the file names 
     * @return String Array of File Names
     */
    public String[] getFileNames() {
        return fileNames;
    }
    
    /**
     * Get the File name 
     * @return The File Name
     */
    public String getFileName() {
        return fileNames[0];
    }
}

/*
 * Contains data for uploading csv files to the server
 */
package kiwi.message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author nikai
 */
public class CSVFiles implements Serializable{
    
    //Instance Variables:
    private ArrayList<byte []> csvFiles;  
    private String [] fileNames;

    public CSVFiles(ArrayList<byte[]> csvFiles, String[] fileNames) {
        this.csvFiles = csvFiles;
        this.fileNames = fileNames;
    }
    
    public ArrayList<byte []> getCsvFiles() {
        return csvFiles;
    }
    
    public byte [] getCsvFile() {
        return csvFiles.get(0);
    }

    public String[] getFileNames() {
        return fileNames;
    }
    
    public String getFileName() {
        return fileNames[0];
    }
}

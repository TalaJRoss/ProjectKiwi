package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a Schema in a CMD_UPLOAD_SCHEMA
 * message from lecturer client to server.
 * @author Nikai Jagganath (JGGNIK001)
 */
public class Schema implements Serializable{
    
    /**
     * Byte array representing the schema image.
     */
    private final byte [] schemaImg;
    
    // Constructors:
    /**
     * Construct the Schema with parameter below
     * @param schemaImg byte array representing the schema image.
     */
    public Schema(byte[] schemaImg) {
        this.schemaImg = schemaImg;
    }
    
    // Getters:
    /**
     * Get the Schema Image
     * @return Schema Image
     */
    public byte[] getSchemaImg() {
        return schemaImg;
    }
    
}

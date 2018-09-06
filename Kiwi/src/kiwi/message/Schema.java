
package kiwi.message;

import java.io.Serializable;

/**
 * Contains information pertaining to a Schema in a CMD_UPLOAD_SCHEMA
 * message from lecturer client to server.
 * @author nikai
 */
public class Schema implements Serializable{
    
    private byte [] schemaImg;
    
    // Constructors:
    /**
     * Construct the Schema with parameter below
     * @param schemaImg 
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

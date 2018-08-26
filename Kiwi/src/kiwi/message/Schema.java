
package kiwi.message;

import java.io.Serializable;

/**
 *
 * @author nikai
 */
public class Schema implements Serializable{
    
    private byte [] schemaImg;

    public Schema(byte[] schemaImg) {
        this.schemaImg = schemaImg;
    }
    
    public byte[] getSchemaImg() {
        return schemaImg;
    }
    
}

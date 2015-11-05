//@@author A0133920N
package common;

import java.io.IOException;

public interface ConfigurationInterface {	
    public String getUsrFileDirectory();
    public Time getDefaultStartTime();
    public Time getDefaultEndTime();
    
    public void setUsrFileDirector(String newDir) throws IOException;
    public void setDefaultStartTime(String newTime) throws IOException;
    public void setDefaultEndTime(String newTime) throws IOException;
    
    public void resetStorageLocation() throws IOException;
}

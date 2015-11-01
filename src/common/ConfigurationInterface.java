package common;

import java.io.IOException;

public interface ConfigurationInterface {	
    public String getUsrFileDirectory();
    
    public void setUsrFileDirector(String newDir) throws IOException;
}

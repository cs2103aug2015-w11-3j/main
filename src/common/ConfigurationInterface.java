//@@author A0133920N
package common;

import java.io.IOException;

public interface ConfigurationInterface {
	
	public String getDefaultUsrFileDirectory();
    public String getUsrFileDirectory();
    public Time getDefaultStartTime();
    public Time getDefaultEndTime();
    public String getSkin();
    public boolean isUserAlias(String alias);
    public String getUserAliasTargetName(String alias);
    
    public void setUsrFileDirector(String newDir) throws IOException;
    public void setDefaultStartTime(String newTime) throws IOException;
    public void setDefaultEndTime(String newTime) throws IOException;
    public void setSkin(String skin) throws IOException;
    public void setUserAlias(String alias, String target) throws IOException;
    public void removeUserAlias(String alias) throws IOException;
    public void clearUserAliases() throws IOException;
    
    public void resetStorageLocation() throws IOException;
	
	
}

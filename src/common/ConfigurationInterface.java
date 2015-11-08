//@@author A0133920N
package common;

public interface ConfigurationInterface {
	
	public String getDefaultUsrFileDirectory();
    public String getUsrFileDirectory();
    public Time getDefaultStartTime();
    public Time getDefaultEndTime();
    public String getSkin();
    public boolean isUserAlias(String alias);
    public String getUserAliasTargetName(String alias);
    
    public void setStorageLocation(String newDir);
    public void setDefaultStartTime(String newTime);
    public void setDefaultEndTime(String newTime);
    public void setSkin(String skin);
    public void setUserAlias(String alias, String target);
    public void removeUserAlias(String alias);
    public void clearUserAliases();
    
    public void resetStorageLocation();
	
	
}

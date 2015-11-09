//@@author A0133920N
package common;

public interface ConfigurationInterface {
	
	/*
	 * @return the default storage location, which cannot be changed
	 */
	public String getDefaultUsrFileDirectory();
	
	/*
	 * @return the current storage location specified by user
	 */
    public String getUsrFileDirectory();
    
    /*
     * @return the current default start time
     */
    public Time getDefaultStartTime();
    
    /*
     * @return the current default end time
     */
    public Time getDefaultEndTime();
    
    /*
     * @return the current theme selected by user
     */
    public String getSkin();
    
    /*
     * Check whether this string is an alias specified by user
     */
    public boolean isUserAlias(String alias);
    
    /*
     * Get the keyword that this string represents
     */
    public String getUserAliasTargetName(String alias);
    
    /*
     * After the storage file moved a new location, set configuration 
     * to remember this new location
     */
    public void setStorageLocation(String newDir);
    
    /*
     * Record a new default start time specified by user
     */
    public void setDefaultStartTime(String newTime);
    
    /*
     * Record a new default end time specified by user
     */
    public void setDefaultEndTime(String newTime);
    
    /*
     * Record a new theme specified by user
     */
    public void setSkin(String skin);
    
    /*
     * Set a new alias specified by user
     */
    public void setUserAlias(String alias, String target);
    
    /*
     * Delete an alias previously added by user
     */
    public void removeUserAlias(String alias);
    
    /*
     * Remove all alias previously added by user
     */
    public void clearUserAliases();
    
    /*
     * Record the storage location is set to default
     */
    public void resetStorageLocation();
	
}

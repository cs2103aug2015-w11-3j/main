package common;

import logic.Logic;

public class Common {
	
static Common instance;
	
	private String usrFileDirectory;		// User data file directory
	private final String configDirectory = "usr.conf";		// Config to store global settings

	public static Common getInstance(){
		if(instance == null){
			instance = new Common();
		}
		return instance;
	}
	
	public Common(){
		
	}

	public String getConfigDirectory(){
		return configDirectory;
	}
	public String getUsrFileDirectory(){
		if(usrFileDirectory == null){
			setUpFileDirectory();
		}
		return usrFileDirectory;
	}

}

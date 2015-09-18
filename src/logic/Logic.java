package logic;

import common.Common;

public class Logic {
	
	static Logic instance;
	
	public static Logic getInstance(){
		if(instance == null){
			instance = new Logic();
		}
		return instance;
	}
	
	public Logic(){
		
	}

	public void setUpFileDirectory() {
		String fileDir = Common.getInstance().getUsrFileDirectory();
		
	}
	
	
}

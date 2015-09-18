package logic;

import common.CelebiContainer;
import common.Common;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {
	
	StorageInterface storage;
	
	public Logic(){
		
	}

	public void setUpFileDirectory() {
		String fileDir = Common.getInstance().getUsrFileDirectory();
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		storage = new Storage();
	}

	@Override
	public CelebiContainer executeCommand() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

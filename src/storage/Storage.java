package storage;

import common.CelebiContainer;

public class Storage implements StorageInterface {

	public void init() {
		System.out.println("Storage Init");
		System.out.println("Storage Init complete");
	}

	public Storage() {

	}


	@Override
	public void save(CelebiContainer c) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean load(String s, CelebiContainer c) {
		
		boolean loadSuccessful = true;
		return loadSuccessful;
	}
}

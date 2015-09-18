package storage;

import common.CelebiBag;

public class Storage implements StorageInterface {

	public void init() {
		System.out.println("Storage Init");
		System.out.println("Storage Init complete");
	}

	public Storage() {

	}


	@Override
	public void save(CelebiBag c) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean load(String s, CelebiBag c) {
		
		boolean loadSuccessful = true;
		return loadSuccessful;
	}
}

package storage;

import common.CelebiBag;

public interface StorageInterface {
	public void init();

	public boolean load(String s, CelebiBag c);
	
	public void save(CelebiBag c);
}

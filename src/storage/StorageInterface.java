package storage;

import common.Celebi;
import common.CelebiBag;

public interface StorageInterface {
	public void init();

	public boolean load(String s, CelebiBag c);
	
	public boolean save(Celebi c);
	
	public boolean delete(Celebi c);
}

package storage;

import common.Celebi;
import common.CelebiBag;

public interface StorageInterface {
	public void init();

	public boolean load(String s, CelebiBag c);
	
	public Celebi save(Celebi c);
}

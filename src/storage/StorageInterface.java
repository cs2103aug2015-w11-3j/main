package storage;

import common.CelebiContainer;

public interface StorageInterface {
	public void init();

	public boolean load(String s, CelebiContainer c);
	
	public void save(CelebiContainer c);
}

package storage;

import common.Task;
import common.TasksBag;

public interface StorageInterface {
	public void init();

	public boolean load(String s, TasksBag c);
	
	public boolean save(Task c);
	
	public boolean delete(Task c);
}

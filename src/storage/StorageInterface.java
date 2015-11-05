package storage;

import java.io.IOException;

import common.Task;
import common.TasksBag;

public interface StorageInterface {
	public void init();
	
	public boolean load(String s, TasksBag c);
	
	public boolean save(Task c);
	
	public boolean delete(Task c);
	
	public boolean moveFileTo(String destination) throws IOException;
	
	public void close();
}

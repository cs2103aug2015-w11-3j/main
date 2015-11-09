package storage;

import java.io.IOException;

import common.Task;
import common.TasksBag;

public interface StorageInterface {
	/*
	 * Initialize this component. It will find the storage file, or create one if not exists.
	 */
	public void init();
	
	/*
	 * Add all persisted tasks into the given TaskBag. 
	 */
	public boolean load(TasksBag c);
	
	/*
	 * Add a new task into storage file the input task is new (no id), 
	 * update the input if it is current in storage file, 
	 * or restore the input task if it has been deleted (has id but not exists in storage file). 
	 * Return true if the execution succeed, return false is exception encountered.
	 */
	public boolean save(Task c);
	
	/*
	 * Remove a task from storage file. Returns true if successfully removed, 
	 * returns false if exception encountered.
	 */
	public boolean delete(Task c);
	
	/*
	 * Move the storage file to a new location specified by destination. 
	 * Throws exception, which will be caught by logic, if fail to move.
	 */
	public void moveFileTo(String destination) throws IOException;
	
	/*
	 * Close the file and reset data fields.
	 */
	public void close();
}

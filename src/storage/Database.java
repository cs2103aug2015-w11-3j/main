//@@author A0133920N

/*
 * @class: Database
 * 
 * Database is the class implementing the actual file I/O logic, including appending, 
 * insertion, removal, loading of binary data, as well as moving the storage file to 
 * a certain location. Also handles serialisation and JSON load parsing. 
 * It keeps a direct and strict map of the content of tasks.json, which is an array
 * of the tasks (in JSON format). 
 */

package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

class Database {
	// name of storage files
	private final static String FILENAME = "task.json";
	private final static String TEST_FILENAME = "test_task.json";
	
	// used to apply singleton pattern
	private static Database _instance;
	
	// IO tools
	private File db;
	private Scanner dbReader;
	private Writer dbWriter;

	// Data persisted by this class
	private List<TaskJson> dbData;
	private HashMap<Integer, TaskJson> dbIndex;
	
	/* 
	 * Use Singleton Pattern, make constructor private and define 
	 * a public method to get the only instance;
	 */
	private Database() {
		
	}
	
	static Database getDatabase() {
		if (_instance == null) {
			_instance = new Database();
    	}
    	
    	return _instance;
	}
	
	/*
	 * Connect to the file named 'tasks.json' in the specified path
	 * @returns true if connect successfully, otherwise false
	 */
	boolean connect (String path, boolean isTestMode) {
		try {
			if (isTestMode) {
				db = new File(path, TEST_FILENAME);
				db.createNewFile();
			} else {
				// look for the file named tasks.json in the specified directory, 
				// if not found, it will create this file. 
				db = new File(path, FILENAME);
				if(!db.exists()) {
					db.createNewFile();
				}
			}

			// parse the content in storage file
			load();
			
			return true;
		} catch (IOException e) {
			dbData = new ArrayList<TaskJson>();
			dbIndex = new HashMap<Integer, TaskJson>();
			return false;
		}
	}
	
	/*
	 * Reset itself to initial status
	 */
	void disconnect () {
		db = null;
		dbData = null;
		dbIndex = null;
	}
	
	/*
	 * Get all persisted tasks 
	 */
	List<TaskJson> selectAll () {
		checkConnected();
		return new ArrayList<TaskJson>(dbData);
	}
	
	/*
	 * Get a certain task specified by its serial number
	 */
	TaskJson selectById (int id) {
		checkConnected();
		return dbIndex.get(id);
	}
	
	/*
	 * Write a new task into storage file
	 * @return the generated id for the new task
	 */
	int insert (TaskJson cj) throws IOException {
		checkConnected();
		
		int last = getLastId();
		int newId = last + 1;
		
		cj.setId(newId);
		dbData.add(cj);
		dbIndex.put(newId, cj);
		save ();
		
		return newId;
	}
	
	/*
	 * Update the attributes of task existing in storage file
	 */
	void update (TaskJson cj) throws IOException {
		checkConnected();
		
		TaskJson cjInDb = dbIndex.get(cj.getId());
		
		cjInDb.update(cj);
		save ();
	}
	
	/*
	 * Delete a task specified by serial number from storage file
	 */
	void delete (int id) throws IOException {
		checkConnected();
		
		TaskJson cj= dbIndex.get(id);
		
		dbData.remove(cj);
		dbIndex.remove(id);
		save ();
	}
	
	/*
	 * Re-add a task which has been deleted
	 */
	void restore (TaskJson cj) throws IOException {
		checkConnected();
		
		dbData.add(cj);
		dbIndex.put(cj.getId(), cj);
		// keep the task in dbData are sorted by their serial numbers
		orderById();
		save ();
	}
	
	/*
	 * Move a storage file to new location
	 * Exceptions are intended to be thrown and be caught by logic
	 */
	void moveTo(String destination, boolean isTestMode) throws IOException {
		checkConnected();
		
		String fileName = isTestMode ? TEST_FILENAME : FILENAME;
		File newDb = new File(destination, fileName);
		
		Files.move(db.toPath(), newDb.toPath());		
		db = newDb;
	}
		
	
	// Private Methods
	
	/*
	 * Read from storage file and parse the content from text to JSON data
	 */
	private void load () throws IOException {
		assert(db != null);
		
		// Read content in storage file
		String plainText = "";
		
		dbReader = new Scanner(db);
		dbReader.useDelimiter("\\Z");
		
		if (dbReader.hasNext()) {
			plainText = dbReader.next();
		}
		
		dbReader.close();
		dbReader = null;
		
		// Parse the content and translate it into a list of tasks
		JSONArray parsedResult = (JSONArray)JSONValue.parse(plainText);
		
		if (parsedResult == null) {
			parsedResult = new JSONArray();
		}
					
		dbData = new ArrayList<TaskJson>();
		dbIndex = new HashMap<Integer, TaskJson>();
		
		addToData(parsedResult);
		save();
	}
	
	// Write the data persisted by this class into the storage file
	private void save () throws IOException {
		checkConnected();
	
		String text = JSONValue.toJSONString(dbData);
		dbWriter = new BufferedWriter(new FileWriter(db));
		
		dbWriter.write(text);
		dbWriter.close();
		dbWriter = null;
	}
	
	// Sort the current data by their serial number
	private void orderById () {
		checkConnected();
		Collections.sort(dbData, TaskJson.getComparator());
	}
	
	private void checkConnected() {
		assert(dbData != null);
		assert(dbIndex != null);
	}
	
	// Find the serial number of the last task in current data
	private int getLastId() {
		checkConnected();
		
		int last; 
		int dbSize = dbData.size();

		if (dbSize < 1) {
			last = 0;
		} else {
			last = Integer.parseInt(dbData.get(dbSize - 1).get("ID"));
		}
		
		return last;
	}
	
	// Add a list of JSON object to currently persisted data
	private void addToData(JSONArray jsons) {
		checkConnected();
		
		for (int i = 0; i < jsons.size(); i ++) {
			JSONObject json = (JSONObject)jsons.get(i);
			TaskJson cj = new TaskJson(json);
			
			if(cj.isValid()) {
				dbData.add(cj);
				dbIndex.put(Integer.parseInt(cj.get("ID")), cj);
			}
		}
	}
}

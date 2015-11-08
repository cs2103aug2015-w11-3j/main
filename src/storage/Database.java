//@@author A0133920N
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

abstract class Database {
	private final static String FILENAME = "task.json";
	private final static String TEST_FILENAME = "test_task.json";
	
	private static File db;
	private static Scanner dbReader;
	private static Writer dbWriter;

	private static List<TaskJson> dbData;
	private static HashMap<Integer, TaskJson> dbIndex;
	
	static boolean connect (String path, boolean isTestMode) {
		try {
			if (isTestMode) {
				db = new File(path, TEST_FILENAME);
				db.createNewFile();
			} else {
				db = new File(path, FILENAME);
				if(!db.exists()) {
					db.createNewFile();
				}
			}

			load();
			
			return true;
		} catch (IOException e) {
			dbData = new ArrayList<TaskJson>();
			dbIndex = new HashMap<Integer, TaskJson>();
			return false;
		}
	}
	
	static private void load () throws IOException {
		assert(db != null);
		
		String plainText = "";
		
		dbReader = new Scanner(db);
		dbReader.useDelimiter("\\Z");
		
		if (dbReader.hasNext()) {
			plainText = dbReader.next();
		}
		
		dbReader.close();
		dbReader = null;
		
		JSONArray parsedResult = (JSONArray)JSONValue.parse(plainText);
		
		if (parsedResult == null) {
			parsedResult = new JSONArray();
		}
					
		dbData = new ArrayList<TaskJson>();
		dbIndex = new HashMap<Integer, TaskJson>();
		for (int i = 0; i < parsedResult.size(); i ++) {
			TaskJson cj = new TaskJson((JSONObject)parsedResult.get(i));
			if(cj.isValid()) {
				dbData.add(cj);
				dbIndex.put(Integer.parseInt(cj.get("ID")), cj);
			}
		}
		save();
	}
	
	static void disconnect () {
		db = null;
		dbData = null;
		dbIndex = null;
	}
	
	static List<TaskJson> selectAll () {
		checkConnected();
		return new ArrayList<TaskJson>(dbData);
	}
	
	static TaskJson selectById (int id) {
		checkConnected();
		return dbIndex.get(id);
	}
	
	static int insert (TaskJson cj) throws IOException {
		checkConnected();
		
		int last; 
		int dbSize = dbData.size();

		if (dbSize < 1) {
			last = 0;
		} else {
			last = Integer.parseInt(dbData.get(dbSize - 1).get("ID"));
		}
		
		int id = last + 1;
		
		cj.setId(id);
		dbData.add(cj);
		dbIndex.put(id, cj);
		
		save ();
		
		return id;
	}
		
	static void update (TaskJson cj) throws IOException {
		checkConnected();
		
		TaskJson cjInDb = dbIndex.get(cj.getId());
		cjInDb.update(cj);
				
		save ();
	}
	
	static void delete (int id) throws IOException {
		checkConnected();
		TaskJson cj= dbIndex.get(id);
		
		dbData.remove(cj);
		dbIndex.remove(id);
		
		save ();
	}
	
	static void restore (TaskJson cj) throws IOException {
		checkConnected();
		dbData.add(cj);
		dbIndex.put(cj.getId(), cj);
		orderById();
		
		save ();
	}
	
	static void moveTo(String destination, boolean isTestMode) throws IOException {
		checkConnected();
		String fileName = isTestMode ? TEST_FILENAME : FILENAME;
		File newDb = new File(destination, fileName);
		
		Files.move(db.toPath(), newDb.toPath());		
		db = newDb;
	}
		
	// private methods
	private static void save () throws IOException {
		checkConnected();
		dbWriter = new BufferedWriter(new FileWriter(db));
		String text = JSONValue.toJSONString(dbData);
			
		dbWriter.write(text);
		dbWriter.close();
			
		dbWriter = null;
	}
	
	private static void orderById () {
		checkConnected();
		Collections.sort(dbData, TaskJson.getComparator());
	}
	
	private static void checkConnected() {
		assert(dbData != null);
		assert(dbIndex != null);
	}
}

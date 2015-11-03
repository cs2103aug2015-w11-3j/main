//@@author A0133920N
package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
	
	private static File db;
	private static Scanner dbReader;
	private static Writer dbWriter;

	private static List<TaskJson> dbData;
	private static HashMap<Integer, TaskJson> dbIndex;
	
	private static boolean isConnected;

	static boolean connect (String path) {
		try {
			db = new File(path, FILENAME);
			
			if(!db.exists()) {
				db.createNewFile();
			}

			dbReader = new Scanner(db);
			dbReader.useDelimiter("\\Z");
			
			isConnected = true;
		} catch (IOException e) {
			dbReader = null;
			isConnected = false;
		}
		return isConnected;
	}
	
	static boolean load () throws BadFileContentException {
		if (!isConnected) {
			return false;
		} 
		
		try {
			String plainText = "";
			if (dbReader.hasNext()) {
				plainText = dbReader.next();
			}
			
			JSONArray parsedResult = (JSONArray)JSONValue.parse(plainText);
			
			if (parsedResult == null) {
				parsedResult = new JSONArray();
			}
						
			dbData = new ArrayList<TaskJson>();
			dbIndex = new HashMap<Integer, TaskJson>();
			for (int i = 0; i < parsedResult.size(); i ++) {
				TaskJson cj = new TaskJson((JSONObject)parsedResult.get(i));
				dbData.add(cj);
				dbIndex.put(Integer.parseInt(cj.get("ID")), cj);
			}
			return true;
		} catch (ClassCastException e) {
			throw new BadFileContentException("Bad file format");
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	static boolean disconnect () {
		db = null;
		dbReader = null;
		dbData = null;
		dbIndex = null;
		
		isConnected = false;
		
		return true;
	}
	
	static List<TaskJson> selectAll () {
		return new ArrayList<TaskJson>(dbData);
	}
	
	static TaskJson selectById (int id) {
		return dbIndex.get(id);
	}
	
	static int insert (TaskJson cj) {
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
		
	static boolean update (TaskJson cj) {
		TaskJson cjInDb = dbIndex.get(cj.getId());
		cjInDb.update(cj);
				
		save ();
		
		return true;
	}
	
	static boolean delete (int id) {
		TaskJson cj= dbIndex.get(id);
		
		dbData.remove(cj);
		dbIndex.remove(id);
		
		save ();

		return true;
	}
	
	static boolean restore (TaskJson cj) {
		dbData.add(cj);
		dbIndex.put(cj.getId(), cj);
		orderById();
		
		save ();
		
		return true;
	}
	
	static boolean moveTo(String destination) throws FileNotFoundException {
		db.renameTo(new File(destination, FILENAME));
		return true;
	}
	
	// private methods
	private static boolean save () {
		try {
			dbWriter = new BufferedWriter(new FileWriter(db));
			String text = JSONValue.toJSONString(dbData);
			
			dbWriter.write(text);
			dbWriter.close();
			dbWriter = null;
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private static void orderById () {
		Collections.sort(dbData, TaskJson.getComparator());
	}
}

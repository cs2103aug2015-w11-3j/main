package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Database {
	private static File db;
	private static Scanner dbReader;

	private static List<CelebiJson> dbData;
	private static HashMap<Integer, CelebiJson> dbIndex;
	
	private static boolean isConnected;

	static boolean connect (String path) {
		db = new File(path);
		try {
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
	
	static boolean load () {
		try {
			String plainText = "";
			if (dbReader.hasNext()) {
				plainText = dbReader.next();
			}
			
			JSONArray parsedResult = (JSONArray)JSONValue.parse(plainText);
			
			if (parsedResult == null) {
				parsedResult = new JSONArray();
			}
						
			dbData = new ArrayList<CelebiJson>();
			dbIndex = new HashMap<Integer, CelebiJson>();
			
			for (int i = 0; i < parsedResult.size(); i ++) {
				CelebiJson cj = new CelebiJson((JSONObject)parsedResult.get(i));
				dbData.add(cj);
				dbIndex.put(Integer.parseInt(cj.get("ID")), cj);
			}
			return true;
		} catch (ClassCastException e) {
			System.out.println("Bad file format");
			return false;
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
	
	static List<CelebiJson> getData () {
		return new ArrayList<CelebiJson>(dbData);
	}
	
	static int insert (CelebiJson cj) {
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
		
	static boolean update (int id, CelebiJson cj) {
		CelebiJson cjInDb = dbIndex.get(id);
		cjInDb.update(cj);
				
		save ();
		
		return true;
	}
	
	static boolean delete (int id) {
		CelebiJson cj= dbIndex.get(id);
		
		dbData.remove(cj);
		dbIndex.remove(id);
		
		save ();

		return true;
	}
	
	// private methods
	private static boolean save () {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(db));
			String text = JSONValue.toJSONString(dbData);
			System.out.println(text);
			writer.write(text);
			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}

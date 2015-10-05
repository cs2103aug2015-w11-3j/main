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
import org.json.simple.parser.JSONParser;

import common.Celebi;

public class Database {
	private static BufferedWriter dbWriter;
	private static Scanner dbReader;
	private static JSONParser dbParser=new JSONParser();

	private static List<CelebiJson> dbData;
	private static HashMap<Integer, CelebiJson> dbIndex;
	
	private static boolean isConnected;

	static boolean connect (String path) {
		File file = new File(path);
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			dbWriter = new BufferedWriter(new FileWriter(file));
			dbReader = new Scanner(file);
			dbReader.useDelimiter("\\A");
			isConnected = true;
		} catch (IOException e) {
			dbWriter = null;
			dbReader = null;
			isConnected = false;
		}
		return isConnected;
	}
	
	static boolean load () {
		try {
			if (!isConnected) {
				return false;
			} 
			String plainText = dbReader.next();
			JSONArray parsedResult = (JSONArray)dbParser.parse(plainText);
			dbData = new ArrayList<CelebiJson>();
			for (int i = 0; i < parsedResult.size(); i ++) {
				CelebiJson cj = new CelebiJson((JSONObject)parsedResult.get(i));
				dbData.add(cj);
				dbIndex.put(Integer.parseInt(cj.get(Celebi.DataType.ID)), cj);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	static List<CelebiJson> getData () {
		return new ArrayList<CelebiJson>(dbData);
	}
	
	static int insert (CelebiJson cj) {
		int last = Integer.parseInt(dbData.get(dbData.size() - 1).get(Celebi.DataType.ID));
		int id = last + 1;
		
		cj.setId(id);
		dbData.add(cj);
		dbIndex.put(id, cj);
		
		save ();
		
		return id;
	}
		
	static boolean update (Celebi c) {
		int id = c.getId();
		CelebiJson cj= dbIndex.get(id);
		cj.update(c);
				
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
		String text = JSONValue.toJSONString(dbData);
		try {
			dbWriter.write(text);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}

package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class Database {
	private static BufferedWriter dbWriter;
	private static Scanner dbReader;
	private static JSONParser dbParser=new JSONParser();

	private static JSONArray dbData;
	
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
			dbData = (JSONArray)JSONValue.parse(plainText);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

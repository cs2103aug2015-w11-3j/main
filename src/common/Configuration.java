package common;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import storage.BadFileContentException;

public class Configuration {

	static Configuration instance;

	private final String CONFIG_DIRECTORY = "bin/config.json"; // Config to store global
														// settings
	
	private File configFile;
	private Scanner configReader;
	
	private String configStorageLocation;

	public static Configuration getInstance() throws BadFileContentException {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	private Configuration() throws BadFileContentException {
		try {
			findConfigFile();
			
			parseConfigFile();
			
		} catch (ClassCastException e) {
			throw new BadFileContentException("Bad file format");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void findConfigFile() {
		try {
			configFile = new File(CONFIG_DIRECTORY);
			
			if(!configFile.exists()) {
				// if not found, re-create and set to default configuration
				configFile.createNewFile();
			}
			
			configReader = new Scanner(configFile);
			configReader.useDelimiter("\\Z");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void parseConfigFile() throws BadFileContentException {
		try {
			String plainText = "";
			if (configReader.hasNext()) {
				plainText = configReader.next();
			}
			
			JSONObject parsedResult = (JSONObject) JSONValue.parse(plainText);
			
			if (parsedResult == null) {
				reset();
			}  
			
			configStorageLocation = (String) parsedResult.get("STORAGE_LOCATION");
			
			if(configStorageLocation == null) {
				configStorageLocation = "";
			}
		} catch (ClassCastException e) {
			reset();;
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void reset() {
		
	}
	
	public String getUsrFileDirectory() {
		Log.log("usr file is " + configStorageLocation, this.getClass());
		return configStorageLocation;
	}
}

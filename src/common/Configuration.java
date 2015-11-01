//@@author A0133920N
package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/*
 * Possible errors and solutions: 
 * 1. configuration file not found -> recreate write in default settings
 * 2. configuration file cannot be parsed -> rewrite in default settings
 * 3. configuration file does not have certain property -> set to default
 */
public class Configuration implements ConfigurationInterface {

    private final String CONFIG_DIRECTORY = "bin/config.json";
    private final String KEY_STORAGE_LOCATION = "STORAGE_LOCATION";
    private final String KEY_DEFAULT_START_TIME = "DEFAULT_START_TIME";
    private final String KEY_DEFAULT_END_TIME = "DEFAULT_END_TIME";
    
    private final String DEFAULT_VALUE_STORAGE_LOCATION = "bin";
    private final String DEFAULT_VALUE_DEFAULT_START_TIME = "00:00";
    private final String DEFAULT_VALUE_DEFAULT_END_TIME = "23:59";
    
    private final String MESSAGE_INVALID_STORAGE_LOCATION = "%1$s is not a valid path";
    private final String MESSAGE_INVALID_DEFAULT_START_TIME = "Start time %1$s is invalid";
    private final String MESSAGE_INVALID_DEFAULT_END_TIME = "End time %1$s is invalid";
    
    private final String MESSAGE_RESET_STORAGE_LOCATION = "Storage location set to " + DEFAULT_VALUE_STORAGE_LOCATION;
    private final String MESSAGE_RESET_DEFAULT_START_TIME = "Storage location set to " + DEFAULT_VALUE_DEFAULT_START_TIME;
    private final String MESSAGE_RESET_DEFAULT_END_TIME = "Storage location set to " + DEFAULT_VALUE_DEFAULT_END_TIME;

    static Configuration instance;

    private File configFile;
    private Scanner configReader;
    private Writer configWriter;
    private String configStorageLocation, configDefaultStartTime, configDefaultEndTime;

    public static ConfigurationInterface getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
    
    private Configuration() {
        try {
            findConfigFile();
            readProperties();
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void findConfigFile() throws IOException {
        configFile = new File(CONFIG_DIRECTORY);

        if (!configFile.exists()) {
            // if not found, re-create and set to default configuration
            configFile.createNewFile();
            resetAll();
        }

        configReader = new Scanner(configFile);
        configReader.useDelimiter("\\Z");
    }

    private void readProperties() throws IOException {
        try {
            String plainText = "";
            if (configReader.hasNext()) {
                plainText = configReader.next();
            }

            JSONObject parsedResult = (JSONObject) JSONValue.parse(plainText);
            if (parsedResult == null) {
            	resetAll();
            }

            configStorageLocation = (String) parsedResult.get(KEY_STORAGE_LOCATION);
            
            if (!isValidPath(configStorageLocation)) {
            	resetStorageLocation();
            	logError(MESSAGE_INVALID_STORAGE_LOCATION, configStorageLocation, MESSAGE_RESET_STORAGE_LOCATION);
            } 
            
            configDefaultStartTime = (String) parsedResult.get(KEY_DEFAULT_START_TIME);
            if (!isValidTime(configDefaultStartTime)) {
            	resetDefaultStartTime();
            	logError(MESSAGE_INVALID_DEFAULT_START_TIME, configDefaultStartTime, MESSAGE_RESET_DEFAULT_START_TIME);
            } 
            
            configDefaultEndTime = (String) parsedResult.get(KEY_DEFAULT_END_TIME);
            if (!isValidTime(configDefaultEndTime)) {
            	resetDefaultEndTime();
            	logError(MESSAGE_INVALID_DEFAULT_END_TIME, configDefaultEndTime, MESSAGE_RESET_DEFAULT_END_TIME);
            } 
            
            writeBack();
        } catch (ClassCastException e) {
        	resetAll();
        }
    }

    public String getUsrFileDirectory() {
        return configStorageLocation;
    }
    
    public Time getDefaultStartTime() {
        return new Time(configDefaultStartTime);
    }
    
    public Time getDefaultEndTime() {
        return new Time(configDefaultEndTime);
    }

    public void setUsrFileDirector(String newDir) throws IOException {
    	if (isValidPath(newDir)) {
    		configDefaultStartTime = newDir;
    		
    		// write to the configuration file
            writeBack();
            Log.log("usr file moved to " + newDir, this.getClass());
    	}
    }

    public void setDefaultStartTime(String newTime) throws IOException {
    	if (isValidTime(newTime)) {
    		configDefaultStartTime = newTime;
    		
    		// write to the configuration file
            writeBack();
            Log.log("default start time reset to " + newTime, this.getClass());
    	} 
    }
    
    public void setDefaultEndTime(String newTime) throws IOException {
    	if (isValidTime(newTime)) {
    		configDefaultEndTime = newTime;
    		
    		// write to the configuration file
            writeBack();
            Log.log("default end time reset to " + newTime, this.getClass());
    	}
    }
    
    private void logError(String invalidMsg, String arg, String resetMsg) {
    	String formatted = Utilities.formatString(invalidMsg, arg);
    	Log.log(formatted);
    	Log.log(resetMsg);
    }
    
    private void resetAll() throws IOException {
        // set all properties to default value
    	resetStorageLocation();
    	resetDefaultStartTime();
    	resetDefaultEndTime();

        // write to the configuration file
        writeBack();
    }
    
    private void resetStorageLocation() {
    	configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;
    }
    
    private void resetDefaultStartTime() {
    	configDefaultStartTime = DEFAULT_VALUE_DEFAULT_START_TIME;
    }
    
    private void resetDefaultEndTime() {
    	configDefaultEndTime = DEFAULT_VALUE_DEFAULT_END_TIME;
    }
    
    private boolean isValidPath(String path) {
    	if (path == null) {
    		return false;
    	}
    	
    	return isValidPathName(path) && isExistingPath(path);
    }
    
    private boolean isValidTime(String str) {
    	if (str == null) {
    		return false;
    	}
    	
    	Time t = new Time(str);
    	return t.isValid();
    }
    
    private boolean isValidPathName(String path) {
    	String[] folders = path.split("/");
    	for(int i = 0; i < folders.length; i++) {
    		if(!isAlphanumeric(folders[i])) {
    			return false;
    		}
    	}	
    	return true;
    }
    
    private boolean isExistingPath(String path) {
    	return new File(path).exists();
    }
    
    private boolean isAlphanumeric(String s) {
		return s.matches("[A-Za-z0-9]+");
    }

    private void writeBack() throws IOException {
        JSONObject configJson = new JSONObject();
        configJson.put(KEY_STORAGE_LOCATION, configStorageLocation);
        configJson.put(KEY_DEFAULT_START_TIME, configDefaultStartTime);
        configJson.put(KEY_DEFAULT_END_TIME, configDefaultEndTime);

        configWriter = new BufferedWriter(new FileWriter(CONFIG_DIRECTORY));
        String text = JSONValue.toJSONString(configJson);

        configWriter.write(text);
        configWriter.close();
        configWriter = null;
    }
}

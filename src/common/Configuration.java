// @author Liu Yang

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
public class Configuration {

    private final String CONFIG_DIRECTORY = "bin/config.json";
    private final String KEY_STORAGE_LOCATION = "STORAGE_LOCATION";
    private final String DEFAULT_VALUE_STORAGE_LOCATION = "bin";
    
    private final String MESSAGE_INVALID_STORAGE_LOCATION = "%1$s is not a valid path";
    
    private final String MESSAGE_RESET_STORAGE_LOCATION = "Storage location set to " + CONFIG_DIRECTORY;

    static Configuration instance;

    private File configFile;
    private Scanner configReader;
    private Writer configWriter;
    private String configStorageLocation;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getUsrFileDirectory() {
        Log.log("usr file is " + configStorageLocation, this.getClass());
        return configStorageLocation;
    }

    public void setUsrFileDirector(String newDir) throws IOException {
        configStorageLocation = newDir;

        // write to the configuration file
        writeBack();
        Log.log("usr file moved to " + newDir, this.getClass());
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
            
            if (configStorageLocation == null || !isValidPath(configStorageLocation)) {
            	logInvalidLocation();
            	resetStorageLocation();
            } 

            writeBack();
        } catch (ClassCastException e) {
        	resetAll();
        }
    }
    
    private void logInvalidLocation() {
    	String formatted = Utilities.formatString(MESSAGE_INVALID_STORAGE_LOCATION, configStorageLocation);
    	Log.log(formatted);
    	Log.log(MESSAGE_RESET_STORAGE_LOCATION);
    }
    
    private void resetAll() throws IOException {
        // set all properties to default value
    	resetStorageLocation();

        // write to the configuration file
        writeBack();
    }
    
    private void resetStorageLocation() {
    	configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;
    }
    
    private boolean isValidPath(String path) {
    	return isValidPathName(path) && isExistingPath(path);
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

        configWriter = new BufferedWriter(new FileWriter(CONFIG_DIRECTORY));
        String text = JSONValue.toJSONString(configJson);

        configWriter.write(text);
        configWriter.close();
        configWriter = null;
    }
}

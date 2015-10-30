package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            reset();
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
                reset();
            }

            configStorageLocation = (String) parsedResult.get(KEY_STORAGE_LOCATION);
            if (configStorageLocation == null) {
                configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;
            } else if (!new File(configStorageLocation).exists()) {
            	configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;
            }

            writeBack();
        } catch (ClassCastException e) {
            reset();
        }
    }

    private void reset() throws IOException {
        // set all properties to default value
        configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;

        // write to the configuration file
        writeBack();
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

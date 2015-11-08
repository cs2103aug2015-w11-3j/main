//@@author A0133920N
package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import parser.Aliases;
import parser.AliasesImpl;
import parser.commands.CommandData;

import static ui.view.CelebiViewController.Skin;

/*
 * Possible errors and solutions: 
 * 1. configuration file not found -> recreate write in default settings
 * 2. configuration file cannot be parsed -> rewrite in default settings
 * 3. configuration file does not have certain property -> set to default
 */
public class Configuration implements ConfigurationInterface {

    private static final String CONFIG_DIRECTORY = "config.json";
    
    private static final String KEY_STORAGE_LOCATION = "STORAGE_LOCATION";
    private static final String KEY_DEFAULT_START_TIME = "DEFAULT_START_TIME";
    private static final String KEY_DEFAULT_END_TIME = "DEFAULT_END_TIME";
    private static final String KEY_ALIAS_MAPPINGS = "ALIAS_MAPPINGS";
    private static final String KEY_SKIN = "SKIN";
    
    private static final String VALUE_SKIN_DAY = Skin.DAY.name();
    private static final String VALUE_SKIN_NIGHT = Skin.NIGHT.name();
    
    private static final String DEFAULT_VALUE_STORAGE_LOCATION = "";
    private static final String DEFAULT_VALUE_DEFAULT_START_TIME = "08:00";
    private static final String DEFAULT_VALUE_DEFAULT_END_TIME = "23:59";
    private static final String DEFAULT_VALUE_SKIN = VALUE_SKIN_DAY;
    
    private static final String MESSAGE_INVALID_STORAGE_LOCATION = "%1$s is not a valid path";
    private static final String MESSAGE_INVALID_DEFAULT_START_TIME = "Start time %1$s is invalid";
    private static final String MESSAGE_INVALID_DEFAULT_END_TIME = "End time %1$s is invalid";
    private static final String MESSAGE_INVALID_ALIAS_MAP = "Corrupted Map (format or values)%s";
    private static final String MESSAGE_INVALID_SKIN = "Skin %1s does not exist";
    
    private static final String MESSAGE_RESET_STORAGE_LOCATION = "Storage location set to " + DEFAULT_VALUE_STORAGE_LOCATION;
    private static final String MESSAGE_RESET_DEFAULT_START_TIME = "Storage location set to " + DEFAULT_VALUE_DEFAULT_START_TIME;
    private static final String MESSAGE_RESET_DEFAULT_END_TIME = "Storage location set to " + DEFAULT_VALUE_DEFAULT_END_TIME;
    private static final String MESSAGE_RESET_NEW_ALIAS_MAP = "";
    private static final String MESSAGE_RESET_SKIN = "";
    
    private static Configuration instance = null;

    private File configFile;
    private Scanner configReader;
    private Writer configWriter;
    private String configStorageLocation, configDefaultStartTime, configDefaultEndTime, configSkin;
    private Map<String, String> configUserCmdAliases;
    private final Aliases ALIASES;

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
        ALIASES = AliasesImpl.getInstance();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
            
            if (configStorageLocation == null) {
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

            //TODO new alias code
            configUserCmdAliases = (Map) parsedResult.get(KEY_ALIAS_MAPPINGS);
            if (!isValidAliasMap(configUserCmdAliases)) {
            	clearUserAliases();
            	logError(MESSAGE_INVALID_ALIAS_MAP, "", MESSAGE_RESET_NEW_ALIAS_MAP);
            }
            
            configSkin = (String) parsedResult.get(KEY_SKIN);
            if (!isValidSkin(configSkin)) {
            	resetSkin();
            	logError(MESSAGE_INVALID_SKIN, configSkin, MESSAGE_RESET_SKIN);
            }
            
            writeBack();
        } catch (ClassCastException e) {
        	resetAll();
        }
    }

    // getters

    @Override
    public String getDefaultUsrFileDirectory() {
    	return DEFAULT_VALUE_STORAGE_LOCATION;
    }

    @Override
    public String getUsrFileDirectory() {
        return configStorageLocation;
    }

    @Override
    public Time getDefaultStartTime() {
        return new Time(configDefaultStartTime);
    }

    @Override
    public Time getDefaultEndTime() {
        return new Time(configDefaultEndTime);
    }
    
    @Override
    public String getSkin() {
    	return configSkin;
    }

	//@@author A0131891E
    @Override
    public boolean isUserAlias(String alias) {
    	return configUserCmdAliases.containsKey(alias);
    }
    @Override
    public String getUserAliasTargetName(String alias) {
    	return configUserCmdAliases.get(alias);
    }
    
    // setters
    //@@author A0133920N

    @Override
    public void setUsrFileDirector(String newDir) throws IOException {
		configStorageLocation = newDir;
		
		// write to the configuration file
        writeBack();
        Log.log("usr file moved to " + newDir, this.getClass());
    }

    @Override
    public void setDefaultStartTime(String newTime) throws IOException {
    	if (isValidTime(newTime)) {
    		configDefaultStartTime = newTime;
    		
    		// write to the configuration file
            writeBack();
            Log.log("default start time reset to " + newTime, this.getClass());
    	} 
    	//TODO how would caller know if newTime is accepteed as valid time
    }
    
    @Override
    public void setDefaultEndTime(String newTime) throws IOException {
    	if (isValidTime(newTime)) {
    		configDefaultEndTime = newTime;
    		
    		// write to the configuration file
            writeBack();
            Log.log("default end time reset to " + newTime, this.getClass());
    	}
    	//TODO how would caller know if newTime is accepteed as valid time
    }
    
    @Override
    public void setSkin(String skin) throws IOException {
    	if (isValidSkin(skin)) {
    		configSkin = skin;
    		writeBack();
    		Log.log("skin reset to " + configSkin, this.getClass());
    	}
    }
    
    //@@author A0131891E
    @Override
    public void setUserAlias(String alias, String targetName) throws IOException {
    	assert( alias != null 
    			&& targetName != null 
    			&& !alias.equals("")
    			&& !targetName.equals("")
    			);
    	
    	configUserCmdAliases.put(alias, targetName);
    	
    	writeBack();
    	Log.log("new alias mapping added: " + alias + "-->" + targetName);
    }
    //@@author A0131891E
    @Override
    public void removeUserAlias(String alias) throws IOException {
    	assert(alias != null && alias.length() != 0);
    	configUserCmdAliases.remove(alias);
    	
    	writeBack();
    	Log.log("alias mapping removed: " + alias);
    }
    
    // resetterss
    //@@author A0133920N
    
    private void resetAll() throws IOException {
        // set all properties to default value
    	configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;
    	configDefaultStartTime = DEFAULT_VALUE_DEFAULT_START_TIME;
    	configDefaultEndTime = DEFAULT_VALUE_DEFAULT_END_TIME;
    	//TODO new alias code
    	configUserCmdAliases = new LinkedHashMap<>();

        // write to the configuration file
        writeBack();
    }
    
    public void resetStorageLocation() throws IOException {
    	configStorageLocation = DEFAULT_VALUE_STORAGE_LOCATION;
    	writeBack();
    }
    
    private void resetDefaultStartTime() throws IOException {
    	configDefaultStartTime = DEFAULT_VALUE_DEFAULT_START_TIME;
    	writeBack();
    }
    
    private void resetDefaultEndTime() throws IOException {
    	configDefaultEndTime = DEFAULT_VALUE_DEFAULT_END_TIME;
    	writeBack();
    }
    
    private void resetSkin() throws IOException {
    	configSkin = DEFAULT_VALUE_SKIN;
    	writeBack();
    }
    
    //@@author A0131891E
    @Override
	public void clearUserAliases() throws IOException {
    	configUserCmdAliases = new LinkedHashMap<>();
    	writeBack();
    	Log.log("All user alias mappings cleared");
    }
	
	// file data validation
	
    //@@author A0131891E
    @SuppressWarnings("rawtypes")
	private boolean isValidAliasMap(Map aliasMap) {
		if (aliasMap == null) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final Set<Map.Entry> entries = aliasMap.entrySet();
		
		// check each entry
		for (Map.Entry entry : entries) {
			
			if ( !(entry.getKey() instanceof String) || !(entry.getValue() instanceof String) ) {
				return false; // keys and vals must be Strings
			}
			
			if ( entry.getKey().equals("") || entry.getValue().equals("") ) {
				return false; // should have no empty string as key or value
			}
			
			// check target command
			try {
				CommandData.Type target = Enum.valueOf(CommandData.Type.class, (String) entry.getValue());
				if (target == CommandData.Type.INVALID) {
					return false; // cannot map to invalid command
				}
			} catch (IllegalArgumentException iae) {
				return false; // value not a valid Command.Type enum value
			}
			
			// check alias
			if (ALIASES.isReservedCmdAlias((String)entry.getKey())) {
				return false; // reserved alias names cannot be redirected
			}
		}
	
		return true; // all tests passed, phew!
	}
	
    //@@author A0133920N

    private boolean isValidTime(String str) {
    	if (str == null) {
    		return false;
    	}
    	
    	Time t = new Time(str);
    	return t.isValid();
    }
    
    private boolean isValidSkin(String str) {
    	if (str == null) {
    		return false;
    	}
    	
    	String upper = str.toUpperCase();
    	
    	return upper.equals(VALUE_SKIN_DAY) || upper.equals(VALUE_SKIN_NIGHT);
    }

    private void logError(String invalidMsg, String arg, String resetMsg) {
    	String formatted = Utilities.formatString(invalidMsg, arg);
    	Log.log(formatted);
    	Log.log(resetMsg);
    }
    
    @SuppressWarnings("unchecked")
	private void writeBack() throws IOException {
        JSONObject configJson = new JSONObject();
        configJson.put(KEY_STORAGE_LOCATION, configStorageLocation);
        configJson.put(KEY_DEFAULT_START_TIME, configDefaultStartTime);
        configJson.put(KEY_DEFAULT_END_TIME, configDefaultEndTime);
        configJson.put(KEY_SKIN, configSkin);
        //TODO new alias code
        configJson.put(KEY_ALIAS_MAPPINGS, configUserCmdAliases);

        configWriter = new BufferedWriter(new FileWriter(CONFIG_DIRECTORY));
        String text = JSONValue.toJSONString(configJson);
        configWriter.write(text);
        configWriter.close();
        configWriter = null;
    }

}

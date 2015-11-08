//@@author A0133920N
package storage;

import java.io.IOException;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	
	private static Storage instance;
	private static boolean _isTestMode;
	
    private Storage() {
    }
    
    public static Storage getStorage() {
    	if (instance == null) {
    		instance = new Storage();
    	}
    	
    	return instance;
    }

    public void init() {
        Log.log("Storage Init");
        try {
            connectToDatabase();
            Log.log("Storage Init complete");
        } catch (IOException e) {
        	Log.log("Storage Init Fail");
        } 
    }

    public void close() {
        Log.log("Storage closed");
        Database.disconnect();
    }

    private void connectToDatabase() throws IOException {
    	ConfigurationInterface setting = Configuration.getInstance();
        String fileLoc = setting.getUsrFileDirectory();
        
        boolean connectResult = Database.connect(fileLoc, _isTestMode);
        
        if (!connectResult) {
        	Log.log("Location invalid");
        	setting.resetStorageLocation();
        	connectToDatabase();
        }
    	
        Database.load();
    }

    @Override
    public boolean save(Task c) {
        TaskJson cj = new TaskJson(c);
        int id = c.getId();
        
        try {
        	if (id <= 0) {
                id = Database.insert(cj);
                c.setId(id);
            } else if (Database.selectById(id) != null) {
                Database.update(cj);
            } else {
                Database.restore(cj);
            }
        } catch (IOException e) {
        	return false;
        }
        
        return true;
    }

    @Override
    public boolean load(TasksBag c) {
        List<TaskJson> data = Database.selectAll();
        for (int i = 0; i < data.size(); i++) {
            c.addTask(data.get(i).toCelebi());
        }

        boolean loadSuccessful = true;
        return loadSuccessful;
    }

    @Override
    public boolean delete(Task c) {
    	try {
    		Database.delete(c.getId());
    	} catch (IOException e) {
        	return false;
        }
        
        return true;
    }

    @Override
    public boolean moveFileTo(String destination) throws IOException {
    	return Database.moveTo(destination, _isTestMode);
    }
       
    // Methods below are only used for Storage unit tests
    void openTestMode() {
    	_isTestMode = true;
    }
    
    void closeTestMode() {
    	_isTestMode = false;
    }
}

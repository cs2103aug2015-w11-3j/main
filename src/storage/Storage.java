//@@author A0133920N
package storage;

import java.io.IOException;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	
	private static Storage instance;
	private static boolean _isTestMode;
	private ConfigurationInterface config;
	
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
    	config = Configuration.getInstance();
    	 System.out.println("wocap");
        connectToDatabase();
        Log.log("Storage Init complete");
    }

    public void close() {
        Log.log("Storage closed");
        Database.disconnect();
    }

    private void connectToDatabase() {        
        boolean connectSuccess = tryConnect();
        
        if (!connectSuccess) {
        	Log.log("Fail to connect to storage file");
        	config.resetStorageLocation();
        	tryConnect();
        }    	
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
    public void moveFileTo(String destination) throws IOException {
    	Database.moveTo(destination, _isTestMode);
    }
    
    private boolean tryConnect() {
        String fileLoc = config.getUsrFileDirectory();
        
        return Database.connect(fileLoc, _isTestMode);
    }
       
    // Methods below are only used for Storage unit tests
    void openTestMode() {
    	_isTestMode = true;
    }
    
    void closeTestMode() {
    	_isTestMode = false;
    }
}

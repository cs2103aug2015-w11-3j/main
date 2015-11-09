//@@author A0133920N
package storage;

import java.io.IOException;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	
	private static Storage instance;
	private static boolean _isTestMode;
	private ConfigurationInterface config;
	private Database _database;
	
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
        connectToDatabase();
        Log.log("Storage Init complete");
    }

    public void close() {
    	assert(_database != null);
        _database.disconnect();
        Log.log("Storage closed");
    }

    private void connectToDatabase() {     
    	_database = Database.getDatabase();
        boolean connectSuccess = tryConnect();
        
        if (!connectSuccess) {
        	Log.log("Fail to connect to storage file");
        	config.resetStorageLocation();
        	tryConnect();
        }    	
    }

    @Override
    public boolean save(Task c) {
    	assert(_database != null);
    	
        TaskJson cj = new TaskJson(c);
        int id = c.getId();
        
        try {
        	if (id <= 0) {
                id = _database.insert(cj);
                c.setId(id);
            } else if (_database.selectById(id) != null) {
            	_database.update(cj);
            } else {
            	_database.restore(cj);
            }
        } catch (IOException e) {
        	return false;
        }
        
        return true;
    }

    @Override
    public boolean load(TasksBag c) {
    	assert(_database != null);
        List<TaskJson> data = _database.selectAll();
        for (int i = 0; i < data.size(); i++) {
            c.addTask(data.get(i).toTask());
        }

        boolean loadSuccessful = true;
        return loadSuccessful;
    }

    @Override
    public boolean delete(Task c) {
    	assert(_database != null);
    	try {
    		_database.delete(c.getId());
    	} catch (IOException e) {
        	return false;
        }
        
        return true;
    }

    @Override
    public void moveFileTo(String destination) throws IOException {
    	assert(_database != null);
    	_database.moveTo(destination, _isTestMode);
    }
    
    private boolean tryConnect() {
    	assert(_database != null);
    	
        String fileLoc = config.getUsrFileDirectory();
        return _database.connect(fileLoc, _isTestMode);
    }
       
    // Methods below are only used for Storage unit tests
    void openTestMode() {
    	_isTestMode = true;
    }
    
    void closeTestMode() {
    	_isTestMode = false;
    }
}

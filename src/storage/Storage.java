//@@author A0133920N

/*
 * @class: Storage
 * 
 * The controller of this package. When Storage is invoked by the Logic Component,
 * it controls Database on how to operate the storage file and the persist data.
 */

package storage;

import java.io.IOException;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	
	// Data used to apply Singleton Pattern
	private static Storage _instance;
	private static boolean _isTestMode;
	
	// Other class used in this class
	private ConfigurationInterface _config;
	private Database _database;
	
	/* 
	 * Use Singleton Pattern, make constructor private and define 
	 * a public method to get the only instance;
	 */
    private Storage() {
    	
    }
    
    public static Storage getStorage() {
    	if (_instance == null) {
    		_instance = new Storage();
    	}
    	
    	return _instance;
    }

    // Public Methods
    
    /*
     * @see storage.StorageInterface#init()
     */
    @Override
    public void init() {
        Log.log("Storage Init");
        getConfig();
        connectToDatabase();
        Log.log("Storage Init complete");
    }

    /*
     * @see storage.StorageInterface#close()
     */
    @Override
    public void close() {
    	assert(_database != null);
    
        _database.disconnect();
        Log.log("Storage closed");
    }

    /*
     * @see storage.StorageInterface#save(common.Task)
     */
    @Override
    public boolean save(Task c) {
    	assert(_database != null);
    	
        TaskJson cj = new TaskJson(c);
        int id = c.getId();
        
        // Based on the input Task, decide which method of Databse to call
        try {
        	if (id <= 0) {
        		// insert new task if the input has no id
                id = _database.insert(cj);
                c.setId(id);
            } else if (existsInDatabase(id)) {
            	_database.update(cj);
            } else {
            	// if input Task has an id but not exists in Database, meaning
            	// it has been deleted, then restore it
            	_database.restore(cj);
            }
        } catch (IOException e) {
        	return false;
        }
        
        return true;
    }

    /*
     * @see storage.StorageInterface#load(common.TasksBag)
     */
    @Override
    public boolean load(TasksBag c) {
    	assert(_database != null);
    	
        List<TaskJson> data = _database.selectAll();
        for (int i = 0; i < data.size(); i++) {
            c.addTask(data.get(i).toTask());
        }
        
        return true;
    }

    /*
     * @see storage.StorageInterface#delete(common.Task)
     */
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
    
    /*
     * @see storage.StorageInterface#moveFileTo(java.lang.String)
     */
    @Override
    public void moveFileTo(String destination) throws IOException {
    	assert(_database != null);
    	
    	_database.moveTo(destination, _isTestMode);
    }
    
    // End of Public Methods
    
    
    // Private Methods
    
    /*
     * Connect to the storage file
     */
    private void connectToDatabase() {     
    	assert(_config != null);
    	boolean connectSuccess;
    	
    	_database = Database.getDatabase();
        connectSuccess = tryConnect();
        
        // if the first attempt to connect fails, reset the storage file
        // to default location and re-connect
        if (!connectSuccess) {
        	Log.log("Fail to connect to storage file");
        	_config.resetStorageLocation();
        	tryConnect();
        }    	
    }

    /*
     * Attempt to connect to the database
     * @return true if found or created, otherwise false
     */
    private boolean tryConnect() {
    	assert(_config != null && _database != null);
    	
        String fileLoc = _config.getUsrFileDirectory();
        return _database.connect(fileLoc, _isTestMode);
    }
    
    /*
     * Initialize configuration
     */
    private void getConfig() {
    	_config = Configuration.getInstance();
    }
    
    /*
     * Check whether the task with the input ID exists in database
     */
    private boolean existsInDatabase(int id) {
    	assert( _database != null);
    	
    	return _database.selectById(id) != null;
    }
   
    // End of Private Methods
       
    
    /*
     * Test Methods
     * ( Methods below are only used for Storage unit tests )
     */
    void openTestMode() {
    	_isTestMode = true;
    }
    
    void closeTestMode() {
    	_isTestMode = false;
    }
}

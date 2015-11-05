//@@author A0133920N
package storage;

import java.io.IOException;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	
	private static Storage instance;
	
    private Storage() {
    }
    
    public static Storage getStorage() {
    	if (instance == null) {
    		instance = new Storage();
    	}
    	
    	return instance;
    }

    public void init(boolean isTestMode) {
        Log.log("Storage Init");
        try {
            connectToDatabase(isTestMode);
            Log.log("Storage Init complete");
        } catch (BadFileContentException e) {
            Log.log("Storage Init Fail");
        } catch (IOException e) {
        	Log.log("Storage Init Fail");
        } 
    }

    public void close() {
        Log.log("Storage closed");
        Database.disconnect();
    }

    private void connectToDatabase(boolean isTestMode) throws IOException, BadFileContentException {
    	ConfigurationInterface setting = Configuration.getInstance();
        String fileLoc = setting.getUsrFileDirectory();
        
        boolean connectResult = Database.connect(fileLoc, isTestMode);
        
        if (!connectResult) {
        	Log.log("Location in ");
        	setting.resetStorageLocation();
        	connectToDatabase(isTestMode);
        }
    	
        Database.load();
    }

    @Override
    public boolean save(Task c) {
        TaskJson cj = new TaskJson(c);
        int id = c.getId();
        if (id <= 0) {
            id = Database.insert(cj);
            c.setId(id);
        } else if (Database.selectById(id) != null) {
            Database.update(cj);
        } else {
            Database.restore(cj);
        }

        boolean saveSuccessful = true;
        return saveSuccessful;
    }

    @Override
    public boolean load(String s, TasksBag c) {
        List<TaskJson> data = Database.selectAll();
        for (int i = 0; i < data.size(); i++) {
            c.addTask(data.get(i).toCelebi());
        }

        boolean loadSuccessful = true;
        return loadSuccessful;
    }

    @Override
    public boolean delete(Task c) {
        Database.delete(c.getId());
        return true;
    }

    @Override
    public boolean moveFileTo(String destination) throws IOException {
    	return Database.moveTo(destination);
    }
}

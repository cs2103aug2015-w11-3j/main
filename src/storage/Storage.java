//@@author A0133920N
package storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	
    public Storage() {

    }

    public void init() {
        Log.log("Storage Init");
        Log.log("Storage Init complete");
        try {
            connectToDatabase();
        } catch (IOException e) {
            Log.log("Storage Init Fail");
        } catch (BadFileContentException e) {
            Log.log("Storage Init Fail");
        }
    }

    public void close() {
        Log.log("Storage closed");
        Database.disconnect();
    }

    private void connectToDatabase() throws IOException, BadFileContentException {
    	ConfigurationInterface setting = Configuration.getInstance();
        String fileLoc = setting.getUsrFileDirectory();
    	Database.connect(fileLoc);
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
    public boolean moveFileTo(String destination) {
        try {
            return Database.moveTo(destination);
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}

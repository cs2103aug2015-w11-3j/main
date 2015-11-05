//@@author A0133920N
package storage;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import common.Configuration;
import common.ConfigurationInterface;
import common.Task;
import common.TasksBag;

public class StorageTest {
	private final static String TEST_FILENAME = "test_task.json";
	
	private final String TESTFILE_CONTENT_EMPTY = "";
	private final String TESTFILE_CONTENT_INVALID_JSON = "invalid json";
	private final String TESTFILE_CONTENT_INVALID_DATE = "[{\"ID\":\"1\","
			+ "\"NAME\":\"testInvalidDate\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }]";
	
	private final String NOT_EXSITS_FILEPATH = "/nonExists";
	
	
    @Before 
    public void openTestMode() throws IOException {
        Storage.openTestMode();
    }
    
    @After 
    public void closeTestMode() {
        Storage.closeTestMode();
    }
    
    @Test
    /* Test if the recorded storage file path does not exist
     * Expected: storage file path reset to default (stored in Configuration), 
     *           re-create storage file
     */
    public void testInvalidPath() throws IOException {
    	ConfigurationInterface config = Configuration.getInstance();
    	String defaultPath = config.getDefaultUsrFileDirectory();
    	String currentConfigPath;
    	File path = new File(NOT_EXSITS_FILEPATH);
    	File defaultFile = new File(defaultPath, TEST_FILENAME);
    	StorageInterface storage;
    	
    	config.setUsrFileDirector(NOT_EXSITS_FILEPATH);
    	
    	// check path in configuration has been reset to a non-existing path
    	currentConfigPath = config.getUsrFileDirectory();
    	Assert.assertEquals(currentConfigPath, NOT_EXSITS_FILEPATH);
    	
    	// check this path does not exist
    	deleteIfExists(path);
    	Assert.assertFalse(path.exists());
    	
    	// check the default file in default location does not exist, 
    	// in order to make sure it is 're-created' below
    	deleteIfExists(defaultFile);
    	Assert.assertFalse(defaultFile.exists());
    	
    	storage = Storage.getStorage();
    	storage.init();
    	
    	// check path in configuration has been reset to default
    	currentConfigPath = config.getUsrFileDirectory();
    	Assert.assertEquals(currentConfigPath, defaultPath);
    	
    	// check the file in default path has been re-created
    	Assert.assertTrue(defaultFile.exists());
    }
    
    @Test
    // Test if path exists but the storage file does not exist
    public void testStorageFileNotExists() throws IOException {
    	ConfigurationInterface config = Configuration.getInstance();
    	String defaultPath = config.getDefaultUsrFileDirectory();
    	String currentConfigPath;
    	File defaultFile = new File(defaultPath, TEST_FILENAME);
    	StorageInterface storage;
    	
    	// check path in configuration has been reset to default
    	config.resetStorageLocation();
    	currentConfigPath = config.getUsrFileDirectory();
    	Assert.assertEquals(currentConfigPath, defaultPath);
    	
    	// check the default file in default location does not exist, 
    	// in order to make sure it is 're-created' below
    	deleteIfExists(defaultFile);
    	Assert.assertFalse(defaultFile.exists());
    	
    	storage = Storage.getStorage();
    	storage.init();
    	
    	// check the file in default path has been re-created
    	Assert.assertTrue(defaultFile.exists());
    }

    // @Test 
    public void test() {
        TasksBag cb;
        int size;

        Storage s = Storage.getStorage();
        s.init();
        Task c1 = new Task("storage dummy test1", createDate(2015, 10, 10, 0, 0), createDate(2015, 11, 11, 0, 0));
        Task c2 = new Task("storage dummy test2", createDate(2016, 10, 10, 0, 0), createDate(2016, 11, 11, 0, 0));
        Task c3 = new Task("storage dummy test3", createDate(2017, 10, 10, 0, 0), createDate(2017, 11, 11, 0, 0));

        boolean result = s.save(c1);
        Assert.assertEquals(true, result);

        s.save(c2);
        s.save(c3);

        cb = new TasksBag();
        s.load("", cb);
        size = cb.size();
        Assert.assertEquals(3, size);

        c1.setName("new");
        s.save(c1);

        cb = new TasksBag();
        s.load("", cb);

        Task ct = cb.getTask(0);
        String ctName = ct.getName();
        Assert.assertEquals("new", ctName);

        s.delete(c2);

        cb = new TasksBag();
        s.load("", cb);
        size = cb.size();
        Assert.assertEquals(2, size);

        s.close();
    }

    // private methods
    private static Date createDate(int year, int month, int day, int hour, int minute) {
        Calendar calender = Calendar.getInstance();
        calender.set(year, month, day, hour, minute);
        return new Date(calender.getTimeInMillis());
    }
    
    private static void deleteIfExists(File f) {
    	if (f.exists()) {
    		f.delete();
    	}
    }
    
    private static void createIfNotExists(File f) throws IOException {
    	if (!f.exists()) {
    		f.createNewFile();
    	}
    }
}

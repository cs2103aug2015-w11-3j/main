//@@author A0133920N
package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import org.json.simple.JSONValue;
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
	private final String TESTFILE_CONTENT_INVALID_JSON = "[invalid json)";
	private final String TESTFILE_CONTENT_INVALID_JSON_ARRAY = "\"foo\":\"bar\"";
	
	private final String VALID_TASK = "{ \"ID:\" \"1\","
			+ "\"NAME\":\"test valid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_NO_ID = "{ "
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_ID_1 = "{ \"ID:\" \"0\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_ID_2 = "{ \"ID:\" \"-1\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_ID_3 = "{ \"ID:\" \"1.5\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_ID_4 = "{ \"ID:\" \"bla\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_NO_NAME = "{ \"ID:\" \"2\","
			+ "\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_NAME_1 = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_NO_START_DATE = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_NO_END_DATE = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_DATE_1 = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"bla\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_DATE_2 = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"bla\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_INVALID_DATE_3 = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"2015-11-06 08:00\","
			+ "\"DATE_END\":\"2014-11-06 08:00\","
			+ "\"IS_COMPLETED\":\"false\" }";
	
	private final String INVALID_TASK_NO_IS_COMPLETE = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\""
			+ "}";
	
	private final String INVALID_TASK_INVALID_IS_COMPLETE = "{ \"ID:\" \"2\","
			+ "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
			+ "\"IS_COMPLETED\":\"bla\" }";
	
	private final String TESTFILE_CONTENT_INITIAL = "[]";
	
	private final String NOT_EXSITS_FILEPATH = "/notExists";	
	
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
    /* Test if path exists but the storage file does not exist
     * Expected: re-create the storage file
     */
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
    
    @Test
    /* Test if file exists but the content is empty
     * Expected: file is initialized to an empty JSON array
     */ 
    public void testLoadEmpty() throws IOException {
    	testInvalidLoad(TESTFILE_CONTENT_EMPTY);
    }
    
    @Test
    /* Test if file exists but the content is not in valid JSON format
     * Expected: file is initialized to an empty JSON array
     */ 
    public void testLoadInvalidJSON() throws IOException {
    	testInvalidLoad(TESTFILE_CONTENT_INVALID_JSON);
    }
    
    @Test
    /* Test if file content is in JSON format but not an array
     * Expected: file is initialized to an empty JSON array
     */ 
    public void testLoadInvalidJSONArray() throws IOException {
    	testInvalidLoad(TESTFILE_CONTENT_INVALID_JSON_ARRAY);
    }
    
    @Test
    /* Test if file content is JSON Array but some entries cannot be parsed to tasks
     * Expected: those entries failed to be parsed are discarded, without affecting others
     */ 
    public void testLoadInvalidTask() throws IOException {
    	
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
    
    /* Test if file exists but the content is corrupted, 
     * input could be empty, invalid JSON format or invalid Task format
     */ 
    private void testInvalidLoad(String content) throws IOException {
    	ConfigurationInterface config = Configuration.getInstance();
    	String path = config.getUsrFileDirectory();
    	StorageInterface storage;
    	TasksBag tb;
    	String fileContent;
    	
    	File storageFile = new File(path, TEST_FILENAME);
    	createIfNotExists(storageFile);
    	Assert.assertTrue(storageFile.exists());
    	
    	writeToFile(storageFile, content);
    	fileContent = readFile(storageFile);
    	Assert.assertEquals(fileContent, content);
    	
    	storage = Storage.getStorage();
    	storage.init();
    	
    	tb = new TasksBag();
    	storage.load("", tb);
    	Assert.assertEquals(tb.size(), 0);
    
    	fileContent = readFile(storageFile);
    	System.out.println(fileContent);
    	System.out.println(TESTFILE_CONTENT_INITIAL);
    	Assert.assertEquals(fileContent, TESTFILE_CONTENT_INITIAL);
    }
    
    private void writeToFile(File f, String content) throws IOException {
    	Writer writer = new BufferedWriter(new FileWriter(f));
    	writer.write(content);
    	writer.close();
    }
    
    private String readFile(File f) throws FileNotFoundException {
    	String content = "";
    	Scanner scanner = new Scanner(f);
    	scanner.useDelimiter("\\Z");
    	
		if (scanner.hasNext()) {
			content = scanner.next();
		} 

		scanner.close();
		
		return content;
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

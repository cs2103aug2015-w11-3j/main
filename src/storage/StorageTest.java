//@@author A0133920N
package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

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

    private final String VALID_TASK = "{\"ID\":\"1\","
            + "\"NAME\":\"test valid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\"}";

    private final String VALID_TASK_2 = "{\"ID\":\"2\","
            + "\"NAME\":\"test valid task 2\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\"}";

    private final String INVALID_TASK_NO_ID = "{ "
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_ID_1 = "{ \"ID\": \"0\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_ID_2 = "{ \"ID\": \"-1\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_ID_3 = "{ \"ID\": \"1.5\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_ID_4 = "{ \"ID\": \"bla\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_NO_NAME = "{ \"ID\": \"2\"," + "\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_NAME_1 = "{ \"ID\": \"2\","
            + "\"NAME\":\"\",\"DATE_START\":\"null\",\"DATE_END\":\"null\"," + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_NO_START_DATE = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_END\":\"null\"," + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_NO_END_DATE = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\"," + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_DATE_1 = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"bla\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_DATE_2 = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"bla\","
            + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_INVALID_DATE_3 = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"2015-11-06 08:00\","
            + "\"DATE_END\":\"2014-11-06 08:00\"," + "\"IS_COMPLETED\":\"false\" }";

    private final String INVALID_TASK_NO_IS_COMPLETE = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\"" + "}";

    private final String INVALID_TASK_INVALID_IS_COMPLETE = "{ \"ID\": \"2\","
            + "\"NAME\":\"test invalid task\",\"DATE_START\":\"null\",\"DATE_END\":\"null\","
            + "\"IS_COMPLETED\":\"bla\" }";

    private final String[] INVALID_TASKS = { INVALID_TASK_NO_ID, INVALID_TASK_INVALID_ID_1, INVALID_TASK_INVALID_ID_2,
            INVALID_TASK_INVALID_ID_3, INVALID_TASK_INVALID_ID_4, INVALID_TASK_NO_NAME, INVALID_TASK_INVALID_NAME_1,
            INVALID_TASK_NO_START_DATE, INVALID_TASK_NO_END_DATE, INVALID_TASK_INVALID_DATE_1,
            INVALID_TASK_INVALID_DATE_2, INVALID_TASK_INVALID_DATE_3, INVALID_TASK_NO_IS_COMPLETE,
            INVALID_TASK_INVALID_IS_COMPLETE };

    private final static String TESTFILE_CONTENT_INITIAL = "[]";

    private final static String NOT_EXSITS_FILEPATH = "notExists";
    private final static String MOVE_TO_FILEPATH = "newPath";

    private ConfigurationInterface config;
    private String defaultPath;
    private File defaultFile;
    private Storage storage;

    @Before
    public void openTestMode() throws IOException {
        config = Configuration.getInstance();
        defaultPath = config.getDefaultUsrFileDirectory();
        defaultFile = new File(defaultPath, TEST_FILENAME);

        resetDefaultPath();
        createFileIfNotExists(defaultFile);
        writeToFile(defaultFile, TESTFILE_CONTENT_INITIAL);

        storage = Storage.getStorage();
        storage.openTestMode();
    }

    @After
    public void closeTestMode() {
        storage.closeTestMode();
        storage.close();
    }

    @Test
    /*
     * Test if the recorded storage file path does not exist Expected: storage
     * file path reset to default (stored in Configuration), re-create storage
     * file
     */
    public void testInvalidPath() throws IOException {
        File path = new File(NOT_EXSITS_FILEPATH);

        config.setUsrFileDirector(NOT_EXSITS_FILEPATH);
        Assert.assertEquals(config.getUsrFileDirectory(), NOT_EXSITS_FILEPATH);

        deleteIfExists(path);

        // check the default file in default location does not exist,
        // in order to make sure it is 're-created' below
        deleteIfExists(defaultFile);

        storage = Storage.getStorage();
        storage.init();

        Assert.assertEquals(config.getUsrFileDirectory(), defaultPath);

        assertFileExists(defaultFile);
    }

    @Test
    /*
     * Test if path exists but the storage file does not exist Expected:
     * re-create the storage file
     */
    public void testStorageFileNotExists() throws IOException {
        // check the default file in default location does not exist,
        // in order to make sure it is 're-created' below
        deleteIfExists(defaultFile);

        storage = Storage.getStorage();
        storage.init();

        // check the file in default path has been re-created
        assertFileExists(defaultFile);
    }

    @Test
    /*
     * Test if file exists but the content is empty Expected: file is
     * initialized to an empty JSON array
     */
    public void testLoadEmpty() throws IOException {
        testInvalidLoad(TESTFILE_CONTENT_EMPTY);
    }

    @Test
    /*
     * Test if file exists but the content is not in valid JSON format Expected:
     * file is initialized to an empty JSON array
     */
    public void testLoadInvalidJSON() throws IOException {
        testInvalidLoad(TESTFILE_CONTENT_INVALID_JSON);
    }

    @Test
    /*
     * Test if file content is in JSON format but not an array Expected: file is
     * initialized to an empty JSON array
     */
    public void testLoadInvalidJSONArray() throws IOException {
        testInvalidLoad(TESTFILE_CONTENT_INVALID_JSON_ARRAY);
    }

    @Test
    /*
     * Test if file content is JSON Array but some entries cannot be parsed to
     * tasks Expected: those entries failed to be parsed are discarded, without
     * affecting others
     */
    public void testLoadInvalidTasks() throws IOException {
        TasksBag tb;
        String fileContent;

        for (int i = 0; i < INVALID_TASKS.length; i++) {
            writeToFile(defaultFile, "[" + VALID_TASK + INVALID_TASKS[i] + "]");

            storage.init();

            tb = new TasksBag();
            storage.load(tb);
            Assert.assertEquals(1, tb.size());

            fileContent = readFile(defaultFile);
            Assert.assertEquals(fileContent, "[" + VALID_TASK + "]");
            storage.close();
        }
    }

    @Test
    /*
     * Test if file content is valid Expected: load successfully
     */
    public void testLoadValidTasks() throws IOException {
        TasksBag tb;

        writeToFile(defaultFile, "[" + VALID_TASK + VALID_TASK_2 + "]");

        storage.init();

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(2, tb.size());
        storage.close();
    }

    @Test
    /*
     * Test if save a new task Expected: load the saved task
     */
    public void testSaveNew() throws IOException {
        TasksBag tb;
        Task task = new Task("test", createDate(2015, 10, 10, 0, 0, 0), createDate(2015, 11, 11, 0, 0, 0));
        Task outTask;
        storage.init();

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 0);

        boolean result = storage.save(task);
        Assert.assertTrue(result);

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 1);

        outTask = tb.getTask(0);
        assertTaskIdentical(task, outTask);
    }

    @Test
    /*
     * Test if save a new task Expected: load the saved task
     */
    public void testSaveUpdate() throws IOException {
        Task task = new Task("test", createDate(2015, 10, 10, 0, 0, 0), createDate(2015, 11, 11, 0, 0, 0));
        storage.init();

        testSaved(task, 0);

        task.setName("haha");
        testSaved(task, 0);

        task.setStart(createDate(2015, 5, 10, 0, 0, 0));
        testSaved(task, 0);

        task.setEnd(createDate(2016, 5, 10, 1, 0, 0));
        testSaved(task, 0);

        task.setComplete(true);
        ;
        testSaved(task, 0);
    }

    @Test
    /*
     * Test if delete a task Expected: task gets removed from the storage file
     */
    public void testDelete() throws IOException {
        TasksBag tb;
        Task task = new Task("test", createDate(2015, 10, 10, 0, 0, 0), createDate(2015, 11, 11, 0, 0, 0));
        storage.init();
        storage.save(task);

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 1);

        storage.delete(task);
        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 0);
    }

    @Test
    /*
     * Test if delete a task Expected: task gets removed from the storage file
     */
    public void testRestore() throws IOException {
        TasksBag tb;
        Task task = new Task("test", createDate(2015, 10, 10, 0, 0, 0), createDate(2015, 11, 11, 0, 0, 0));
        Task outTask;
        storage.init();
        storage.save(task);

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 1);

        storage.delete(task);
        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 0);

        storage.save(task);
        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 1);
        outTask = tb.getTask(0);
        assertTaskIdentical(task, outTask);
    }

    @Test
    /*
     * Test if move storage file Expected: file successfully moved to a new
     * directory
     */
    public void testMoveSuccess() throws IOException {
        TasksBag tb;
        Task task = new Task("test", createDate(2015, 10, 10, 0, 0, 0), createDate(2015, 11, 11, 0, 0, 0));
        Task outTask;
        File newPath = new File(MOVE_TO_FILEPATH);
        File newFile = new File(MOVE_TO_FILEPATH, TEST_FILENAME);

        storage.init();
        storage.save(task);

        createFolderIfNotExists(newPath);
        deleteIfExists(newFile);

        storage.moveFileTo(MOVE_TO_FILEPATH);
        assertFileExists(newFile);

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(tb.size(), 1);

        outTask = tb.getTask(0);
        assertTaskIdentical(task, outTask);
        storage.close();
        deleteIfExists(newPath);
    }

    @Test
    /*
     * Test if move storage file to a location which is not a folder Expected:
     * throw an exception indicating
     */
    public void testMoveFailNotFolder() throws IOException {
        File newPath = new File(MOVE_TO_FILEPATH);
        File newFile = new File(MOVE_TO_FILEPATH, TEST_FILENAME);

        createFileIfNotExists(newPath);
        deleteIfExists(newFile);

        storage.init();

        try {
            storage.moveFileTo(MOVE_TO_FILEPATH);
            Assert.fail("A FileSystemException should have been thrown");
        } catch (FileSystemException e) {
            return;
        } catch (Exception e) {
            Assert.fail(
                    String.format("A FileSystemException should have been thrown, but get %s", e.getClass().getName()));
        }
    }

    @Test
    /*
     * Test if move storage file to a location which is not a folder Expected:
     * throw an exception indicating
     */
    public void testMoveFailNoDir() throws IOException {
        File newPath = new File(MOVE_TO_FILEPATH);

        deleteIfExists(newPath);

        storage.init();

        try {
            storage.moveFileTo(MOVE_TO_FILEPATH);
            Assert.fail("A NoSuchFileException should have been thrown");
        } catch (NoSuchFileException e) {
            return;
        } catch (Exception e) {
            Assert.fail(
                    String.format("A NoSuchFileException should have been thrown, but get %s", e.getClass().getName()));
        }
    }

    @Test
    /*
     * Test if move storage file to a location which is not a folder Expected:
     * throw an exception indicating
     */
    public void testMoveFailAlreadyExists() throws IOException {
        File newPath = new File(MOVE_TO_FILEPATH);
        File newFile = new File(MOVE_TO_FILEPATH, TEST_FILENAME);

        createFolderIfNotExists(newPath);
        createFileIfNotExists(newFile);

        storage.init();

        try {
            storage.moveFileTo(MOVE_TO_FILEPATH);
            Assert.fail("A FileAlreadyExistsException should have been thrown");
        } catch (FileAlreadyExistsException e) {
            return;
        } catch (Exception e) {
            Assert.fail(String.format("A FileAlreadyExistsException should have been thrown, but get %s",
                    e.getClass().getName()));
        }
    }

    @Test
    public void testComplicatedCases() {
        TasksBag tb;

        Storage s = Storage.getStorage();
        s.init();
        Task c1 = new Task("storage test1", createDate(2015, 10, 10, 0, 0, 0), createDate(2015, 11, 11, 0, 0, 0));
        Task c2 = new Task("storage test2", createDate(2016, 10, 10, 0, 0, 0), createDate(2016, 11, 11, 0, 0, 0));
        Task c3 = new Task("storage test3", createDate(2017, 10, 10, 0, 0, 0), createDate(2017, 11, 11, 0, 0, 0));

        boolean result = s.save(c1);
        Assert.assertEquals(true, result);

        s.save(c2);
        s.save(c3);

        tb = new TasksBag();
        s.load(tb);
        Assert.assertEquals(tb.size(), 3);

        c1.setName("new");
        s.save(c1);

        tb = new TasksBag();
        s.load(tb);

        Task ct = tb.getTask(0);
        String ctName = ct.getName();
        Assert.assertEquals("new", ctName);

        s.delete(c2);

        tb = new TasksBag();
        s.load(tb);
        Assert.assertEquals(tb.size(), 2);

        s.close();
    }

    /*
     * Test if file exists but the content is corrupted, input could be empty,
     * invalid JSON format or invalid Task format
     */
    private void testInvalidLoad(String content) throws IOException {
        TasksBag tb;
        String fileContent;

        writeToFile(defaultFile, content);

        storage.init();

        tb = new TasksBag();
        storage.load(tb);
        Assert.assertEquals(0, tb.size());

        fileContent = readFile(defaultFile);
        Assert.assertEquals(fileContent, TESTFILE_CONTENT_INITIAL);
    }

    private void testSaved(Task task, int index) throws IOException {
        TasksBag tb = new TasksBag();
        Task outTask;

        storage.save(task);
        storage.load(tb);

        outTask = tb.getTask(index);
        assertTaskIdentical(task, outTask);
    }

    private void writeToFile(File f, String content) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(f));
        String fileContent;

        writer.write(content);
        writer.close();

        fileContent = readFile(f);
        Assert.assertEquals(fileContent, content);
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
    private static Date createDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calender = Calendar.getInstance();
        calender.set(year, month, day, hour, minute, second);
        return new Date(calender.getTimeInMillis());
    }

    private void resetDefaultPath() throws IOException {
        String currentConfigPath;
        String defaultPath = config.getDefaultUsrFileDirectory();

        config.resetStorageLocation();

        currentConfigPath = config.getUsrFileDirectory();
        Assert.assertEquals(currentConfigPath, defaultPath);
    }

    private void deleteIfExists(File f) {
        if (!f.exists()) {
            return;
        }

        if (f.isDirectory()) {
            deleteFolder(f);
        } else {
            deleteFile(f);
        }

        Assert.assertFalse(f.exists());
    }

    private void deleteFile(File f) {
        f.delete();
    }

    private void deleteFolder(File f) {
        String[] children = f.list();
        for (int i = 0; i < children.length; i++) {
            new File(f, children[i]).delete();
        }

        f.delete();
    }

    private void createFileIfNotExists(File f) throws IOException {
        if (!f.exists()) {
            f.createNewFile();
        }

        assertFileExists(f);
    }

    private void createFolderIfNotExists(File f) throws IOException {
        f.delete();

        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }

        if (!f.exists()) {
            f.mkdir();
        }

        assertFileExists(f);
    }

    private static void assertFileExists(File f) {
        Assert.assertTrue(f.exists());
    }

    private static void assertTaskIdentical(Task task1, Task task2) {
        System.out.println(task1.getId());
        Assert.assertEquals(task1.getId(), task2.getId());
        Assert.assertEquals(task1.getName(), task2.getName());
        Assert.assertEquals(task1.isCompleted(), task2.isCompleted());

        assertDateEqual(task1.getStart(), task2.getStart());
        assertDateEqual(task1.getEnd(), task2.getEnd());
    }

    private static void assertDateEqual(Date d1, Date d2) {
        int result = d1.compareTo(d2);

        Assert.assertEquals(result, 1);
    }
}

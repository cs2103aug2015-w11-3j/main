//@@author A0125546E
package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import common.Task;
import common.TasksBag;
import logic.exceptions.AlreadyMarkedException;
import logic.exceptions.AlreadyUnmarkedException;
import logic.exceptions.IllegalAccessCommandException;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import logic.exceptions.NoRedoActionException;
import logic.exceptions.NoUndoActionException;
import logic.exceptions.UnknownCommandException;

public class LogicTest {

    private static final String JSON_LOC_DEFAULT = "bin/task.json";
    private static final String JSON_LOC_TEMP = "bin/temp.json";
    private static final String JSON_LOC_FINAL = "bin/test/";
    static Logic logic;

    @Before
    public void initTest() {
        init();
    }

    @After
    public void cleanUpTest() {
        cleanUp();
    }

    @Test
    public void Coverage() {

        // Logic.java
        testFailException("", UnknownCommandException.class);
        testFailException("wrong add undo remove quit exit delete", UnknownCommandException.class);
        testFailException("khasd", UnknownCommandException.class);
        testFailException("undo", NoUndoActionException.class);
        testFailException("redo", NoRedoActionException.class);
        testFailException("remove 1", IllegalAccessCommandException.class);
        testFailException("remove 0", IllegalAccessCommandException.class);

        // AddAction.java
        testPass("add task 1");
        testPass("add task 2");
        testPass("undo");
        testPass("redo");

        // DeleteAction.java
        testFailException("delete 0", IllegalAccessCommandException.class);
        testFailException("delete 3", IllegalAccessCommandException.class);
        testPass("delete 1");
        testPass("undo");
        testPass("redo");

        // UpdateAction.java
        testFailException("update 0 name hello", IllegalAccessCommandException.class);
        testFailException("update 3 name hello", IllegalAccessCommandException.class);
        testPass("update 1 name hello");
        testFailException("update 1 start hello", UnknownCommandException.class);
        testPass("update 1 start 2015-10-10 15:00");
        testPass("update 1 end now");
        testPass("undo");
        testPass("redo");

        // Show
        testPass("show");
        testPass("show done");
        testFailException("delete 1", IllegalAccessCommandException.class);
        testPass("show undone");

        // Mark/Unmark
        testFailException("unmark 1", AlreadyUnmarkedException.class);
        testPass("mark 1");
        testFailException("mark 0", IllegalAccessCommandException.class);
        testPass("undo");
        testPass("redo");
        testPass("show done");
        testPass("unmark 1");
        testPass("undo");
        testPass("redo");
        testPass("show done");

        // Search
        testPass("search hello");
        testPass("show");
        testPass("show done");
        testPass("show complete");
        testPass("show undone");
        testPass("show incomplete");

        // filter
        testPass("filter after now");
        testPass("filter before now");
        testPass("filter from now to tmr");

        // help
        testPass("help");
        testPass("help done");
        testPass("help complete");
        testPass("help undo");
        testPass("help redo");
        testPass("help mark");
        testPass("help unmark");
        testPass("help add");
        testPass("help delete");
        testPass("help quit");
        testPass("help search");
        testPass("help filter");
        testPass("help show");
        // testPass("help show done");
        // testPass("help show undone");
        // testPass("help show today");
        testPass("help help");
        testFailException("help 1", UnknownCommandException.class);

    }

    @Test
    public void AddStressTest() {
        int TASK_COUNT = 50;
        TasksBag temp;
        Task tempTask;
        Random rng = new Random();

        int rndVal = rng.nextInt(TASK_COUNT);

        for (int i = 0; i < rndVal; i++) {
            testPass("add name " + i);
        }

        temp = logic.getTaskBag();
        Assert.assertEquals(rndVal, temp.size());

        for (int i = 0; i < rndVal; i++) {
            tempTask = temp.getTask(i);

            Assert.assertEquals(false, tempTask.isComplete());
            Assert.assertEquals(null, tempTask.getStart());
            Assert.assertEquals(null, tempTask.getEnd());
            Assert.assertEquals("name " + i, tempTask.getName());
        }
    }

    @Test
    public void AddSymbols() {
        // Valid symbols. The only invalid is ;
        String symbols = ",./<>?:'\"[]{}\\|=+=~-_!@#$%^&*()`";
        testPass("add " + symbols);
        Task addedTask = logic.getTaskBag().getTask(0);
        Assert.assertTrue(symbols.equals(addedTask.getName()));
        testPass("delete 1");
    }

    @Test
    public void AddTrailingSpaces() {
        String taskName = "takoyaki";
        testPass("  \n  \t   add " + taskName + "     ");
        Task addedTask = logic.getTaskBag().getTask(0);
        Assert.assertTrue(taskName.equals(addedTask.getName()));
        testPass("delete 1");
    }

    @Test
    public void AddNeg() {

        // Nameless tasks and symbols variants
        testFailException("add", UnknownCommandException.class);
        testFailException("add ", UnknownCommandException.class);
        testFailException("add\n", UnknownCommandException.class);
        testFailException("add\t", UnknownCommandException.class);
        testFailException("add!?", UnknownCommandException.class);
        testFailException("a!dd task", UnknownCommandException.class);
        testFailException("add;", UnknownCommandException.class);
    }

    @Test
    public void Delete() {
        // Boundary for fail. Empty entry
        testFailException("d -1", IllegalAccessCommandException.class);
        testFailException("d 0", IllegalAccessCommandException.class);
        testFailException("d 1", IllegalAccessCommandException.class);

        testPass("add one");
        // Boundary for fail. 0 and 2 with size 1
        testFailException("d 0", IllegalAccessCommandException.class);
        testFailException("d 2", IllegalAccessCommandException.class);
    }

    @Test
    public void Mark() {
        Task t;
        // Boundary for 0 entry but trying to mark
        testFailException("mark 0", IllegalAccessCommandException.class);

        testPass("add one");
        testPass("mark 1");

        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(true, t.isComplete());

        // fail test under INCOMPLETE(default) filter of tasksbag
        testFailException("mark 1", IllegalAccessCommandException.class);

        logic.getTaskBag().setView(TasksBag.ViewType.COMPLETE_TASKS);

        testFailException("mark 1", AlreadyMarkedException.class);

        // Can access task bag due to COMPLETE filter now
        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(true, t.isComplete());
    }

    @Test
    public void Unmark() {
        // Boundary for 0 entry but trying to unmark
        Task t;
        testFailException("unmark 0", IllegalAccessCommandException.class);

        testPass("add one");
        testFailException("unmark 1", AlreadyUnmarkedException.class);

        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(false, t.isComplete());

        // Unmark twice onto same object. Should throw exception
        testFailException("unmark 1", AlreadyUnmarkedException.class);

        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(false, t.isComplete());

        // Mark and unmarking
        testPass("mark 1");
        testPass("show done");
        testPass("unmark 1");
        Assert.assertEquals(false, t.isComplete());
    }

    @Test
    public void TaskSearchKeyword() {
        Task k = new Task("LowerCase UpperCase", null, null);

        // Half match case mixcase
        Assert.assertEquals(true, k.hasKeyword("LowerCase"));
        // Full match case mixrcase
        Assert.assertEquals(true, k.hasKeyword("lowerCase uppErCase"));
        // substring case
        Assert.assertEquals(true, k.hasKeyword("er"));
        // non substring
        Assert.assertEquals(false, k.hasKeyword("ber"));
        // Empty string
        Assert.assertEquals(true, k.hasKeyword(""));
        // Null string
        Assert.assertEquals(true, k.hasKeyword(null));

    }

    @Test
    public void DateInvalidDates() {
        // Invalid due to out of normal date range
        testFailException("a task; from 2015_13_1, 10:00", UnknownCommandException.class);
        testFailException("a task; from 2015-2-28, 10:00 to 2015_2_29, 10:01", UnknownCommandException.class);
        testFailException("a task; from 2015-2-29, 10:00", UnknownCommandException.class);
        testFailException("a task; from 2015-2-00, 10:00", UnknownCommandException.class);
        testFailException("a task; from 2015_1_32, 10:00", UnknownCommandException.class);
        testFailException("a task; from 2015_00_1, 10:00", UnknownCommandException.class);
        testFailException("a task; from -15_00_1, 10:00", UnknownCommandException.class);

        // @yijin Currently failing
        testFailException("a task; from -15_00_1, 10:00", UnknownCommandException.class);
        testFailException("a task; from 2015_2_-1, 10:00", UnknownCommandException.class);
    }

    @Test
    public void DateUnderscore() {
        LocalDateTime testDate;
        LocalDateTime assertDate;

        testPass("a task; from 2015_1_3, 10:00 to 2015_1_5, 10:01");
        TasksBag bag = logic.getTaskBag();
        Task task = bag.getTask(0);

        assertDate = LocalDateTime.of(2015, 1, 3, 10, 0);
        testDate = LocalDateTime.ofInstant(task.getStart().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));

        assertDate = LocalDateTime.of(2015, 1, 5, 10, 1);
        testDate = LocalDateTime.ofInstant(task.getEnd().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));
    }

    @Test
    public void DateDot() {
        LocalDateTime testDate;
        LocalDateTime assertDate;

        testPass("a task; from 2015.1.2, 10:00 to 2015.1.4, 10:01");
        TasksBag bag = logic.getTaskBag();
        Task task = bag.getTask(0);

        assertDate = LocalDateTime.of(2015, 1, 2, 10, 0);
        testDate = LocalDateTime.ofInstant(task.getStart().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));

        assertDate = LocalDateTime.of(2015, 1, 4, 10, 1);
        testDate = LocalDateTime.ofInstant(task.getEnd().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));
    }

    @Test
    public void DateForwardSlash() {
        LocalDateTime testDate;
        LocalDateTime assertDate;

        testPass("a task; from 2015/1/3, 10:00 to 2015/1/5, 10:01");
        TasksBag bag = logic.getTaskBag();
        Task task = bag.getTask(0);

        assertDate = LocalDateTime.of(2015, 1, 3, 10, 0);
        testDate = LocalDateTime.ofInstant(task.getStart().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));

        assertDate = LocalDateTime.of(2015, 1, 5, 10, 1);
        testDate = LocalDateTime.ofInstant(task.getEnd().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));
    }

    @Test
    public void DateBackSlash() {
        LocalDateTime testDate;
        LocalDateTime assertDate;

        testPass("a task; from 2015\\1\\3, 10:00 to 2015\\1\\5, 10:01");
        TasksBag bag = logic.getTaskBag();
        Task task = bag.getTask(0);

        assertDate = LocalDateTime.of(2015, 1, 3, 10, 0);
        testDate = LocalDateTime.ofInstant(task.getStart().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));

        assertDate = LocalDateTime.of(2015, 1, 5, 10, 1);
        testDate = LocalDateTime.ofInstant(task.getEnd().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));
    }

    @Test
    public void DateColon() {
        LocalDateTime testDate;
        LocalDateTime assertDate;
        testPass("a task; from 2015:10:1, 10:00 to 2015:10:1, 10:01");
        TasksBag bag = logic.getTaskBag();
        Task task = bag.getTask(0);

        assertDate = LocalDateTime.of(2015, 10, 1, 10, 0);
        testDate = LocalDateTime.ofInstant(task.getStart().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));

        assertDate = LocalDateTime.of(2015, 10, 1, 10, 1);
        testDate = LocalDateTime.ofInstant(task.getEnd().toInstant(), ZoneId.systemDefault());
        System.out.println(testDate);
        System.out.println(assertDate);
        Assert.assertTrue(testDate.equals(assertDate));
    }

    /*
     * Below are helper functions to ease testing
     */
    private void testPass(String passCommand) {
        try {
            logic.executeCommand(passCommand);
        } catch (LogicException e) {
            Assert.fail("Should not thrown Exception for: " + passCommand + e.toString());
        }
    }

    private void testFailException(String failCommand, Class<? extends LogicException> whatException) {
        try {
            logic.executeCommand(failCommand);
        } catch (LogicException e) {
            Assert.assertEquals(whatException, e.getClass());
            if (e.getClass().equals(whatException)) {
                return;
            }
            Assert.fail("Should have thrown " + whatException.getName() + " for: " + failCommand);
        }
        Assert.fail("Should have thrown " + whatException.getName() + " for: " + failCommand);
    }

    private void init() {
        File tempFd = new File(JSON_LOC_TEMP);
        File fd = new File(JSON_LOC_DEFAULT);

        try {
            Files.copy(fd.toPath(), tempFd.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logic = new Logic();
        logic.init();
    }

    private void cleanUp() {
        logic.close();
        File fd = new File(JSON_LOC_DEFAULT);
        File tempFd = new File(JSON_LOC_TEMP);
        Date timeStamp = new Date();

        File finalLocFd = new File(JSON_LOC_FINAL + timeStamp.getTime() + ".json");

        try {
            Files.copy(fd.toPath(), finalLocFd.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(tempFd.toPath(), fd.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

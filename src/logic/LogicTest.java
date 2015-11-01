//@@author A0125546E
package logic;

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

    static Logic logic;

    @Before
    public void initTest() {
        init();
    }

    @Test
    public void testFullCoverage() {

        // Logic.java
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

        // Show
        testPass("show");
        testPass("show done");
        testFailException("delete 1", IllegalAccessCommandException.class);
        testPass("show");

        // Mark/Unmark
        testFailException("unmark 1", AlreadyUnmarkedException.class);
        testPass("mark 1");
        testFailException("mark 0", IllegalAccessCommandException.class);

        // Search
        testPass("search hello");
        testPass("show");

        // filter
        testPass("filter from now to tmr");
    }

    @Test
    public void testAdd() {
        TasksBag temp;
        Task tempTask;

        testPass("add name");

        temp = logic.getTaskBag();
        tempTask = temp.getTask(0);

        Assert.assertEquals(1, temp.size());
        Assert.assertEquals("name", tempTask.getName());

    }

    @Test
    public void testDelete() {
        // Boundary for fail. Empty entry
        testFailException("d 0", IntegrityCommandException.class);

        testPass("add one");
        // Boundary for fail. 0 and 2 with size 1
        testFailException("d 0", IntegrityCommandException.class);
        testFailException("d 2", IntegrityCommandException.class);
    }

    @Test
    public void testMark() {
        Task t;
        // Boundary for 0 entry but trying to mark
        testFailException("mark 0", IntegrityCommandException.class);

        testPass("add one");
        testPass("mark 1");

        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(true, t.isComplete());

        // fail test under INCOMPLETE(default) filter of tasksbag
        testFailException("mark 1", IntegrityCommandException.class);

        logic.getTaskBag().setSortState(TasksBag.FilterBy.COMPLETE_TASKS);

        testFailException("mark 1", AlreadyMarkedException.class);

        // Can access task bag due to COMPLETE filter now
        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(true, t.isComplete());
    }

    @Test
    public void testUnmark() {
        // Boundary for 0 entry but trying to unmark
        Task t;
        testFailException("unmark 0", IntegrityCommandException.class);

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
    public void testTaskSearchKeyword() {
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
            e.getClass().equals(whatException);
            return;
        }
        Assert.fail("Should have thrown " + whatException.getName() + " for: " + failCommand);
    }

    /*
     * 
     * private void testFailAlreadyMarked(String failCommand) { try {
     * logic.executeCommand(failCommand); } catch (AlreadyMarkedException e) {
     * return; } catch (LogicException e) { Assert.fail(
     * "Should have thrown Already Marked Exception for: " + failCommand); }
     * Assert.fail("Should have thrown Already Marked Exception for: " +
     * failCommand); }
     * 
     * private void testFailAlreadyUnmarked(String failCommand) { try {
     * logic.executeCommand(failCommand); } catch (AlreadyUnmarkedException e) {
     * return; } catch (LogicException e) { Assert.fail(
     * "Should have thrown Already Unmarked Exception for: " + failCommand); }
     * Assert.fail("Should have thrown Already Unmarked Exception for: " +
     * failCommand); }
     * 
     * // Will return if it actually fails the integrity test private void
     * testFailIntegrity(String failCommand) { try {
     * logic.executeCommand(failCommand); } catch (IntegrityCommandException e)
     * { return; } catch (LogicException e) { Assert.fail(
     * "Should have thrown Integrity Exception for: " + failCommand); }
     * Assert.fail("Should have thrown Integrity Exception for: " +
     * failCommand); }
     */

    private void init() {
        logic = new Logic();
        logic.init();

        logic.setStorage(new StorageStub());
        // logic.setParser(new ParserStub());
    }

}

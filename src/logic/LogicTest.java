package logic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;

public class LogicTest {

    static Logic logic;

    @Before
    public void initTest() {
        init();
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
        testFailIntegrity("d 0");

        testPass("add one");
        // Boundary for fail. 0 and 2 with size 1
        testFailIntegrity("d 0");
        testFailIntegrity("d 2");
    }

    @Test
    public void testMark() {
        Task t;
        // Boundary for 0 entry but trying to mark
        testFailIntegrity("mark 0");

        testPass("add one");
        testPass("mark 1");

        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(true, t.isComplete());

        // fail test under INCOMPLETE(default) filter of tasksbag
        testFailIntegrity("mark 1");
        
        logic.getTaskBag().setSortState(TasksBag.FliterBy.COMPLETE_TASKS);
        
        testFailAlreadyMarked("mark 1");
        
        // Can access task bag due to COMPLETE filter now
        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(true, t.isComplete());
    }

    @Test
    public void testUnmark() {
        // Boundary for 0 entry but trying to unmark
        Task t;
        testFailIntegrity("unmark 1");

        testPass("add one");
        testPass("unmark 1");

        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(false, t.isComplete());

        // Unmark twice onto same object. Should remain as unmarked
        testPass("unmark 1");
        t = logic.getTaskBag().getTask(0);
        Assert.assertEquals(false, t.isComplete());
    }

    private void testPass(String passCommand) {
        try {
            logic.executeCommand(passCommand);
        } catch (LogicException e) {
            Assert.fail("Should not thrown Exception for: " + passCommand + e.toString());
        }
    }
    private void testFailAlreadyMarked(String failCommand){
        try {
            logic.executeCommand(failCommand);
        } catch (AlreadyMarkedException e) {
            return;
        } catch (LogicException e) {
            Assert.fail("Should have thrown Already Marked Exception for: " + failCommand);
        }
        Assert.fail("Should have thrown Already Marked Exception for: " + failCommand);
    }
    
    // Will return if it actually fails the integrity test
    private void testFailIntegrity(String failCommand) {
        try {
            logic.executeCommand(failCommand);
        } catch (IntegrityCommandException e) {
            return;
        } catch (LogicException e) {
            Assert.fail("Should have thrown Integrity Exception for: " + failCommand);
        }
        Assert.fail("Should have thrown Integrity Exception for: " + failCommand);
    }

    private void init() {
        logic = new Logic();
        logic.init();

        logic.setStorage(new StorageStub());
        // logic.setParser(new ParserStub());
    }

}

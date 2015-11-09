//@@author A0133895U
/**
 * This is the GUI test for Celebi.
 * Please first add testFx-3.1.2.jar , testfx-core-4.0.1-alpha.jar , guava-18.0.jar 
 * that are under lib into path before you run the test.
 */
package ui;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.fxmisc.richtext.InlineCssTextArea;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import common.TasksBag.FilterDateState;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ui.view.CelebiViewController;
import ui.view.CelebiViewController.Skin;

public class GUITest extends FxRobot {
	private static Main mainApp;
	private static CelebiViewController controller;
	public static Stage primaryStage;
	
	//Setting up
	@BeforeClass
	public static void setUpClass() throws Exception {
		primaryStage = FxToolkit.registerPrimaryStage();
	}
	
	@AfterClass
	public static void tearDownClass() {
		
	}	
	
	@Before
	public void before() {
		try {
		mainApp = (Main)FxToolkit.setupApplication(Main.class);
		controller = mainApp.getController();
		WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, primaryStage.showingProperty());
		sleep(2000);
		} 
		catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
		
	@After
    public void after() throws TimeoutException {
        FxToolkit.cleanupStages();
        FxToolkit.hideStage();
    }
	
	
	@Test
	public void keyEnterTest() {
		InlineCssTextArea commandArea=(InlineCssTextArea)mainApp.getScene().lookup("#command-area");
   		clickOn("#command-area").write("add").push(KeyCode.ENTER);
   		assertTrue(commandArea.getText().equals("")); 		
	}
	
	@Test
	public void keyTypeTabTest() {
		switchToDefaultView();
		
		TabPane tabPane=(TabPane)mainApp.getScene().lookup("#tab-pane");
		push(KeyCode.TAB);
		assertTrue(tabPane.getSelectionModel().getSelectedIndex() == 2);
		push(KeyCode.TAB);
		assertTrue(tabPane.getSelectionModel().getSelectedIndex() == 1);
		push(KeyCode.TAB);
		assertTrue(tabPane.getSelectionModel().getSelectedIndex() == 0);
	}
	
	@Test
	public void changeSkinTest() {
		clickOn("#command-area");
		
		// test change night skin
		write("skin night").push(KeyCode.ENTER);
		assertTrue(controller.getSkin() == Skin.NIGHT);
		
		// test change day skin
		write("skin day").push(KeyCode.ENTER);
		assertTrue(controller.getSkin() == Skin.DAY);
	}
	
	@Test
	public void commandScrollBarIsHiddenTest() {
		clickOn("#command-area");
		
		// test whether command area scroll bar is hidden when command is
		// too long
		write("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
		ScrollBar commandScrollBar = (ScrollBar)mainApp.getScene().lookup("#command-scroll-bar"); 
		assertTrue(!commandScrollBar.isDisable());
		push(KeyCode.ENTER);
	}
	
	@Test
	public void addTasksSuccessfulTest() {
		int TASK_COUNT = 5;
		Random rng = new Random();
		int rndVal = rng.nextInt(TASK_COUNT) + 5;
		
		switchToDefaultView();
		// switch to complete view
		push(KeyCode.TAB);
		push(KeyCode.TAB);
		
		TableView tableView = (TableView)mainApp.getScene().lookup("#celebi-table-view") ;
		int beforeNumberOfTasks = tableView.getItems().size();

		// check whether feedback for successful adding is there
        for (int i = 0; i < rndVal; i++) {
        	assertTrue(isTaskAdded("task"+i));
        }
        
        // check whether the number of items added to the table is correct
        int afterNumberOfTasks = tableView.getItems().size();
        assertTrue(afterNumberOfTasks - beforeNumberOfTasks == rndVal);
        push(KeyCode.TAB);
	}

	@Test
	public void addLengthyTaskTest() {
		String lengthyTask1 = generateRandomString(49);
		String lengthyTask2 = generateRandomString(50);
		String lengthyTask3 = generateRandomString(51);
		
		// check whether string that is longer than 50 characters is rejected
		assertTrue(!isTaskRejectedDueToLengthyName(lengthyTask1));
		assertTrue(!isTaskRejectedDueToLengthyName(lengthyTask2));
		assertTrue(isTaskRejectedDueToLengthyName(lengthyTask3));
	}
	
	@Test
	public void deleteTest() {
		String DELETE_INDEX_EXCEED_MSG = "You: delete %1$d\nCelebi: Provided index not on list.";
		int afterNumberOfTasks, beforeNumberOfTasks;
		
		switchToDefaultView();
		push(KeyCode.TAB);
		push(KeyCode.TAB);
		clickOn("#command-area");
		write("add task1").push(KeyCode.ENTER);
		
		InlineCssTextArea feedbackArea=(InlineCssTextArea)mainApp.getScene().lookup("#feedback-area");
		TableView tableView = (TableView)mainApp.getScene().lookup("#celebi-table-view") ;
		beforeNumberOfTasks = tableView.getItems().size();
		
		write("delete 1").push(KeyCode.ENTER);
		afterNumberOfTasks  = tableView.getItems().size();
		assert(beforeNumberOfTasks - 1 == afterNumberOfTasks);
		
		write("delete " + (afterNumberOfTasks+1)).push(KeyCode.ENTER);
		Assert.assertEquals(feedbackArea.getText(), String.format(DELETE_INDEX_EXCEED_MSG, afterNumberOfTasks+1));
		beforeNumberOfTasks = afterNumberOfTasks;
		afterNumberOfTasks = tableView.getItems().size();
		Assert.assertEquals(beforeNumberOfTasks, afterNumberOfTasks);
		
		write("delete -1").push(KeyCode.ENTER);
		Assert.assertEquals(feedbackArea.getText(), String.format(DELETE_INDEX_EXCEED_MSG, -1));
		beforeNumberOfTasks = afterNumberOfTasks;
		afterNumberOfTasks = tableView.getItems().size();
		Assert.assertEquals(beforeNumberOfTasks, afterNumberOfTasks);
	}
	
	@Test
	public void filterTest() {
		switchToDefaultView();
		
		String FILTER_MSG = "Now filtering: %1$s. ";
        Label filterLabel = (Label)mainApp.getScene().lookup("#filter-label");
        String formattedDate;
		
		push(KeyCode.TAB);
		push(KeyCode.TAB);
		clickOn("#command-area");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		// Test default
		Assert.assertEquals(" ", filterLabel.getText());
		
		// Test filtering after
		write("filter after 2015/12/11").push(KeyCode.ENTER);
		c1.set(2015, 12-1, 11, 8, 0);
		formattedDate = controller.getDateFilterString(FilterDateState.AFTER, c1.getTime(), null);
		Assert.assertEquals(String.format(FILTER_MSG, formattedDate) + " ", filterLabel.getText());
		
		// Test filtering before
		write("filter before 2016/3/2").push(KeyCode.ENTER);
		c2.set(2016, 3-1, 2, 23, 59);
		formattedDate = controller.getDateFilterString(FilterDateState.BEFORE, null, c2.getTime());
		Assert.assertEquals(String.format(FILTER_MSG, formattedDate) + " ", filterLabel.getText());
		
		// Test filtering between
		write("filter from 2018/2/18 to 2019/10/12").push(KeyCode.ENTER);
		c1.set(2018, 2-1, 18, 8, 0);
		c2.set(2019, 10-1, 12, 23, 59);
		formattedDate = controller.getDateFilterString(FilterDateState.BETWEEN, c1.getTime(), c2.getTime());
		Assert.assertEquals(String.format(FILTER_MSG, formattedDate) + " ", filterLabel.getText());
		
		// Test clearing filter
		write("clear").push(KeyCode.ENTER);
		Assert.assertEquals(" ", filterLabel.getText());
	}
	
	@Test
	public void searchTest() {
		switchToDefaultView();
		
		String SEARCH_MSG = "Now searching: %1$s.";
        Label filterLabel = (Label)mainApp.getScene().lookup("#filter-label");
        
		clickOn("#command-area");
        
        // Test default
	Assert.assertEquals(" ", filterLabel.getText());
     	
     	// Test searching
     	write("search task").push(KeyCode.ENTER);
     	Assert.assertEquals(" " + String.format(SEARCH_MSG, "task"), filterLabel.getText());
     	
     	// Test clearing filter
     	write("clear").push(KeyCode.ENTER);
     	Assert.assertEquals(" ", filterLabel.getText());
	}
	
	@Test
	public void doneTest() {
		switchToDefaultView();
		
		int beforeCompleteTasks, afterCompleteTasks;
		int beforeIncompleteTasks, afterIncompleteTasks;
		TableView tableView = (TableView)mainApp.getScene().lookup("#celebi-table-view") ;
		
		// switch to complete view
		push(KeyCode.TAB).push(KeyCode.ENTER);
		clickOn("#command-area");
		write("add test done");
		
		beforeCompleteTasks = tableView.getItems().size();
		
		// switch to incomplete view
		push(KeyCode.TAB);
		beforeIncompleteTasks = tableView.getItems().size();
		
		write("done 1").push(KeyCode.ENTER);
		
		// switch to complete view
		push(KeyCode.TAB);
		push(KeyCode.TAB);
		afterCompleteTasks = tableView.getItems().size();
		
		// switch to incomplete view
		push(KeyCode.TAB);
		afterIncompleteTasks = tableView.getItems().size();
		
		// check if the task inside incomplete view is moved to complete
		// view successfully
		Assert.assertEquals(beforeCompleteTasks + 1, afterCompleteTasks);
		Assert.assertEquals(beforeIncompleteTasks - 1, afterIncompleteTasks);
	}
				

	/**
	 * to generate a random string of given length
	 * @param length
	 * @return
	 */
	private String generateRandomString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()[]{}".toCharArray();
		StringBuilder builder = new StringBuilder();
		Random rdm = new Random();
		for (int i=0; i<length; i++) {
			char c = chars[rdm.nextInt(chars.length)];
			builder.append(c);
		}
		return builder.toString();
	}
	
	/**
	 * to check whether a task is successfully added
	 * @param taskName
	 * @return
	 */
	private boolean isTaskAdded(String taskName) {
		String SUCCESSFUL_ADD_MSG = "You: add %1$s\nCelebi: Added %1$s!";
		InlineCssTextArea feedbackArea=(InlineCssTextArea)mainApp.getScene().lookup("#feedback-area");
		clickOn("#command-area");
		
		write("add " + taskName).push(KeyCode.ENTER);
    	boolean isAdded = feedbackArea.getText().equals(String.format(SUCCESSFUL_ADD_MSG, taskName));
    	return isAdded;
	}
	
	/**
	 * to check whether a task is rejected because its length exceed the
	 * maximum length allowed
	 * @param taskName
	 * @return
	 */
	private boolean isTaskRejectedDueToLengthyName(String taskName) {
		String LENGTHY_FAILURE_ADD_MSG = "You: add %1$s\nCelebi: Failed to add! Your task name is too long! Keep it to less than 50 characters.";
		InlineCssTextArea feedbackArea=(InlineCssTextArea)mainApp.getScene().lookup("#feedback-area");
		clickOn("#command-area");
		
		write("add " + taskName).push(KeyCode.ENTER);
		boolean isRejected = feedbackArea.getText().equals(String.format(LENGTHY_FAILURE_ADD_MSG, taskName));
		return isRejected;
	}
	
	/**
	 * to switch the view to default view
	 */
	private void switchToDefaultView() {
		TabPane tabPane=(TabPane)mainApp.getScene().lookup("#tab-pane");
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(0);
	}
}

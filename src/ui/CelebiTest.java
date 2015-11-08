package ui;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.fxmisc.richtext.InlineCssTextArea;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.loadui.testfx;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ui.view.CelebiViewController;

public class CelebiTest extends FxRobot {
	private static Main mainApp;
	private static CelebiViewController controller;
	public static Stage primaryStage;
	
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
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
		
	@After
    public void after() throws TimeoutException {
        FxToolkit.cleanupStages();
        FxToolkit.hideStage();
    }
	
	@Test
	public void setTextAreaText() {
		InlineCssTextArea commandArea=(InlineCssTextArea)mainApp.getScene().lookup("#command-area");
   		clickOn("#command-area").write("add").push(KeyCode.ENTER);
   		assertTrue(commandArea.getText().equals("")); 		
	}
	
	@Test
	public void keyTypeTabTest() {
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
		InlineCssTextArea commandArea=(InlineCssTextArea)mainApp.getScene().lookup("#command-area");
		clickOn("#command-area").write("skin night").push(KeyCode.ENTER);
		//assertTrue(controller.);
	}
}

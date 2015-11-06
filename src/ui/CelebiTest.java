//package ui;
//
//import java.util.concurrent.TimeUnit;
//
//import org.fxmisc.richtext.InlineCssTextArea;
//import org.junit.Before;
//import org.junit.Test;
//import org.testfx.api.FxRobot;
//import org.testfx.api.FxToolkit;
//import org.testfx.util.WaitForAsyncUtils;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.input.KeyCode;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//
//public class CelebiTest extends FxRobot {
//	private Main mainApp;
//	public static Stage primaryStage;
//	
//	@Before
//	public void before() throws Exception {
//		primaryStage = FxToolkit.registerPrimaryStage();
//		mainApp = (Main)FxToolkit.setupApplication(Main.class);
//		WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, primaryStage.showingProperty());
//		Thread.sleep(3000);
//	}
//	
//	@Test
//	public void setTextAreaText() {
//        try {
//        	InlineCssTextArea commandArea=(InlineCssTextArea)mainApp.getScene().lookup("#command-area");
//    		//System.out.println(commandArea == null);
//    		clickOn("#command-area").write("add").push(KeyCode.ENTER);
//    		assert(commandArea.getText().equals(""));
//        } catch (Exception e) {
//        	System.out.println("cnm");
//        }
//        
//		
//	}
//}

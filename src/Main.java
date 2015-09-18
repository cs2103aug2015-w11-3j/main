import ui.UI;
import ui.UIInterface;

public class Main {
	public static void main(String[] args) {
		UIInterface mUI = new UI();

		mUI.init();
		mUI.run();

		System.out.println("CELEBI ENDED");
	}
}

/*
 * Snippet for GUI
 * 
 * import javafx.application.Application; import javafx.event.ActionEvent;
 * import javafx.event.EventHandler; import javafx.scene.Scene; import
 * javafx.scene.control.Button; import javafx.scene.layout.StackPane; import
 * javafx.stage.Stage;
 * 
 * public class Main extends Application { public static void main(String[]
 * args) { launch(args); }
 * 
 * @Override public void start(Stage primaryStage) { primaryStage.setTitle(
 * "Hello World!"); Button btn = new Button(); btn.setText("Say 'Hello World'");
 * 
 * // Very wow, So new, // Java 8's new feature lambda functions HHAHAHah
 * 
 * btn.setOnAction(new EventHandler<ActionEvent>() {
 * 
 * @Override public void handle(ActionEvent event) { System.out.println(
 * "Hello World!"); } });
 * 
 * StackPane root = new StackPane(); root.getChildren().add(btn);
 * primaryStage.setScene(new Scene(root, 300, 250)); primaryStage.show(); } }
 */
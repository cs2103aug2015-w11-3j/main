package ui;
import java.io.IOException;

import common.Configuration;
import common.ConfigurationInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.view.CelebiViewController;

public class Main extends Application {	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane celebiPane;
    private Scene scene;
    private CelebiViewController controller;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Our Brilliant Celebi >o<!!");
        try {
        	this.primaryStage.initStyle(StageStyle.TRANSPARENT);
        } catch (Exception e) {
        	
        }
        
        Font regularFont = Font.loadFont(Main.class.getResourceAsStream("resource/Oxygen regular.ttf"), 10);
        Font boldFont = Font.loadFont(Main.class.getResourceAsStream("resource/Oxygen 700.ttf"), 10);
        initRootLayout();
        
        showCelebiView();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(UI.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            scene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            this.scene = scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showCelebiView() {
    	try {    		
    		// Initialize UI
            UIInterface mUI = new UI();
    		mUI.init();
    		
    		// Load celebi view.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(UI.class.getResource("view/CelebiView.fxml"));
            AnchorPane celebiView = (AnchorPane) loader.load();

            // Set celebi view into the center of root layout.
            rootLayout.setCenter(celebiView);
            
            // Give the controller access to the main app and UI.
            controller = loader.getController();
            ConfigurationInterface config = Configuration.getInstance();
            CelebiViewController.Skin skin;
            
            try {
            	skin = Enum.valueOf(CelebiViewController.Skin.class, config.getSkin());
            } catch (IllegalArgumentException e) {
            	skin = CelebiViewController.Skin.DAY;
            }
            
            switch (skin) {
    	        case NIGHT:
    	            controller.switchNightSkin();
    	            break;
    	
    	        case DAY:
    	        default: 
    	        	controller.switchDaySkin();
    	    }
            
            controller.setUI(mUI);
            controller.setMainApp(this);
            
            // Give the UI acess to the controller.
            mUI.setController(controller);
            mUI.showWelcomeView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /*
    class WindowsButtons extends HBox {
    	public WindowsButtons() {
    		Button close = new Button("X");
    		close.setOnAction(new EventHandler<ActionEvent>() {
    			@Override
    			public void handle(ActionEvent event) {
    				Platform.exit();
    			}
    		});
    	}
    }
    */
	
	
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
    	launch(args);
    }
    
    public BorderPane getRootLayout() {
    	return rootLayout;
    }
    
    public AnchorPane getCelebiLayout() {
    	return celebiPane;
    }
    
    public Scene getScene() {
    	return scene;
    }
    
    public CelebiViewController getController() {
    	return controller;
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
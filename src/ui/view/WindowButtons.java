// @@author A0133895U
package ui.view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

class WindowButtons extends HBox {
	private Stage stage;
	
	public WindowButtons(Stage stage) {
		this.stage = stage;
		
		// add in close button
		Button close = new Button("X");
		close.setId("close-button");
		setClickActionExit(close);
		setMouseOverCursor(close);
		
		// add in minimize button
		Button minimize = new Button("\u2013");
		setClickActionMinimize(minimize);
		setMouseOverCursor(minimize);
		
		this.setSpacing(5);
		this.getChildren().add(minimize);
		this.getChildren().add(close);
	}
	
	private void setClickActionMinimize(Button minimize) {
		minimize.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				stage.setIconified(true);
			}
		});
	}

	private void setClickActionExit(Button close) {
		close.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Platform.exit();
			}
		});
	}

	/**
	 * When mousing over the buttons, the cursor changes to hand
	 * @param button
	 */
	private void setMouseOverCursor(Button button) {
		button.setOnMouseEntered(new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent mouseEvent) {
    			button.setCursor(Cursor.HAND);
    		}
    	});
	}
}

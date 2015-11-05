package ui;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import common.Task;
import ui.view.CelebiViewController;

public interface UIInterface {
	public void init();

	public void passCommand(String userInput);
	public void passKeyEvent(KeyCode whichKey);
	public ObservableList<Task> getCelebiList();
	public void setController(CelebiViewController controller);
	public void showWelcomeView();
	// main loop.
	// Should be switching to event callbacks when
	// JavaFX is used
	// public void run();
}

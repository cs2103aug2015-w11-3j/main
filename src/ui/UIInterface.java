package ui;

import javafx.collections.ObservableList;
import common.Celebi;
import ui.view.CelebiViewController;

public interface UIInterface {
	public void init();

	public void passCommand(String userInput);
	public ObservableList<Celebi> getCelebiList();
	public void setController(CelebiViewController controller);
	public void showWelcomeView();
	// main loop.
	// Should be switching to event callbacks when
	// JavaFX is used
	// public void run();
}

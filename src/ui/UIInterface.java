package ui;

public interface UIInterface {
	public void init();

	// main loop.
	// Should be switching to event callbacks when
	// JavaFX is used
	public void run();
}

//@@author A0125546E
package ui.view;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class LabelFader {

    private enum State {
        ON, OFF
    }

    private Label cLabel;
    private State cState;
    private FadeTransition fadeIn;

    public LabelFader(Label label) {
        cLabel = label;
        label.setVisible(false);
        cState = State.OFF;

        fadeIn = new FadeTransition(Duration.millis(500));
        initFadeAnimations();
    }

    public void fadeIn() {
        if (cState == State.OFF) {
            cLabel.setVisible(true);
        	fadeIn.setRate(1);
            fadeIn.play();
            cState = State.ON;
        }
    }

    public void fadeOut() {
        if (cState == State.ON) {
            fadeIn.setRate(-1);
            fadeIn.play();
            cState = State.OFF;
        }
    }

    private void initFadeAnimations() {
        fadeIn.setNode(cLabel);

        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(0);
        fadeIn.setAutoReverse(false);
    }
}

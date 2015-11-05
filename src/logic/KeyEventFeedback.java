package logic;

import common.TasksBag;
import javafx.scene.input.KeyCode;

public class KeyEventFeedback extends Feedback {

    private KeyCode cKey;

    public KeyEventFeedback(TasksBag bag, KeyCode key) {
        super(bag);
        cKey = key;
    }

    public KeyCode getKey() {
        return cKey;
    }

}

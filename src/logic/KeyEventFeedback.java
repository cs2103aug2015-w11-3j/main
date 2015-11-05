package logic;

import common.TasksBag;
import javafx.scene.input.KeyCode;

public class KeyEventFeedback extends Feedback {

    private KeyCode cKey;
    private String cMsg;
    
    public KeyEventFeedback(TasksBag bag, KeyCode key) {
        super(bag);
        cKey = key;
    }

    public KeyEventFeedback(TasksBag bag, KeyCode key, String msg) {
        this(bag,key);
        cMsg = msg;
    }
    
    public KeyCode getKey() {
        return cKey;
    }

    public String getMsg(){
        return cMsg;
    }
}

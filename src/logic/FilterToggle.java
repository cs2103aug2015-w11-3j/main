package logic;

import common.TasksBag;
import javafx.scene.input.KeyCode;
import logic.exceptions.LogicException;

public class FilterToggle implements Action {

    private TasksBag cBag;
    private KeyCode cKey;
    
    public FilterToggle(TasksBag bag, KeyCode key) {
        cBag = bag;
        cKey = key;
    }

    @Override
    public KeyEventFeedback execute() throws LogicException {
        KeyEventFeedback fb = new KeyEventFeedback(cBag, cKey);
        //cBag.toggleFilter();
        cBag.toggleFilter();
        return fb;
    }

}

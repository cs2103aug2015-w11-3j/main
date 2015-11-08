package logic;

import common.TasksBag;
import common.TasksBag.ViewType;
import javafx.scene.input.KeyCode;
import logic.exceptions.LogicException;
//@@author A0125546E
public class FilterToggleAction implements Action {
    private static final String USR_MSG_FILTER_COMPLETE = "Switching view to completed tasks";
    private static final String USR_MSG_FILTER_INCOMPLETE = "Switching view to incompleted tasks";
    private static final String USR_MSG_FILTER_TODAY = "Switching view to today tasks";

    private TasksBag cBag;
    private KeyCode cKey;

    public FilterToggleAction(TasksBag bag, KeyCode key) {
        cBag = bag;
        cKey = key;
    }

    @Override
    public KeyEventFeedback execute() throws LogicException {
        KeyEventFeedback fb;
        ViewType bagState = cBag.getView();
        String msg = "";

        // cBag.toggleFilter();
        cBag.toggleView();

        switch (bagState) {
            case COMPLETED:
                msg = USR_MSG_FILTER_COMPLETE;
                break;

            case INCOMPLETE:
                msg = USR_MSG_FILTER_INCOMPLETE;
                break;

            case DEFAULT:
                msg = USR_MSG_FILTER_TODAY;
                break;
        }
        
        fb = new KeyEventFeedback(cBag, cKey, msg);
        return fb;
    }

}

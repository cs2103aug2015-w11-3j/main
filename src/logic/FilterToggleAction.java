package logic;

import common.TasksBag;
import common.TasksBag.FilterBy;
import javafx.scene.input.KeyCode;
import logic.exceptions.LogicException;

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
        FilterBy bagState = cBag.getState();
        String msg = "";

        // cBag.toggleFilter();
        cBag.toggleFilter();

        switch (bagState) {
            case COMPLETE_TASKS:
                msg = USR_MSG_FILTER_COMPLETE;
                break;

            case INCOMPLETE_TASKS:
                msg = USR_MSG_FILTER_INCOMPLETE;
                break;

            case TODAY:
                msg = USR_MSG_FILTER_TODAY;
                break;
        }
        cBag.setSearchState(null);
        cBag.setFilterDateState(null, null);
        fb = new KeyEventFeedback(cBag, cKey, msg);
        return fb;
    }

}

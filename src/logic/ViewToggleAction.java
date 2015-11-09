package logic;

import common.TasksBag;
import common.TasksBag.ViewType;
import javafx.scene.input.KeyCode;
import logic.exceptions.LogicException;

//@@author A0125546E
public class ViewToggleAction implements Action {
    private static final String USR_MSG_VIEW_COMPLETE = "Switching view to completed tasks";
    private static final String USR_MSG_VIEW_INCOMPLETE = "Switching view to incomplete tasks";
    private static final String USR_MSG_VIEW_TODAY = "Switching view to today tasks";

    private TasksBag cBag;
    private KeyCode cKey;

    public ViewToggleAction(TasksBag bag, KeyCode key) {
        cBag = bag;
        cKey = key;
    }

    @Override
    public KeyEventFeedback execute() throws LogicException {
        KeyEventFeedback fb;
        ViewType viewState;
        String msg = "";

        cBag.toggleView();
        viewState = cBag.getView();
        
        switch (viewState) {
            case COMPLETED:
                msg = USR_MSG_VIEW_COMPLETE;
                break;

            case INCOMPLETE:
                msg = USR_MSG_VIEW_INCOMPLETE;
                break;

            case DEFAULT:
                msg = USR_MSG_VIEW_TODAY;
                break;
        }

        fb = new KeyEventFeedback(cBag, cKey, msg);
        return fb;
    }

}

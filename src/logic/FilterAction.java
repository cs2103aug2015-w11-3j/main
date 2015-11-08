//@@author A0125546E
package logic;

import common.TasksBag;
import common.TasksBag.ViewType;
import parser.Command;

/**
 * Changes the state of the internal taskbags to given sort state Undo sets the
 * internal bag state to previous state
 */
public class FilterAction implements Action {
    private static final String USR_MSG_FILTER_COMPLETE = "Switching view to completed tasks";
    private static final String USR_MSG_FILTER_INCOMPLETE = "Switching view to incompleted tasks";
    private static final String USR_MSG_FILTER_TODAY = "Switching view to today tasks";

    private Command cCommand;
    private TasksBag cBag;
    private ViewType cSortBy;

    public FilterAction(Command command, TasksBag internalBag) {
        cCommand = command;
        cBag = internalBag;
        cSortBy = cCommand.getViewType();
    }

    @Override
    public CommandFeedback execute() {
        String msg = "";
        switch (cSortBy) {
            case COMPLETED:
                msg = USR_MSG_FILTER_COMPLETE;
                cBag.setView(ViewType.COMPLETED);
                break;
            case INCOMPLETE:
                msg = USR_MSG_FILTER_INCOMPLETE;
                cBag.setView(ViewType.INCOMPLETE);
                break;
            case DEFAULT:
                msg = USR_MSG_FILTER_TODAY;
                cBag.setView(ViewType.DEFAULT);
                break;
            default:
                assert false;
                break;

        }
        return new CommandFeedback(cCommand, cBag, msg);
    }

}

//@@author A0125546E
package logic;

import common.TasksBag;
import common.TasksBag.ViewType;
import logic.exceptions.LogicException;
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

    
    public FilterAction(Command command, TasksBag internalBag, ViewType sortBy) throws LogicException {
        cCommand = command;
        cBag = internalBag;
        cSortBy = sortBy;
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String msg = "";
        switch (cSortBy) {
            case COMPLETE_TASKS:
                msg = USR_MSG_FILTER_COMPLETE;
                cBag.setView(ViewType.COMPLETE_TASKS);
                break;
            case INCOMPLETE_TASKS:
                msg = USR_MSG_FILTER_INCOMPLETE;
                cBag.setView(ViewType.INCOMPLETE_TASKS);
                break;
            case TODAY:
                msg = USR_MSG_FILTER_TODAY;
                cBag.setView(ViewType.TODAY);
                break;
            default:
                assert false;
                break;

        }

        // both search string and filter date will be reset
        cBag.setSearchState(null);
        cBag.setFilterDateState(null, null);
        return new CommandFeedback(cCommand, cBag, msg);
    }

}

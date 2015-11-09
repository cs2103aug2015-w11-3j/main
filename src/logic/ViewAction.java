//@@author A0125546E
package logic;

import common.TasksBag;
import common.TasksBag.ViewType;
import parser.commands.CommandData;

/**
 * Changes the state of the internal taskbags to given sort state Undo sets the
 * internal bag state to previous state
 */
public class ViewAction implements Action {
    private static final String USR_MSG_VIEW_COMPLETE = "Switching view to completed tasks";
    private static final String USR_MSG_VIEW_INCOMPLETE = "Switching view to incomplete tasks";
    private static final String USR_MSG_VIEW_TODAY = "Switching view to today tasks";

    private CommandData cCommand;
    private TasksBag cBag;
    private ViewType cViewBy;

    public ViewAction(CommandData command, TasksBag internalBag) {
        cCommand = command;
        cBag = internalBag;
        cViewBy = cCommand.getViewType();
    }

    @Override
    public CommandFeedback execute() {
        String msg = "";
        switch (cViewBy) {
            case COMPLETED:
                msg = USR_MSG_VIEW_COMPLETE;
                cBag.setView(ViewType.COMPLETED);
                break;
            case INCOMPLETE:
                msg = USR_MSG_VIEW_INCOMPLETE;
                cBag.setView(ViewType.INCOMPLETE);
                break;
            case DEFAULT:
                msg = USR_MSG_VIEW_TODAY;
                cBag.setView(ViewType.DEFAULT);
                break;
            default:
                assert false;
                break;

        }
        return new CommandFeedback(cCommand, cBag, msg);
    }

}

//@@author A0125546E
package logic;

import common.TasksBag;
import parser.commands.CommandData;

public class FilterClearAction implements Action {
    
    private static final String USR_MSG_FILTER_CLEAR = "Cleared searches and filters!";

    private CommandData cCommand;
    private TasksBag cBag;

    public FilterClearAction(CommandData rtnCmd, TasksBag cInternalBag) {
        cCommand = rtnCmd;
        cBag = cInternalBag;
    }

    @Override
    public Feedback execute() {
        String msg = USR_MSG_FILTER_CLEAR;
        
        cBag.setSearchState(null);
        cBag.setFilterDateState(null, null);
        CommandFeedback fb = new CommandFeedback(cCommand, cBag, msg);
        return fb;
    }

}

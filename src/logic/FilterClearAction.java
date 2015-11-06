package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;

public class FilterClearAction implements Action {
    
    private static final String USR_MSG_FILTER_CLEAR = "Cleared searches and filters!";

    private Command cCommand;
    private TasksBag cBag;

    public FilterClearAction(Command rtnCmd, TasksBag cInternalBag) {
        cCommand = rtnCmd;
        cBag = cInternalBag;
    }

    @Override
    public Feedback execute() throws LogicException {
        String msg = USR_MSG_FILTER_CLEAR;
        
        cBag.setSearchState(null);
        cBag.setFilterDateState(null, null);
        CommandFeedback fb = new CommandFeedback(cCommand, cBag, msg);
        return fb;
    }

}

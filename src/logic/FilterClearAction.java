package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;

public class FilterClearAction implements Action {
    
    private Command cCommand;
    private TasksBag cBag;

    public FilterClearAction(Command rtnCmd, TasksBag cInternalBag) {
        cCommand = rtnCmd;
        cBag = cInternalBag;
    }

    @Override
    public Feedback execute() throws LogicException {
        cBag.setSearchState(null);
        cBag.setFilterDateState(null, null);
        CommandFeedback fb = new CommandFeedback(cCommand, cBag);
        return fb;
    }

}

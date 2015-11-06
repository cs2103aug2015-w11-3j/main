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
        CommandFeedback fb = new CommandFeedback(cCommand, cBag);
        return fb;
    }

}

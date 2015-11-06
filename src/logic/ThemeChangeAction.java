package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;

public class ThemeChangeAction implements Action {      

    private Command cCommand;
    private TasksBag cBag;

    public ThemeChangeAction(Command comd, TasksBag bag) {
        cCommand = comd;
        cBag = bag;
    }

    @Override
    public Feedback execute() throws LogicException {
        CommandFeedback fb = new CommandFeedback(cCommand, cBag);
        return fb;
    }

}

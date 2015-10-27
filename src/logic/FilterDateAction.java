package logic;

import java.util.Date;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;

public class FilterDateAction implements Action {

    private Command cCommand;
    private TasksBag cBag;
    private Date cStart;
    private Date cEnd;

    public FilterDateAction(Command command, TasksBag internalBag) throws LogicException {
        cCommand = command;
        cBag = internalBag;
        cStart = cCommand.getStart();
        cEnd = cCommand.getEnd();
    }

    @Override
    public Feedback execute() throws LogicException {
        Feedback fb;

        cBag.setFilterDateState(cStart, cEnd);

        fb = new Feedback(cCommand, cBag, "Filtering date after " + cStart);
        return fb;
    }

}

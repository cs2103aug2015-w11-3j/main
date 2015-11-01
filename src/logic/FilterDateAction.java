package logic;

import java.util.Date;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.Command;

public class FilterDateAction implements Action {

    private static final String USR_MSG_FILTER_AFTER_DATE = "Filtering dates after %1$s";
    private static final String USR_MSG_FILTER_BEFORE_DATE = "Filtering dates before %1$s";
    private static final String USR_MSG_FILTER_BETWEEN_DATE = "Filtering dates between %1$s and %2$s";

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
        TasksBag.FilterDateState dState = cBag.getDateState();

        fb = new Feedback(cCommand, cBag, getFeedbackText(dState));
        return fb;
    }

    private String getFeedbackText(TasksBag.FilterDateState state) {
        String rtn = "Filtering date after " + cStart + " to " + cEnd;
        switch (state) {
            case AFTER:
                rtn = Utilities.formatString(USR_MSG_FILTER_AFTER_DATE, cStart);
                break;

            case BEFORE:
                rtn = Utilities.formatString(USR_MSG_FILTER_BEFORE_DATE, cEnd);
                break;
            case BETWEEN:
                rtn = Utilities.formatString(USR_MSG_FILTER_BETWEEN_DATE, cStart, cEnd);
                break;
            default:
                break;
        }
        return rtn;
    }

}

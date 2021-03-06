//@@author A0125546E
package logic;

import java.util.Date;

import common.TasksBag;
import common.Utilities;
import logic.exceptions.InvalidDateException;
import parser.commands.CommandData;
import ui.view.DateFormatter;

public class FilterDateAction implements Action {

    private static final String USR_MSG_FILTER_AFTER_DATE = "Filtering dates after %1$s";
    private static final String USR_MSG_FILTER_BEFORE_DATE = "Filtering dates before %1$s";
    private static final String USR_MSG_FILTER_BETWEEN_DATE = "Filtering dates between %1$s and %2$s";
    private static final String USR_MSG_FILTER_INVALID_SEARCH_DATE = "I can't search for a start date that is after end date!";
    private CommandData cCommand;
    private TasksBag cBag;
    private Date cStart;
    private Date cEnd;

    public FilterDateAction(CommandData command, TasksBag internalBag) throws InvalidDateException {
        cCommand = command;
        cBag = internalBag;
        cStart = cCommand.getStart();
        cEnd = cCommand.getEnd();
        boolean isValid = Utilities.verifyDate(cStart, cEnd);
        if (isValid == false) {
            throw new InvalidDateException(USR_MSG_FILTER_INVALID_SEARCH_DATE);
        }
    }

    @Override
    public CommandFeedback execute() {
        CommandFeedback fb;

        cBag.setFilterDateState(cStart, cEnd);
        TasksBag.FilterDateState dState = cBag.getDateState();

        fb = new CommandFeedback(cCommand, cBag, getFeedbackText(dState));
        return fb;
    }

    private String getFeedbackText(TasksBag.FilterDateState state) {
        String rtn = "";
        String formatStart;
        String formatEnd;
        DateFormatter df = new DateFormatter();
        switch (state) {
            case AFTER:
                formatStart = df.formatDate(cStart);
                rtn = Utilities.formatString(USR_MSG_FILTER_AFTER_DATE, formatStart);
                break;

            case BEFORE:
                formatEnd = df.formatDate(cEnd);
                rtn = Utilities.formatString(USR_MSG_FILTER_BEFORE_DATE, formatEnd);
                break;
            case BETWEEN:
                formatStart = df.formatDate(cStart);
                formatEnd = df.formatDate(cEnd);
                rtn = Utilities.formatString(USR_MSG_FILTER_BETWEEN_DATE, formatStart, formatEnd);
                break;
            default:
                break;
        }
        return rtn;
    }

}

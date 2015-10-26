package logic;

import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.Command;

public class SearchAction implements Action {
    private static final String USR_MSG_SEARCH_COMPLETE = "Search for %1$s";

    private Command cCommand;
    private TasksBag cBag;
    private String cKeyword;

    public SearchAction(Command command, TasksBag internalBag) throws LogicException {
        cCommand = command;
        cBag = internalBag;
        cKeyword = command.getSearchKeyword();
    }

    @Override
    public Feedback execute() throws LogicException {
        String formattedString;

        cBag.setSearchState(cKeyword);
        formattedString = Utilities.formatString(USR_MSG_SEARCH_COMPLETE, cKeyword);

        return new Feedback(cCommand, cBag, formattedString);
    }

}

//@@author A0125546E
package logic;

import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.CommandImpl;

public class SearchAction implements Action {
    private static final String USR_MSG_SEARCH_COMPLETE = "Search for %1$s";

    private CommandImpl cCommand;
    private TasksBag cBag;
    private String cKeyword;

    public SearchAction(CommandImpl command, TasksBag internalBag) throws LogicException {
        cCommand = command;
        cBag = internalBag;
        cKeyword = command.getText();
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;

        cBag.setSearchState(cKeyword);
        formattedString = Utilities.formatString(USR_MSG_SEARCH_COMPLETE, cKeyword);

        return new CommandFeedback(cCommand, cBag, formattedString);
    }

}

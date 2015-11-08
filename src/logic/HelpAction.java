//@@author A0125546E
package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.HelpStrings;
import parser.commands.CommandData;

public class HelpAction implements Action {

    private static final String USR_MSG_HELP_INVALID = "You can view the help for this command by typing it out by itself!";
    private CommandData cCommand;
    private TasksBag cBag;

    public HelpAction(CommandData rtnCmd, TasksBag bag) {
        cCommand = rtnCmd;
        cBag = bag;
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String msg = "";
        CommandFeedback fb;

        msg = generateHelpString();

        fb = new CommandFeedback(cCommand, cBag, msg);
        return fb;
    }

    private String generateHelpString() {
        String msg;
        if(cCommand.getSecondaryCmdType() == null){
            return HelpStrings.HELP_LIST_ALL_CMDS;
        }
        
        switch (cCommand.getSecondaryCmdType()) {
            case HELP:
                msg = HelpStrings.HELP_LIST_ALL_CMDS;
                break;
            default:
                msg = USR_MSG_HELP_INVALID;
                break;

        }
        return msg;
    }
}

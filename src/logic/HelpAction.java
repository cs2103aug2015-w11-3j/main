//@@author A0125546E
package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import parser.Command.Type;
import parser.CommandImpl;
import parser.HelpStrings;

public class HelpAction implements Action {

    private static final String USR_MSG_HELP_INVALID = "No help available for this command";
    private CommandImpl cCommand;
    private TasksBag cBag;
    
    public HelpAction(CommandImpl rtnCmd, TasksBag bag) {
        cCommand = rtnCmd;
        cBag = bag;
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String msg = "";
        CommandFeedback fb;
        Command.Type cmdType = cCommand.getSecondaryCmdType();
        
        // General help command requested by user
        if(cmdType == null){
            msg = buildGeneralHelpString();
        }else {
            msg = generateHelpString();
        }
        
        fb = new CommandFeedback(cCommand,cBag, msg);
        return fb;
    }

    private String generateHelpString() {
        String msg;
        switch(cCommand.getSecondaryCmdType()){
            case HELP:
                msg = buildString(HelpStrings.FORMAT_HELP);
                break;
            /*
            case ADD:
                msg = buildString(HelpStrings.FORMAT_ADD);
                break;
            case DELETE:
                msg = buildString(HelpStrings.FORMAT_DELETE);
                break;
            case FILTER_DATE:
                msg = buildString(HelpStrings.FORMAT_FILTER);
                break;            
            case MARK:
                msg = buildString(HelpStrings.FORMAT_MARK);
                break;
            case MOVE:
                msg = buildString(HelpStrings.FORMAT_MOVE);
                break;
            case QUIT:
                msg = buildString(HelpStrings.FORMAT_QUIT);
                break;
            case REDO:
                msg = buildString(HelpStrings.FORMAT_REDO);
                break;
            case SEARCH:
                msg = buildString(HelpStrings.FORMAT_SEARCH);
                break;
            case SHOW :
                msg = buildString(HelpStrings.FORMAT_SHOW);
                break;
            case UNDO :
                msg = buildString(HelpStrings.FORMAT_UNDO);
                break;
            case UNMARK:
                msg = buildString(HelpStrings.FORMAT_UNMARK);
                break;
            case UPDATE:
                msg = buildString(HelpStrings.FORMAT_UPDATE);
                break;
            case ALIAS:
                msg = buildString(HelpStrings.FORMAT_ALIAS);
                break;
            case CLEAR_FILTERS:
                msg = buildString(HelpStrings.FORMAT_CLEAR);
                break;
            case THEME:
                msg = buildString(HelpStrings.FORMAT_THEME);
                break;
            */
            case INVALID:       // Fallthrough
            	assert false; // should never happen; no help for invalid commands
            default:
            	assert false; // should never happen as well, this is an enum
                msg = buildString(USR_MSG_HELP_INVALID);
                break;
            
        }
        return msg;
    }

    private String buildString(String s){
        return "Enter " + s;
    }
    private String buildGeneralHelpString() {
    	return "Enter \"help " + HelpStrings.HELP_LIST_ALL_CMDS + "\"";
    }
}

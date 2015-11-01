//@@author A0125546E
package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import parser.Command.Type;
import parser.HelpStrings;

public class HelpAction implements Action {

    private static final String USR_MSG_HELP_INVALID = "No help available for this command";
    private Command cCommand;
    private TasksBag cBag;
    
    public HelpAction(Command rtnCmd, TasksBag bag) {
        cCommand = rtnCmd;
        cBag = bag;
    }

    @Override
    public Feedback execute() throws LogicException {
        String msg = "";
        Feedback fb;
        Type cmdType = cCommand.getHelpCmdType();
        
        // General help command requested by user
        if(cmdType == null){
            msg = buildString(HelpStrings.HELP_LIST_ALL_CMDS);
        }else {
            msg = generateHelpString();
        }
        
        fb = new Feedback(cCommand,cBag, msg);
        return fb;
    }

    private String generateHelpString() {
        String msg;
        switch(cCommand.getHelpCmdType()){
            case ADD:
                msg = buildString(HelpStrings.HELP_FORMAT_ADD);
                break;
            case DELETE:
                msg = buildString(HelpStrings.HELP_FORMAT_DEL);
                break;
            case FILTER_DATE:
                msg = buildString(HelpStrings.HELP_FORMAT_FILTER);
                break;
            case HELP:
                msg = buildString(HelpStrings.HELP_FORMAT_HELP);
                break;
            case MARK:
                msg = buildString(HelpStrings.HELP_FORMAT_MARK);
                break;
            case MOVE:
                msg = buildString(HelpStrings.HELP_FORMAT_MOVE);
                break;
            case QUIT:
                msg = buildString(HelpStrings.HELP_FORMAT_QUIT);
                break;
            case REDO:
                msg = buildString(HelpStrings.HELP_FORMAT_REDO);
                break;
            case SEARCH:
                msg = buildString(HelpStrings.HELP_FORMAT_SEARCH);
                break;
            case SHOW_COMPLETE:
                msg = buildString(HelpStrings.HELP_FORMAT_SHOW);
                break;
            case SHOW_DEFAULT:
                msg = buildString(HelpStrings.HELP_FORMAT_SHOW);
                break;
            case SHOW_INCOMPLETE:
                msg = buildString(HelpStrings.HELP_FORMAT_SHOW);
                break;
            case UNDO:
                msg = buildString(HelpStrings.HELP_FORMAT_UNDO);
                break;
            case UNMARK:
                msg = buildString(HelpStrings.HELP_FORMAT_UNMARK);
                break;
            case show_temp:
                msg = buildString(HelpStrings.HELP_FORMAT_HELP_TEMP);
                break;
            case UPDATE:
                msg = buildString(HelpStrings.HELP_FORMAT_UPD);
                break;
            case INVALID:       // Fallthrough
            default:
                msg = buildString(USR_MSG_HELP_INVALID);
                break;
            
        }
        return msg;
    }

    private String buildString(String s){
        return "Enter " + s;
    }
}

package logic;

import logic.exceptions.LogicException;
import parser.Command;
import parser.HelpStrings;

public class HelpAction implements Action {

    private static final String USR_MSG_HELP_INVALID = "No help available for this command";
    private Command cCommand;

    public HelpAction(Command rtnCmd) {
        cCommand = rtnCmd;
    }

    @Override
    public Feedback execute() throws LogicException {
        String msg = "";
        Feedback fb;
        
        switch(cCommand.getHelpCmdType()){
            case ADD:
                msg = buildHelp(HelpStrings.HELP_FORMAT_ADD);
                break;
            case DELETE:
                msg = buildHelp(HelpStrings.HELP_FORMAT_DEL);
                break;
            case FILTER_DATE:
                msg = buildHelp(HelpStrings.HELP_FORMAT_FILTER);
                break;
            case HELP:
                msg = buildHelp(HelpStrings.HELP_FORMAT_HELP);
                break;
            case MARK:
                msg = buildHelp(HelpStrings.HELP_FORMAT_MARK);
                break;
            case MOVE:
                msg = buildHelp(HelpStrings.HELP_FORMAT_MOVE);
                break;
            case QUIT:
                msg = buildHelp(HelpStrings.HELP_FORMAT_QUIT);
                break;
            case REDO:
                msg = buildHelp(HelpStrings.HELP_FORMAT_REDO);
                break;
            case SEARCH:
                msg = buildHelp(HelpStrings.HELP_FORMAT_SEARCH);
                break;
            case SHOW_COMPLETE:
                msg = buildHelp(HelpStrings.HELP_FORMAT_SHOW);
                break;
            case SHOW_DEFAULT:
                msg = buildHelp(HelpStrings.HELP_FORMAT_SHOW);
                break;
            case SHOW_INCOMPLETE:
                msg = buildHelp(HelpStrings.HELP_FORMAT_SHOW);
                break;
            case UNDO:
                msg = buildHelp(HelpStrings.HELP_FORMAT_UNDO);
                break;
            case UNMARK:
                msg = buildHelp(HelpStrings.HELP_FORMAT_UNMARK);
                break;
            case show_temp:
                msg = buildHelp(HelpStrings.HELP_FORMAT_HELP_TEMP);
                break;
            case UPDATE:
                msg = buildHelp(HelpStrings.HELP_FORMAT_UPD);
                break;
            case INVALID:       // Fallthrough
            default:
                msg = buildHelp(USR_MSG_HELP_INVALID);
                break;
            
        }
        fb = new Feedback(cCommand,null, msg);
        return fb;
    }

    private String buildHelp(String s){
        return "" + s;
    }
}

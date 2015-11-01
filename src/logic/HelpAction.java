package logic;

import logic.exceptions.LogicException;
import parser.Command;
import parser.HelpStrings;

public class HelpAction implements Action {

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
                break;
            case FILTER_DATE:
                break;
            case HELP:
                break;
            case INVALID:
                break;
            case MARK:
                break;
            case MOVE:
                break;
            case QUIT:
                break;
            case REDO:
                break;
            case SEARCH:
                break;
            case SHOW_COMPLETE:
                break;
            case SHOW_DEFAULT:
                break;
            case SHOW_INCOMPLETE:
                break;
            case UNDO:
                break;
            case UNMARK:
                break;
            case UPDATE:
                break;
            case show_temp:
                break;
            default:
                break;
            
        }
        fb = new Feedback(cCommand,null, msg);
        return fb;
    }

    private String buildHelp(String s){
        return "";
    }
}

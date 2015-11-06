package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import ui.view.CelebiViewController;

public class ThemeChangeAction implements Action {
    private static final String USR_MSG_THEME_NIGHT = "Switched to night theme~!";
    private static final String USR_MSG_THEME_DAY = "Switched to day theme~!";
    private Command cCommand;
    private TasksBag cBag;
    private CelebiViewController.Skin cSkin;

    public ThemeChangeAction(Command comd, TasksBag bag) {
        cCommand = comd;
        cBag = bag;
        cSkin = cCommand.getTheme();
        assert cSkin != null;
    }

    @Override
    public Feedback execute() throws LogicException {
        String msg = "";
        switch(cSkin){
            case DAY:
                msg = USR_MSG_THEME_DAY;
                break;
            case NIGHT:
                msg = USR_MSG_THEME_NIGHT;
                break;
            default:
                break;
            
        }
        CommandFeedback fb = new CommandFeedback(cCommand, cBag, msg);
        return fb;
    }

}

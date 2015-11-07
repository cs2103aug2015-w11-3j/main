package logic;

import java.io.IOException;

import common.Configuration;
import common.ConfigurationInterface;
import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import ui.view.CelebiViewController;

public class ThemeChangeAction implements Action {
    private static final String USR_MSG_THEME_NIGHT = "Switched to night theme~!";
    private static final String USR_MSG_THEME_DAY = "Switched to day theme~!";
    private static final String USR_MSG_FAIL_SAVE = "Fail to write new settings into configuration";
    private Command cCommand;
    private TasksBag cBag;
    private ConfigurationInterface cConfig;
    private CelebiViewController.Skin cSkin;

    public ThemeChangeAction(Command comd, TasksBag bag) {
    	cConfig = Configuration.getInstance();
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
        
        try {
        	cConfig.setSkin(cSkin);
        } catch (IOException e) {
        	msg = msg + USR_MSG_FAIL_SAVE;
        }
        
        CommandFeedback fb = new CommandFeedback(cCommand, cBag, msg);
        return fb;
    }

}

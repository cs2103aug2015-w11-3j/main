package logic;

import java.io.IOException;

import common.Configuration;
import common.ConfigurationInterface;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.commands.CommandData;
import ui.view.CelebiViewController;

//@@author A0125546E
public class ThemeChangeAction implements Action {
    private static final String USR_MSG_THEME_NIGHT = "Switched to night theme~!";
    private static final String USR_MSG_THEME_DAY = "Switched to day theme~!";
    private CommandData cCommand;
    private TasksBag cBag;
    private ConfigurationInterface cConfig;
    private CelebiViewController.Skin cSkin;


    public ThemeChangeAction(CommandData comd, TasksBag bag) {
    	cConfig = Configuration.getInstance();
        cCommand = comd;
        cBag = bag;
        cSkin = cCommand.getTheme();
        assert cSkin != null;
    }

    @Override
    public Feedback execute() throws LogicException {
        String msg = "";
        String warningMsg = "";
        switch (cSkin) {
            case DAY:
                msg = USR_MSG_THEME_DAY;
                break;
            case NIGHT:
                msg = USR_MSG_THEME_NIGHT;
                break;
            default:
                break;

        }

        warningMsg = processWarning();

        CommandFeedback fb = new CommandFeedback(cCommand, cBag, msg, warningMsg);
        return fb;
    }

    private String processWarning() {
        String warningMsg = "";
        cConfig.setSkin(cSkin.toString());
        return warningMsg;
    }

}

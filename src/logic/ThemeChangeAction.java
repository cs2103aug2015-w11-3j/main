package logic;

import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import ui.view.CelebiViewController;

public class ThemeChangeAction implements Action {

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
        CommandFeedback fb = new CommandFeedback(cCommand, cBag);
        return fb;
    }

}

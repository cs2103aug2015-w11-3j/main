package ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;

import java.util.logging.Logger;

import common.*;
import logic.CommandFeedback;
import logic.KeyEventFeedback;
import logic.Logic;
import logic.LogicInterface;
import logic.exceptions.LogicException;
import ui.view.CelebiViewController;

public class UI implements UIInterface {

    private static final String UI_TXT_USRCMD = "You: %1$s\n";
    private static final String UI_TXT_FEEDBACK = "Celebi: %1$s";
    private static final String UI_TXT_WARNING = "Warning: %1$s";
    private static final String UI_TXT_WELCOME = "Celebi: Welcome to Celebi! Is there anything that Celebi can help you?";
    private static final String UI_TXT_TABEVENT = "You pressed tab!\n";

    LogicInterface logic;
    private CelebiViewController controller;
    private TasksBag cb = new TasksBag();
    private Logger log;

    @Override
    public void init() {
        System.out.println("UI Init");
        logic = new Logic();
        logic.init();

        String s = Configuration.getInstance().getUsrFileDirectory();

        while (logic.initData(s) == false) {
            // Failed to load data, query user to give filename
            s = "NEW_LOCATION.txt";
        }

        System.out.println("UI Init complete");
        // logic.executeCommand("display"); can do this for default display?
    }

    public UI() {
        log = Logger.getLogger("UI");
    }

    /**
     * Pass the command to logic and display to user
     * 
     * @param userInput
     */
    public void passCommand(String userInput) {
        controller.clearCommand();
        controller.clearFeedback();

        String usrCmd = Utilities.formatString(UI_TXT_USRCMD, userInput);
        controller.showFeedback(usrCmd);

        CommandFeedback fb = null;
        String usrMsg = "";
        try {
            fb = logic.executeCommand(userInput);

            switch (fb.getCommand().getCmdType()) {
                case QUIT:
                    doQuit();
                    break;
                case THEME:
                    doTheme(fb);
                    break;
                default:
                    doDefault(fb);
                    break;
            }
        } catch (LogicException e) {
            usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, e.cMsg);
            controller.showFeedback(usrMsg);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.toString());
        }
    }

    private void doDefault(CommandFeedback fb) {
        String usrMsg, warningMsg;
        cb = fb.getcBag();
        display(cb);
        
        usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, fb.getMsg());
        warningMsg = Utilities.formatString(UI_TXT_WARNING, fb.getWarningMsg());
        
        controller.showFeedback(usrMsg);
        if(fb.getWarningMsg() != null && fb.getWarningMsg() != "") {
        	controller.showWarning(warningMsg);
        }
    }

    private void doQuit() {
        System.out.println("Quit entered.");
        Platform.exit();
    }

    private void doTheme(CommandFeedback fb) {
        String usrMsg;
        CelebiViewController.Skin skin = fb.getCommand().getTheme();
        switch (skin) {
            case DAY:
                controller.switchDaySkin();
                break;

            case NIGHT:
                controller.switchNightSkin();
                break;
        }

        usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, fb.getMsg());
        controller.showFeedback(usrMsg);
    }

    /**
     * x Display the default table view
     */
    public void showWelcomeView() {
        display(logic.getDefaultBag()); // Get default view

        controller.clearFeedback();
        controller.showFeedback(UI_TXT_WELCOME);
    }

    /**
     * Display the celebi bag in table view
     * 
     * @param cb
     */
    private void display(TasksBag cb) {
        controller.updateUI(cb);
    }

    public ObservableList<Task> getCelebiList() {
        return cb.getList();
    }

    public void setController(CelebiViewController controller) {
        this.controller = controller;
    }

    @Override
    public void passKeyEvent(KeyCode whichKey) {
        controller.clearCommand();
        controller.clearFeedback();

        String usrCmd = Utilities.formatString(UI_TXT_TABEVENT);
        controller.showFeedback(usrCmd);

        KeyEventFeedback cmd = null;
        String usrMsg = "";
        try {
            cmd = logic.executeKeyEvent(whichKey);
            cb = cmd.getcBag();
            display(cb);
            usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, cmd.getMsg());
            controller.showFeedback(usrMsg);

        } catch (LogicException e) {

            usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, e.cMsg);
            controller.showFeedback(usrMsg);
        } catch (Exception e) {
            log.severe(e.toString());
        }
    }
}

//@@author A0133895U
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
        
        // Initialize logic
        logic = new Logic();
        logic.init();

        // Get configuration
        ConfigurationInterface config = Configuration.getInstance();
        String s = config.getUsrFileDirectory();

        // Failed to load data, query user to give filename
        while (logic.initData(s) == false) {
            s = "NEW_LOCATION.txt";
        }

        System.out.println("UI Init complete");
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

        // show the user command
        String usrCmd = Utilities.formatString(UI_TXT_USRCMD, userInput);
        controller.showFeedback(usrCmd);

        CommandFeedback fb = null;
        String usrMsg = "";
        // pass the command to logic and get feedback from it
        try {
            fb = logic.executeCommand(userInput);

            // update UI according to the feedback received
            switch (fb.getCommand().getCmdType()) {
                case QUIT:
                    doQuit();
                    break;
                case THEME:
                    doSkin(fb);
                    break;
                default:
                    doDefault(fb);
                    break;
            }
        } catch (LogicException e) {
            // show the error message if logic exception caught
        	usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, e.cMsg);
            controller.showFeedback(usrMsg);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.toString());
        }
    }
    
    /**
     * The default action when nothing goes wrong
     * @param fb
     */
    private void doDefault(CommandFeedback fb) {
        String usrMsg, warningMsg;
        cb = fb.getcBag();
        display(cb);
        
        usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, fb.getMsg());
        warningMsg = Utilities.formatString(UI_TXT_WARNING, fb.getWarningMsg());
        
        // show user the feedback message and warning message if there is any
        controller.showFeedback(usrMsg);
        if(fb.getWarningMsg() != null && fb.getWarningMsg() != "") {
        	controller.showWarning(warningMsg);
        }
    }

    /**
     * Quit Celebi action
     */
    private void doQuit() {
        System.out.println("Quit entered.");
        Platform.exit();
    }

    /**
     * Change theme action
     * @param fb
     */
    private void doSkin(CommandFeedback fb) {
        String usrMsg;
        CelebiViewController.Skin skin = fb.getCommand().getTheme();
        
        // update the skin according to the command feedback
        switch (skin) {
            case DAY:
                controller.switchDaySkin();
                break;

            case NIGHT:
                controller.switchNightSkin();
                break;
        }

        // show the feedback message
        usrMsg = Utilities.formatString(UI_TXT_FEEDBACK, fb.getMsg());
        controller.showFeedback(usrMsg);
    }

    /**
     * Display the default table view
     */
    public void showWelcomeView() {
    	// Get default view
    	display(logic.getDefaultBag());

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

    
    //@@author A0125546E
    /**
     * Pass the key event to logic
     */
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

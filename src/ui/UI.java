package ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import common.*;
import logic.Feedback;
import logic.Logic;
import logic.LogicInterface;
import logic.exceptions.LogicException;
import parser.Command;
import ui.view.CelebiViewController;

public class UI implements UIInterface {

    LogicInterface logic;
    private CelebiViewController controller;
    private TasksBag cb = new TasksBag();

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

    }

    /**
     * Pass the command to logic and display to user
     * 
     * @param userInput
     */
    public void passCommand(String userInput) {
        controller.clearCommand();
        controller.clearFeedback();
        controller.appendFeedback("You: " + userInput + "\n");

        Feedback cmd = null;
        String feedback = "";
        try {
            cmd = logic.executeCommand(userInput);
            if (cmd.getCommand().getCmdType() == Command.Type.QUIT) {
                System.out.println("Quit entered.");
                Platform.exit();
            } else {
                cb = cmd.getcBag();
                display(cb);
                feedback = cmd.getMsg(); // "Celebi: Add entered. \n";
                controller.appendFeedback("Celebi: " + feedback);
            }
        } catch (LogicException e) {
            // TODO Auto-generated catch block
            feedback = e.cMsg;
            controller.appendFeedback("Celebi: " + feedback);
        }
    }

    /**
     * Display the default table view
     */
    public void showWelcomeView() {
        display(logic.getDefaultBag()); // Get default view
        String feedback = "Celebi: Welcome to Celebi! Is there anything that Celebi can help you?";
        controller.clearFeedback();
        controller.appendFeedback(feedback);
    }

    /**
     * Display the celebi bag in table view
     * 
     * @param cb
     */
    private void display(TasksBag cb) {
        controller.refreshSelection(cb);
        controller.updateFilterDisplay(cb);
        controller.updateTableItems(cb.getList());
    }

    public ObservableList<Task> getCelebiList() {
        return cb.getList();
    }

    public void setController(CelebiViewController controller) {
        this.controller = controller;
    }
}

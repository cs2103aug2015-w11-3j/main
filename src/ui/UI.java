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
     * @param userInput
     */
	public void passCommand(String userInput) {
		controller.clearFeedback();
		controller.appendFeedback("You: " + userInput);
		
		Feedback cmd = null;
		String feedback = "";
		try {
			cmd = logic.executeCommand(userInput);
			if(cmd.getCommand().getCmdType() == Command.Type.QUIT){
				System.out.println("Quit entered.");
				Platform.exit();
			} else {
				cb = cmd.getcBag();
				display(cb);
				feedback = cmd.getMsg(); //"Celebi: Add entered. \n";
				controller.appendFeedback(feedback);
			}
			/*
			switch (cmd.getCommand().getCmdType()) {
			case Add:
				cb = cmd.getcBag();
				feedback = "Celebi: Add entered. \n";
				controller.appendFeedback(feedback);
				
				display(cb);
				
				break;
			case Delete:
				feedback = "Celebi: Delete entered. \n";
				
				cb = cmd.getcBag();		// Ken added
				display(cb);			// Ken added
				controller.appendFeedback(feedback);
				
				break;
			case Sort: // Ken added
				cb = cmd.getcBag();		
				display(cb);
				feedback = "Celebi: sort entered. \n";
				controller.appendFeedback(feedback);
				break;
			case ShowAll: // Ken added
				cb = cmd.getcBag();		
				display(cb);
				feedback = "Celebi: show all entered. \n";
				controller.appendFeedback(feedback);
				break;
			case Quit:
				System.out.println("Quit entered.");
				Platform.exit();
				break;
			default: // Ken added to resolve issues when new switch cases are implemented but not yet configured
				cb = cmd.getcBag();		
				display(cb);
				feedback = cmd.getCommand() + " type entered but not captured. \n";
				controller.appendFeedback(feedback);
				break;
			}
			*/
		} catch (LogicException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			feedback = e.cMsg;
			controller.appendFeedback("Celebi: " + feedback + "\n");
		}
	}
	
	/**
	 * Display the default table view
	 */
	public void showWelcomeView() {
		display(logic.getTaskBag());
		String feedback = "Celebi: Welcome to Celebi! Is there anything that Celebi can help you? \n";
		controller.appendFeedback(feedback);
	}
	
	/**
	 * Display the celebi bag in table view
	 * @param cb
	 */
	private void display(TasksBag cb) {
		// TODO Auto-generated method stub
		System.out.println("No. of elements in system: " + cb.size());
		for(Task c : cb){
			System.out.println(c.getName());
		}
		
		controller.setTableItems(cb.getList());
	}
	
	public ObservableList<Task> getCelebiList() {
        return cb.getList();
    }
	
	public void setController(CelebiViewController controller) {
		this.controller = controller;
	}
}

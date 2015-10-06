package ui;

import java.io.IOException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import common.Celebi;
import common.CelebiBag;
import common.Common;
import logic.IntegrityCommandException;
import logic.Logic;
import logic.LogicInterface;
import parser.Command;
import ui.view.CelebiViewController;

public class UI implements UIInterface {

	LogicInterface logic;
	private CelebiViewController controller;
	private CelebiBag cb = new CelebiBag();
	
    
	@Override
	public void init() {
		System.out.println("UI Init");
		logic = new Logic();
		logic.init();

		String s = Common.getInstance().getUsrFileDirectory();

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
		Command cmd = null;
		String feedback = "";
		try {
			cmd = logic.executeCommand(userInput);
		} catch (IntegrityCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (cmd.getCmdType()) {
		case Add:
			cb = logic.getCelebiBag();
			// to change
			feedback = "Add entered.";
			controller.appendFeedback(feedback);
			
			display(cb);
			
			break;
		case Quit:
			System.out.println("Quit entered.");
			Platform.exit();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Display the default table view
	 */
	public void showWelcomeView() {
		display(logic.getCelebiBag());
	}
	
	/**
	 * Display the celebi bag in table view
	 * @param cb
	 */
	private void display(CelebiBag cb) {
		// TODO Auto-generated method stub
		System.out.println("No. of elements in system: " + cb.size());
		for(Celebi c : cb){
			System.out.println(c.getName());
		}
		
		controller.setTableItems(cb.getList());
	}
	
	public ObservableList<Celebi> getCelebiList() {
        return cb.getList();
    }
	
	public void setController(CelebiViewController controller) {
		this.controller = controller;
	}
}

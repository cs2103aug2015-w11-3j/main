package ui;

import java.util.Scanner;

import common.Celebi.Command;
import common.Common;
import logic.Logic;
import logic.LogicInterface;

public class UI implements UIInterface {

	LogicInterface logic;

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

	public void run() {

		Scanner sc = new Scanner(System.in);
		boolean isRunning = true;
		while (isRunning) {
			Command cmd = logic.executeCommand(sc.nextLine());
			switch (cmd) {
			case Quit:
				System.out.println("Quit entered.");
				isRunning = false;
				break;
			default:
				break;
			}
		}

		sc.close();
	}
}

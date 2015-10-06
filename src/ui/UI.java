package ui;

import java.util.Scanner;

import common.Celebi;
import common.CelebiBag;
import common.Common;
import logic.Feedback;
import logic.IntegrityCommandException;
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
			Feedback fb = null;
			try {
				fb = logic.executeCommand(sc.nextLine());
			} catch (IntegrityCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch (fb.getCommand().getCmdType()) {
			case Add:
				CelebiBag cb = logic.getCelebiBag();
				System.out.println("Add entered.");
				
				display(cb);
				
				break;
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

	private void display(CelebiBag cb) {
		// TODO Auto-generated method stub
		System.out.println("No. of elements in system: " + cb.size());
		for(Celebi c : cb){
			System.out.println(c.getName());
		}
	}
}

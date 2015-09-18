package logic;

import common.Celebi;
import common.Celebi.Command;
import common.CelebiBag;
import parser.Parser;
import parser.ParserInterface;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

	StorageInterface storage;
	ParserInterface parser;
	CelebiBag mBag;

	public Logic() {

	}

	@Override
	public void init() {
		System.out.println("Logic Init");
		
		storage = new Storage();
		storage.init();
		parser = new Parser();
		parser.init();
		
		System.out.println("Logic Init complete");
	}

	@Override
	public Command executeCommand(String cmd) {
		Celebi rtnCelebi = parser.parseCommand(cmd);
		// DO things with it
		switch (rtnCelebi.getCmd()) {
		case Add:
			break;
		case Quit:
			System.out.println("Logic received quit");
			break;
		default:
			break;
		}
		return rtnCelebi.getCmd();
	}

	@Override
	public boolean initData(String s) {
		
		return storage.load(s, mBag);
	}

	@Override
	public CelebiBag getCelebiBag() {
		// TODO Auto-generated method stub
		return mBag;
	}

}

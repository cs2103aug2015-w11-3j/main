package logic;

import common.Celebi;
import common.CelebiBag;
import parser.Command;
import parser.Parser;
import parser.ParserInterface;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

	StorageInterface storage;
	ParserInterface parser;
	CelebiBag mBag;

	public Logic() {
		mBag = new CelebiBag();
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
	public Command executeCommand(String cmd) throws IntegrityCommandException {
		Command rtnCmd = parser.parseCommand(cmd);
		Celebi rtnCelebi = null;
		
		try
		{
			verifyCommand(rtnCmd);
		}
		catch(IntegrityCommandException e){
			throw e;
		}
		
		
		switch (rtnCmd.getCmdType()) {
			case Add:
				rtnCelebi = createCelebi(rtnCmd);
				storage.save(rtnCelebi);
				break;
				
			case Quit:
				System.out.println("Logic received quit");
				break;
			default:
				break;
		}
		return rtnCmd;
	}

	private Celebi createCelebi(Command rtnCmd) {
		// TODO Auto-generated method stub
		Celebi tCelebi = new Celebi("PARSEDCOMMAND NOT YET IMPLEMENTED", null, null);
		
		if(true);	// hasDate data

			
		mBag.addCelebi(tCelebi);
		return tCelebi;
	}

	private void verifyCommand(Command rtnCmd) throws IntegrityCommandException {
		
		// Check date
		// Check
		
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

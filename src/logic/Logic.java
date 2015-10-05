package logic;

import java.util.Date;

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
				
		switch (rtnCmd.getCmdType()) {
			case Add:
				rtnCelebi = createCelebi(rtnCmd);
				storage.save(rtnCelebi);
				break;
				
			case Delete:

				doDelete(rtnCmd);
				break;
			
			case Update:
				
				doUpdate(rtnCmd);
				break;
				
			case Quit:
				System.out.println("Logic received quit");
				break;
				
			case Invalid:
				System.out.println("Logic received invalid type");
				throw new IntegrityCommandException("invalid thrown from parser");
				
			default:
				assert false : rtnCmd.getCmdType();
				break;
		}
		return rtnCmd;
	}

	private void doUpdate(Command rtnCmd) {
		int index = Integer.parseInt(rtnCmd.getName());
		Celebi recvCelebi = mBag.getCelebi(index);
		
		recvCelebi.setName(rtnCmd.getName());
	}

	private void doDelete(Command rtnCmd) {
		int index = Integer.parseInt(rtnCmd.getName());
		Celebi recvCelebi = mBag.getCelebi(index);
		boolean delStatus = storage.delete(recvCelebi);
		
		if(delStatus){	// Removed successfully
			mBag.removeCelebi(index);
		}else{
			// Throw error?
		}
		
	}

	private Celebi createCelebi(Command rtnCmd) throws IntegrityCommandException {
		// TODO Auto-generated method stub
		try
		{
			verifyCommand(rtnCmd);
		}
		catch(IntegrityCommandException e){
			throw e;
		}
		
		String name = rtnCmd.getName();
		Date startDate = rtnCmd.getStart();
		Date endDate = rtnCmd.getEnd();
		Celebi tCelebi = new Celebi(name, startDate, endDate);
		
		boolean addStatus = storage.save(tCelebi);
		if(addStatus){	// Added successfully
			mBag.addCelebi(tCelebi);
		}else{
			// Throw?
		}
			
		
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

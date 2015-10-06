package logic;

import java.util.Date;

import common.Celebi;
import common.Celebi.DataType;
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
	public Feedback executeCommand(String cmd) throws IntegrityCommandException {
		Command rtnCmd = parser.parseCommand(cmd);
		
		// Remove this line after parser fix
		rtnCmd = testFuncs(cmd);
		
		Celebi rtnCelebi = null;
		Feedback fb;
		switch (rtnCmd.getCmdType()) {
			case Add:
				rtnCelebi = createCelebi(rtnCmd);
				storage.save(rtnCelebi);
				fb = new Feedback(rtnCmd, mBag);
				break;
				
			case Delete:

				doDelete(rtnCmd);
				fb = new Feedback(rtnCmd, mBag);
				break;
			
			case Update:
				
				doUpdate(rtnCmd);
				fb = new Feedback(rtnCmd, mBag);
				break;
			
			case Quit:
				System.out.println("Logic received quit");
				fb = new Feedback(rtnCmd, null);
				break;
				
			case Invalid:
				System.out.println("Logic received invalid type");
				throw new IntegrityCommandException("invalid thrown from parser");
				
			default:
				assert false : rtnCmd.getCmdType();
				fb = new Feedback(rtnCmd, mBag);
				break;
		}
		return fb;
	}

	/*
	 * INTEGRATION FUNCTION TO TEST OTHER IMPLEMENTATIONS
	 * PARSER HARD TO IMPLEMENT AT START, THEREFORE BYPASSING IT FOR NOW
	 */
	private Command testFuncs(String cmd) {
		if(cmd.contains("add")){
			Command rtnCmd;
			Date d = new Date();
			d.setDate( new Date().getDate() + 1);		
			rtnCmd = parser.makeAdd("HELLO", new Date(), d);
			return rtnCmd;
		} else if (cmd.contains("delete")) {
			Command rtnCmd;
			rtnCmd = parser.makeDelete(0);
			return rtnCmd;
		} else if(cmd.contains("update")){
			Command rtnCmd;
			rtnCmd = parser.makeUpdate(0, DataType.DATE_END, 20);
			return rtnCmd;
		}else if (cmd.contains("quit")){
			Command rtnCmd;
			rtnCmd = parser.makeQuit();
			return rtnCmd;
		} else {
			return parser.makeInvalid();
		}
	}

	private void doUpdate(Command rtnCmd) {
		
		// verify UID
		int UID = rtnCmd.getCelebiUID();
		if(UID < 0 || UID > mBag.size()){
			// throw error
			return;
		} else {
			Celebi toBeUpdated = mBag.getCelebi(UID);
			assert toBeUpdated != null;
			
			switch(rtnCmd.getCelebiField()){
			case BLOCKED_PERIODS:
				System.out.println("Not supported yet");
				break;
			case DATE_END:
				assert rtnCmd.getEnd() != null;
				toBeUpdated.setEnd(rtnCmd.getEnd());
				break;
			case DATE_START:
				assert rtnCmd.getStart() != null;
				toBeUpdated.setEnd(rtnCmd.getStart());
				break;
			case DESCRIPTION:
				System.out.println("Not supported yet");
				break;
			case ID:
				System.out.println("Not supported yet"); // Should never be ran?
				break;
			case IS_COMPLETED:
				System.out.println("Not supported yet");
				break;
			case NAME:
				assert rtnCmd.getName() != null;
				toBeUpdated.setName(rtnCmd.getName());
				break;
			case PRIORITY:
				System.out.println("Not supported yet");
				break;
			case SCHEDULED_DAYS:
				System.out.println("Not supported yet");
				break;
			case TAGS:
				System.out.println("Not supported yet");
				break;
			default:
				assert false : rtnCmd.getCelebiField();
				System.out.println("Invalid field type");	// Should be marked as invalid command in parser
				break;
			
			}
		}
	}

	private void doDelete(Command rtnCmd) {
		int UID = rtnCmd.getCelebiUID();
		if(UID < 0 || UID > mBag.size()){
			// throw error
		}
		Celebi recvCelebi = mBag.getCelebi(UID);
		boolean delStatus = storage.delete(recvCelebi);
		
		if(delStatus){	// Removed successfully
			mBag.removeCelebi(UID);
			System.out.println("Removed celebi successfully");
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

	
	public void setParser(ParserInterface parserStub) {
		// TODO Auto-generated method stub
		System.out.println("STUB ADDED FOR PARSER");
		parser = parserStub;
	}

}

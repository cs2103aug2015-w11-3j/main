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

	/**
	 * Passes string param to parser then evaluates the command type
	 * @exception IntegrityCommandException When given input violates validity
	 * @exception InvalidCommandException When given input cannot be understood
	 * @param userString string value entered by user
	 */
	@Override
	public Feedback executeCommand(String userString) throws IntegrityCommandException {
		Command rtnCmd = parser.parseCommand(userString);
		
		// Remove this line after parser fix
		//rtnCmd = testFuncs(userString);
		
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
				throw new IntegrityCommandException("I couldn't understand you... (>.<)");
				
			default:
				assert false : rtnCmd.getCmdType();
				fb = new Feedback(rtnCmd, mBag);
				break;
		}
		return fb;
	}

	/**
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
			Date d = new Date();
			d.setDate((int) (d.getDate() + Math.random()* 100));
			rtnCmd = parser.makeUpdate(0, DataType.DATE_END, d);
			return rtnCmd;
		}else if (cmd.contains("quit")){
			Command rtnCmd;
			rtnCmd = parser.makeQuit();
			return rtnCmd;
		} else {
			return parser.makeInvalid();
		}
	}

	private void doUpdate(Command rtnCmd) throws IntegrityCommandException {
		
		// verify UID
		int UID = rtnCmd.getCelebiUID();
		if(UID < 0 || UID >= mBag.size()){
			throw new IntegrityCommandException("Given index out of bound");
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
				storage.save(toBeUpdated);
				break;
			case DATE_START:
				assert rtnCmd.getStart() != null;
				toBeUpdated.setStart(rtnCmd.getStart());
				storage.save(toBeUpdated);
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
				storage.save(toBeUpdated);
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

	private void doDelete(Command rtnCmd) throws IntegrityCommandException {
		int UID = rtnCmd.getCelebiUID();
		if(UID < 0 || UID >= mBag.size()){
			throw new IntegrityCommandException("Given index out of bound");
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
			verifyDate(rtnCmd.getStart());
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

	private void verifyDate(Date date) throws IntegrityCommandException {
		
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

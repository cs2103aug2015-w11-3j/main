package logic;

import java.util.Date;

import common.Log;
import common.Task;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import logic.exceptions.UnknownCommandException;
import common.TasksBag;
import parser.Command;
import parser.Parser;
import parser.ParserInterface;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

	StorageInterface storage;
	ParserInterface parser;
	TasksBag mBag;
	ActionInvoker cInvoker;
	public Logic() {
		mBag = new TasksBag();
		cInvoker = new ActionInvoker();
	}

	@Override
	public void init() {
		Log.log("Logic Init", this.getClass());
		
		storage = new Storage();
		storage.init();
		parser = Parser.getParser();
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
	public Feedback executeCommand(String userString) throws LogicException {
		Command rtnCmd = parser.parseCommand(userString);
		
		Log.log("executing " + userString, this.getClass());
		
		if(userString.equals("undo")){
			cInvoker.undoAction();
			return new Feedback(parser.makeDelete(-1), mBag);
		}
		if(userString.equals("redo")){
			cInvoker.redoAction();
			return new Feedback(parser.makeDelete(-1), mBag);
		}

		Feedback fb;
		switch (rtnCmd.getCmdType()) {
			case Add:
				fb = cInvoker.placeAction(new AddAction(rtnCmd, mBag, storage));
				break;
				
			case Delete:
				fb = cInvoker.placeAction(new DeleteAction(rtnCmd, mBag, storage));
				break;
			
			case Update:
				
				doUpdate(rtnCmd);
				fb = new Feedback(rtnCmd, mBag);
				break;
			
			case Quit:
				Log.log("recevied quit", this.getClass());
				fb = new Feedback(rtnCmd, null);
				break;
				
			case Invalid:
				Log.log("recevied invalid type", this.getClass());
				throw new UnknownCommandException("I couldn't understand you... (>.<)");
				
			default:
				assert false : rtnCmd.getCmdType();
				fb = new Feedback(rtnCmd, mBag);
				break;
		}
		return fb;
	}
	
	private void doUpdate(Command rtnCmd) throws IntegrityCommandException {
		
		// verify UID
		int UID = rtnCmd.getTaskUID();
		if(UID < 0 || UID >= mBag.size()){
			throw new IntegrityCommandException("Given index out of bound");
		} else {
			Task toBeUpdated = mBag.getTask(UID);
			assert toBeUpdated != null;
			
			switch(rtnCmd.getTaskField()){
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
				assert false : rtnCmd.getTaskField();
				System.out.println("Invalid field type");	// Should be marked as invalid command in parser
				break;
			
			}
		}
	}

	

	@Override
	public boolean initData(String s) {
		
		return storage.load(s, mBag);
	}

	@Override
	public TasksBag getTaskBag() {
		return mBag;
	}

	
	public void setParser(ParserInterface parserStub) {
		System.out.println("STUB ADDED FOR PARSER");
		parser = parserStub;
	}

}

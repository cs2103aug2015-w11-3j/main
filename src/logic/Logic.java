package logic;

import java.util.Date;

import common.Task;
import common.Task.DataType;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
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
	public Feedback executeCommand(String userString) throws LogicException {
		Command rtnCmd = parser.parseCommand(userString);
		
		// Remove this line after parser fix
		//rtnCmd = testFuncs(userString);
		if(userString.equals("undo")){
			cInvoker.undoAction();
			return new Feedback(parser.makeDelete(-1), mBag);
		}
		Task rtnTask = null;
		Feedback fb;
		switch (rtnCmd.getCmdType()) {
			case Add:
				
				fb = cInvoker.placeAction(new AddAction(rtnCmd, mBag, storage));
				/*
				rtnTask = createTask(rtnCmd);
				storage.save(rtnTask);
				fb = new Feedback(rtnCmd, mBag);
				*/
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

	private void doSort(Command rtnCmd){
		// Requires command to contain sortType
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

	private void doDelete(Command rtnCmd) throws IntegrityCommandException {
		int UID = rtnCmd.getTaskUID();
		if(UID < 0 || UID >= mBag.size()){
			throw new IntegrityCommandException("Given index out of bound");
		}
		Task recvTask = mBag.getTask(UID);
		boolean delStatus = storage.delete(recvTask);
		
		if(delStatus){	// Removed successfully
			mBag.removeTask(UID);
			System.out.println("Removed task successfully");
		}else{
			// Throw error?
		}
		
	}

	private Task createTask(Command rtnCmd) throws IntegrityCommandException {
		// TODO Auto-generated method stub
		try
		{
			verifyDate(rtnCmd.getStart(), rtnCmd.getEnd());
		}
		catch(IntegrityCommandException e){
			throw e;
		}
		
		String name = rtnCmd.getName();
		Date startDate = rtnCmd.getStart();
		Date endDate = rtnCmd.getEnd();
		Task tTask = new Task(name, startDate, endDate);

		boolean addStatus = storage.save(tTask);
		if(addStatus){	// Added successfully
			mBag.addTask(tTask);
		}else{
			// Throw?
		}
			
		
		return tTask;
	}

	/*
	 * Ensures that
	 * - start date and end date must be after current time
	 * - end date must be after start date
	 */
	private boolean verifyDate(Date dateStart, Date dateEnd) throws IntegrityCommandException {
		
		if(dateStart == null && dateEnd == null){
			assert false : "both start and end dates are null";
			return false;
		} else if(dateStart == null){
			
		}
			
			
			
		if(dateStart != null && dateEnd == null){	
			return dateStart.after(new Date());
			
		} else if(dateStart == null && dateEnd != null){
			return dateEnd.after(new Date());
			
		} else if(dateStart != null && dateEnd != null){
			if(dateStart.after(dateEnd)){
				throw new IntegrityCommandException("End date is earlier than start date!");
			}
			return true;
			
		} else {	
			assert false : "both start and end dates are null";
			return false;
		}
		
	}

	@Override
	public boolean initData(String s) {
		
		return storage.load(s, mBag);
	}

	@Override
	public TasksBag getTaskBag() {
		// TODO Auto-generated method stub
		return mBag;
	}

	
	public void setParser(ParserInterface parserStub) {
		// TODO Auto-generated method stub
		System.out.println("STUB ADDED FOR PARSER");
		parser = parserStub;
	}

}

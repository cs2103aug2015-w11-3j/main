package logic;

import java.util.logging.Logger;

import common.Task;
import common.Task.DataType;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import logic.exceptions.UnknownCommandException;
import common.TasksBag;
import parser.Command;
import parser.Parser;
import parser.ParserInterface;
import parser.SortType;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

	
	
	StorageInterface storage;
	ParserInterface parser;
	TasksBag cInternalBag, cShowBag;
	ActionInvoker cInvoker;
	Logger log;

	public Logic() {
		cInternalBag = new TasksBag();
		cInvoker = new ActionInvoker();
		log = Logger.getLogger("Logic");
	}

	@Override
	public void init() {

		storage = new Storage();
		storage.init();
		parser = Parser.getParser();
		parser.init();

		System.out.println("Logic Init complete");
	}

	/**
	 * Passes string param to parser then evaluates the command type
	 * 
	 * @exception IntegrityCommandException
	 *                When given input violates validity
	 * @exception InvalidCommandException
	 *                When given input cannot be understood
	 * @param userString
	 *            string value entered by user
	 */
	@Override
	public Feedback executeCommand(String userString) throws LogicException {
		Command rtnCmd = parser.parseCommand(userString);

		log.info("executing " + userString);

		if (userString.equals("undo")) {
			// cInvoker.undoAction();
			rtnCmd = parser.makeType(Command.Type.Undo);
		}
		if (userString.equals("redo")) {
			// cInvoker.redoAction();
			rtnCmd = parser.makeType(Command.Type.Redo);
		}
		if (userString.equals("sort")) {
			rtnCmd = parser.makeSort();
		}
		if (userString.equals("mark")) {
			rtnCmd = parser.makeType(Command.Type.Mark);
		}
		if (userString.equals("unmark")) {
			rtnCmd = parser.makeType(Command.Type.Unmark);
		}
		if (userString.equals("show")) {
			rtnCmd = parser.makeType(Command.Type.ShowAll);
		}
		
		
		Feedback fb;
		switch (rtnCmd.getCmdType()) {
			case Add:
				fb = cInvoker.placeAction(new AddAction(rtnCmd, cInternalBag, storage));
				break;
			case Delete:
				fb = cInvoker.placeAction(new DeleteAction(rtnCmd, cInternalBag, storage));
				break;
			case Sort:
				fb = cInvoker.placeAction(new SortAction(rtnCmd, cInternalBag, TasksBag.SortBy.MARK));
				break;
			case Update:
				// Not command pattern yet
				doUpdate(rtnCmd);
				fb = new Feedback(rtnCmd, cInternalBag);
				break;
			case ShowAll:
				fb = cInvoker.placeAction(new SortAction(rtnCmd, cInternalBag, TasksBag.SortBy.NONE));
				break;
			case Mark:
				// Half completed, does not mark any
				if(cInternalBag.getSorted().isEmpty()){
					throw new IntegrityCommandException("Provided index not on list.");
				} else {
					Task t = cInternalBag.getSorted().getTask(0);
					t.setComplete(true);
				}
				fb = new Feedback(rtnCmd, cInternalBag);
				break;
			case Unmark:
				// Half completed, does not mark any
				if(cInternalBag.getSorted().isEmpty()){
					throw new IntegrityCommandException("Provided index not on list.");
				} else {
					Task t2 = cInternalBag.getSorted().getTask(0);
					t2.setComplete(false);
				}
				fb = new Feedback(rtnCmd, cInternalBag);
				break;
			case Undo:
				cInvoker.undoAction();
				fb = new Feedback(rtnCmd, cInternalBag);
				break;
			case Redo:
				cInvoker.redoAction();
				fb = new Feedback(rtnCmd, cInternalBag);
				break;
			case Quit:
				log.info("recevied quit");
				fb = new Feedback(rtnCmd, null);
				break;
			case Invalid:
				log.info("recevied invalid type");
				throw new UnknownCommandException("I couldn't understand you... (>.<)");

			default:
				assert false : rtnCmd.getCmdType();
				fb = new Feedback(rtnCmd, cInternalBag);
				break;
		}
		return fb;
	}

	private void doUpdate(Command rtnCmd) throws IntegrityCommandException {

		// verify UID
		int UID = rtnCmd.getTaskUID();
		if (UID < 0 || UID >= cInternalBag.size()) {
			throw new IntegrityCommandException("Given index out of bound");
		} else {
			Task toBeUpdated = cInternalBag.getTask(UID);
			assert toBeUpdated != null;

			switch (rtnCmd.getTaskField()) {
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
					System.out.println("Not supporting");
					break;
				case ID:
					System.out.println("Not supporting"); // Should never be
																// ran?
					break;
				case IS_COMPLETED:
					System.out.println("Not use mark/unmark?");
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
					System.out.println("Not supporting");
					break;
				case TAGS:
					System.out.println("Not supporting");
					break;
				default:
					assert false : rtnCmd.getTaskField();
					System.out.println("Invalid field type"); // Should be
																// marked as
																// invalid
																// command
																// in parser
					break;

			}
		}
	}

	@Override
	public boolean initData(String s) {

		boolean rtnVal = storage.load(s, cInternalBag);
		cShowBag = cInternalBag;
		return rtnVal;
	}

	@Override
	public TasksBag getTaskBag() {
		return cInternalBag;
	}

	public void setParser(ParserInterface parserStub) {
		System.out.println("STUB ADDED FOR PARSER");
		parser = parserStub;
	}

}

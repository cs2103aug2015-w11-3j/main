package logic;

import common.TasksBag;
import common.TasksBag.SortBy;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;

/**
 * @author MonoChrome
 * Changes the state of the internal taskbags to given sort state
 * Undo sets the internal bag state to previous state
 */
public class SortAction implements UndoableAction {
	private Command cCommand;
	private TasksBag cBag;
	private SortBy cSortBy;
	private SortBy cPrevSortBy;
	
	public SortAction(Command command, TasksBag internalBag, SortBy sortBy){
		cCommand = command;
		cBag = internalBag;
		cSortBy = sortBy;		
	}
	
	@Override
	public Feedback execute() throws LogicException {
		// TODO Auto-generated method stub
		cPrevSortBy = cBag.getState();
		cBag.setSortState(cSortBy);
		return new Feedback(cCommand, cBag);
	}

	@Override
	public void undo() {
		cBag.setSortState(cPrevSortBy);
	}

	@Override
	public void redo() throws LogicException {
		execute();
	}

}

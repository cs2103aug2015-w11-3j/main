package logic;

import java.util.ArrayList;

import logic.exceptions.LogicException;
import logic.exceptions.NoRedoActionException;
import logic.exceptions.NoUndoActionException;

/**
 * Invoker part of Command Pattern 
 * Deals with keeping track of what actions have been done, 
 * Undo/Redo support
 */

public class ActionInvoker {
	
	private final String MSG_NO_UNDO = "No undoable actions found."; 
	private final String MSG_NO_REDO = "No redoable actions found."; 
	private ArrayList<Action> cUndo, cRedo;
	
	public ActionInvoker(){
		
		cUndo = new ArrayList<Action>(); 
		cRedo = new ArrayList<Action>();
	}
	
	public Feedback placeAction(Action act) throws LogicException {
		assert cUndo != null : "Undo arraylist is null";
		
		if(act instanceof UndoableAction){
			cUndo.add(act);
			cRedo.clear();			// Clearing redo list, 
									// should not have redo possible 
									// if new actions executed
		}
		
		return act.execute();
	}
	
	public void undoAction() throws NoUndoActionException{
		assert cUndo != null : "Undo arraylist is null";
		
		if(cUndo.size() == 0){
			throw new NoUndoActionException(MSG_NO_UNDO);
		}
		
		UndoableAction undoAction = (UndoableAction) cUndo.remove(cUndo.size()-1);
		cRedo.add(undoAction);
		
		undoAction.undo();
	}
	
	public void redoAction() throws NoRedoActionException, LogicException{
		assert cRedo != null : "Redo arraylist is null";
		
		if(cRedo.size() == 0){
			throw new NoRedoActionException(MSG_NO_REDO);
		}
		
		UndoableAction redoAction = (UndoableAction) cRedo.remove(cRedo.size()-1);
		cUndo.add(redoAction);
		
		redoAction.redo();
	}
}

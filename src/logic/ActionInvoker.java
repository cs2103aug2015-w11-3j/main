package logic;

import java.util.ArrayList;

import logic.exceptions.NoUndoActionException;

/*
 * Invoker part of Command Pattern 
 * Deals with keeping track of what actions have been done, 
 * Undo/Redo support
 */


public class ActionInvoker {
	
	private final String MSG_NO_UNDO = "No undoable actions found."; 
	ArrayList<Action> cUndo, cRedo;
	
	public ActionInvoker(){
		
		cUndo = new ArrayList<Action>(); 
		cRedo = new ArrayList<Action>();
	}
	
	public Feedback placeAction(Action act){
		assert cUndo != null : "Undo arraylist is null";
		
		if(act instanceof UndoableAction){
			cUndo.add(act);
		}
		
		return act.execute();
	}
	
	public void undoAction() throws NoUndoActionException{
		assert cUndo != null : "Undo arraylist is null";
		if(cUndo.size() == 0){
			throw new NoUndoActionException(MSG_NO_UNDO);
		}
		UndoableAction undoAction = (UndoableAction) cUndo.remove(cUndo.size()-1);

		undoAction.undo();
	}
}

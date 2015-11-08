//@@author A0125546E
package logic;

import java.util.ArrayList;

import logic.exceptions.LogicException;
import logic.exceptions.NoRedoActionException;
import logic.exceptions.NoUndoActionException;

/***
 * Invoker part of Command Pattern Deals with keeping track of what actions have
 * been done, Undo/Redo support.
 * 
 * Note the issue when redo-ing on a "switched" filter on the bag. Might cause
 * unintended side effects
 */

public class ActionInvoker {

    private final String MSG_NO_UNDO = "No undoable actions found.";
    private final String MSG_NO_REDO = "No redoable actions found.";
    private ArrayList<Action> cUndo;
    private ArrayList<Action> cRedo;

    public ActionInvoker() {
        cUndo = new ArrayList<Action>();
        cRedo = new ArrayList<Action>();
    }

    public Feedback placeAction(Action act) throws LogicException {
        assert cUndo != null : "Undo arraylist is null";

        // execute may throw exceptions
        Feedback fb = act.execute();

        // If execution passes, then we add into undo queue
        if (act instanceof UndoableAction) {
            cUndo.add(act);

            // Clearing redo list, should not have redo possible
            // if new actions executed
            cRedo.clear();
        }
        return fb;
    }

    public Feedback undoAction() throws NoUndoActionException {
        assert cUndo != null : "Undo arraylist is null";

        if (cUndo.size() == 0) {
            throw new NoUndoActionException(MSG_NO_UNDO);
        }

        UndoableAction undoAction = (UndoableAction) cUndo.remove(cUndo.size() - 1);
        cRedo.add(undoAction);

        return undoAction.undo();
    }

    public Feedback redoAction() throws NoRedoActionException, LogicException {
        assert cRedo != null : "Redo arraylist is null";

        if (cRedo.size() == 0) {
            throw new NoRedoActionException(MSG_NO_REDO);
        }

        UndoableAction redoAction = (UndoableAction) cRedo.remove(cRedo.size() - 1);
        cUndo.add(redoAction);

        return redoAction.redo();
    }
}

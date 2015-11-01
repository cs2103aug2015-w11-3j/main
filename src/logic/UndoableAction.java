//@@author A0125546E
package logic;

import logic.exceptions.LogicException;

public interface UndoableAction extends Action {
    
    public Feedback undo();

    public Feedback redo() throws LogicException;
}

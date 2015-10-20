package logic;

import logic.exceptions.LogicException;

public interface UndoableAction extends Action {
    
    public void undo();

    public void redo() throws LogicException;
}

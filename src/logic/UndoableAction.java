package logic;

public interface UndoableAction extends Action {
	public void undo();
}
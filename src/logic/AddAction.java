package logic;

import java.util.Date;

import common.Task;
import common.TasksBag;
import parser.Command;
import storage.StorageInterface;

public class AddAction implements UndoableAction {

	private Command cCommand;
	private TasksBag cBag;
	private StorageInterface cStore;
	private Task cCreatedTask;

	public AddAction(Command command, TasksBag bag, StorageInterface stor) {
		cCommand = command;
		cBag = bag;
		cStore = stor;
	}

	@Override
	public Feedback execute() {
		assert cCommand.getCmdType() == Command.Type.Add : cCommand.getCmdType();

		String name = cCommand.getName();
		Date startDate = cCommand.getStart();
		Date endDate = cCommand.getEnd();

		cCreatedTask = new Task(name, startDate, endDate);

		boolean addStatus = cStore.save(cCreatedTask);

		if (addStatus) {
			// Added successfully
			cBag.addTask(cCreatedTask);
		} else {
			// Throw?
		}

		Feedback fb = new Feedback(cCommand, cBag);
		return fb;
	}

	@Override
	public void undo() {
		cStore.delete(cCreatedTask);
		cBag.removeTask(cCreatedTask);
	}

	@Override
	public void redo() {
		execute();
	}

}

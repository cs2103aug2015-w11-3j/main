package logic;

import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class MarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private Command cCommand;
    private TasksBag cBag;
    private StorageInterface cStore;
    private Task cModifiedTask;

    public MarkAction(Command command, TasksBag bag, StorageInterface stor) {
        cCommand = command;
        cBag = bag;
        cStore = stor;

    }

    @Override
    public Feedback execute() throws LogicException {
        assert cCommand.getCmdType() == Command.Type.MARK : cCommand.getCmdType();

        Feedback fb;

        if (cBag.getFlitered().isEmpty()) {
            throw new IntegrityCommandException(USR_MSG_INDEX_ERR);
        } else {
            cModifiedTask = cBag.getFlitered().getTask(0);
            cModifiedTask.setComplete(true);
            cStore.save(cModifiedTask);
        }

        fb = new Feedback(cCommand, cBag);

        return fb;
    }

    @Override
    public void undo() {
        assert cModifiedTask != null;

        cModifiedTask.setComplete(false);
        cStore.save(cModifiedTask);
    }

    @Override
    public void redo() throws LogicException {
        execute();
    }

}

package logic;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class UpdateAction implements UndoableAction {
    private static final String USR_MSG_UPDATE_OOB = "Provided index not on list.";
    private static final String USR_MSG_UPDATE_ERROR = "Failed to store to storage";
    private static final String USR_MSG_UPDATE_STARTDATE_OK = "Updated start date of %1$s!";
    private static final String USR_MSG_UPDATE_ENDDATE_OK = "Updated end date of %1$s!";
    private static final String USR_MSG_UPDATE_NAME_OK = "Updated name of %1$s!";
    private static final String USR_MSG_UPDATE_UNDO = "Undo adding %1$s!";

    private Command cCommand;
    private TasksBag cCurBag;    
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;

    public UpdateAction(Command command, TasksBag bag, StorageInterface stor) throws IntegrityCommandException {
        cCommand = command;
        cCurBag = bag.getFiltered();
        cIntBag = bag;
        cStore = stor;

        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IntegrityCommandException(USR_MSG_UPDATE_OOB);
        }

        if (UID > cCurBag.size()) {
            throw new IntegrityCommandException(USR_MSG_UPDATE_OOB);
        }

        // UID - 1 to get array index
        UID -= 1;

        cWhichTask = cCurBag.getTask(UID);
    }

    @Override
    public Feedback execute() throws LogicException {
        Task toBeUpdated = cWhichTask;
        Feedback fb;
        String formattedString;

        switch (cCommand.getTaskField()) {
            case DATE_END:
                assert cCommand.getEnd() != null;
                toBeUpdated.setEnd(cCommand.getEnd());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_ENDDATE_OK, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);

                return fb;
            case DATE_START:
                assert cCommand.getStart() != null;
                toBeUpdated.setStart(cCommand.getStart());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_STARTDATE_OK, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);

                return fb;
            case NAME:
                assert cCommand.getText() != null;
                toBeUpdated.setName(cCommand.getText());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_NAME_OK, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);

                return fb;
            case IMPORTANCE:

                // TODO Parser not done with this
                System.out.println("Not done yet");
                // assert cCommand.get
                // toBeUpdated.setPriority();
                break;
            default:
                assert false : cCommand.getTaskField();
        }
        return null;
    }

    @Override
    public Feedback undo() {
        // TODO Auto-generated method stub
        String formatted = Utilities.formatString(USR_MSG_UPDATE_UNDO, cWhichTask.getName());
        return new Feedback(cCommand, cIntBag, formatted);
    }

    @Override
    public Feedback redo() throws LogicException {
        // TODO Auto-generated method stub
        return execute();
    }

}

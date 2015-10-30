package logic;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.InvalidDateException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class UpdateAction implements UndoableAction {
    private static final String USR_MSG_UPDATE_OOB = "Provided index not on list.";
    private static final String USR_MSG_UPDATE_STARTDATE_OK = "Updated start date of %1$s!";
    private static final String USR_MSG_UPDATE_STARTDATE_INVALID = "Failed to update! Start date is earlier than of end date!";
    private static final String USR_MSG_UPDATE_ENDDATE_OK = "Updated end date of %1$s!";
    private static final String USR_MSG_UPDATE_ENDDATE_INVALID = "Failed to update! End date is earlier than of start date!";
    private static final String USR_MSG_UPDATE_NAME_OK = "Updated name of %1$s!";
    private static final String USR_MSG_UPDATE_UNDO = "Undo update %1$s!";

    private Command cCommand;
    private TasksBag cCurBag;
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;
    private Task cOldTask;
    
    public UpdateAction(Command command, TasksBag bag, StorageInterface stor) throws LogicException {
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

        verifyUpdateData();
        
        cOldTask = cWhichTask.clone();
    }

    private void verifyUpdateData() throws LogicException {
        boolean isValid;
        switch (cCommand.getTaskField()) {
            case DATE_END:
                // Check date if end date is valid to determine if task should
                // be updated.
                assert cCommand.getEnd() != null;

                isValid = Utilities.verifyDate(cWhichTask.getStart(), cCommand.getEnd());
                if (isValid == false) {
                    throw new InvalidDateException(USR_MSG_UPDATE_ENDDATE_INVALID);
                }

                break;
            case DATE_START:
                // Check date if start date is valid to determine if task should
                // be updated.
                assert cCommand.getStart() != null;

                isValid = Utilities.verifyDate(cCommand.getStart(), cWhichTask.getEnd());
                if (isValid == false) {
                    throw new InvalidDateException(USR_MSG_UPDATE_STARTDATE_INVALID);
                }

                break;
            case IMPORTANCE:
                break;
            case NAME:
                assert cCommand.getText() != null;
                break;
            default:
                break;

        }
    }

    @Override
    public Feedback execute() throws LogicException {
        Task toBeUpdated = cWhichTask;
        Feedback fb;
        String formattedString;

        switch (cCommand.getTaskField()) {
            case DATE_END:

                toBeUpdated.setEnd(cCommand.getEnd());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_ENDDATE_OK, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);

                return fb;
            case DATE_START:

                toBeUpdated.setStart(cCommand.getStart());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_STARTDATE_OK, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);
                return fb;

            case NAME:

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
        Task toBeUpdated = cWhichTask;
        Feedback fb;
        String formattedString;

        switch (cCommand.getTaskField()) {
            case DATE_END:

                toBeUpdated.setEnd(cOldTask.getEnd());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_UNDO, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);

                return fb;
            case DATE_START:

                toBeUpdated.setStart(cOldTask.getStart());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_UNDO, toBeUpdated.getName());
                fb = new Feedback(cCommand, cIntBag, formattedString);
                return fb;

            case NAME:

                toBeUpdated.setName(cOldTask.getName());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_UNDO, toBeUpdated.getName());
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
    public Feedback redo() throws LogicException {
        // TODO Auto-generated method stub
        return execute();
    }

}

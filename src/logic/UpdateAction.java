//@@author A0125546E
package logic;

import common.Task;
import common.TasksBag;
import common.Utilities;
import javafx.collections.ObservableList;
import logic.exceptions.IllegalAccessCommandException;
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

    private static final String USR_MSG_UPDATE_CLASH_WARNING_SINGLE = "Task clashes with %1$s!";
    private static final String USR_MSG_UPDATE_CLASH_WARNING_MANY = "Task clashes with %1$s and %2$s more!";
    
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

        // Lower bound
        if (UID <= 0) {
            throw new IllegalAccessCommandException(USR_MSG_UPDATE_OOB);
        }

        // Upper bound
        if (UID > cCurBag.size()) {
            throw new IllegalAccessCommandException(USR_MSG_UPDATE_OOB);
        }

        // UID - 1 to get actual array mapping
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
            	
            	// allow null values to change from event->startonly or deadline->float
                if (cCommand.getEnd() == null) {
                	break;
                }

                isValid = Utilities.verifyDate(cWhichTask.getStart(), cCommand.getEnd());
                if (isValid == false) {
                    throw new InvalidDateException(USR_MSG_UPDATE_ENDDATE_INVALID);
                }

                break;
            case DATE_START:
                // Check date if start date is valid to determine if task should
                // be updated.
            	
            	// allow null values to change from event->deadline or startonly-float
                if (cCommand.getStart() == null) {
                	break;
                }

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
    public CommandFeedback execute() throws LogicException {
        Task toBeUpdated = cWhichTask;
        CommandFeedback fb;
        String formattedString;
        String warningString;
        
        warningString = processWarningMsg();
        
        switch (cCommand.getTaskField()) {
            case DATE_END:

                toBeUpdated.setEnd(cCommand.getEnd());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_ENDDATE_OK, toBeUpdated.getName());
                fb = new CommandFeedback(cCommand, cIntBag, formattedString, warningString);

                return fb;
            case DATE_START:

                toBeUpdated.setStart(cCommand.getStart());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_STARTDATE_OK, toBeUpdated.getName());
                fb = new CommandFeedback(cCommand, cIntBag, formattedString, warningString);
                return fb;

            case NAME:

                toBeUpdated.setName(cCommand.getText());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_NAME_OK, toBeUpdated.getName());
                fb = new CommandFeedback(cCommand, cIntBag, formattedString, warningString);

                return fb;
            default:
                assert false : cCommand.getTaskField();
        }
        return null;

    }

    @Override
    public CommandFeedback undo() {
        Task toBeUpdated = cWhichTask;
        CommandFeedback fb;
        String formattedString;

        switch (cCommand.getTaskField()) {
            case DATE_END:

                toBeUpdated.setEnd(cOldTask.getEnd());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_UNDO, toBeUpdated.getName());
                fb = new CommandFeedback(cCommand, cIntBag, formattedString);

                return fb;
            case DATE_START:

                toBeUpdated.setStart(cOldTask.getStart());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_UNDO, toBeUpdated.getName());
                fb = new CommandFeedback(cCommand, cIntBag, formattedString);
                return fb;

            case NAME:

                toBeUpdated.setName(cOldTask.getName());
                cStore.save(toBeUpdated);

                formattedString = Utilities.formatString(USR_MSG_UPDATE_UNDO, toBeUpdated.getName());
                fb = new CommandFeedback(cCommand, cIntBag, formattedString);

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
    public CommandFeedback redo() throws LogicException {
        // TODO Auto-generated method stub
        return execute();
    }
    
    private String processWarningMsg() {
        String warningString;
        ObservableList<Task> clashList = cIntBag.findClashesWithIncomplete(cWhichTask);
        
        if(clashList == null || clashList.size() == 0){
            return "";
        }
        
        Task firstTask = clashList.get(0);
        if(clashList.size() > 1){
            int noOfOtherClashes = clashList.size() - 1;
            warningString = Utilities.formatString(USR_MSG_UPDATE_CLASH_WARNING_MANY, firstTask.getName(), noOfOtherClashes);
        } else {            
            warningString = Utilities.formatString(USR_MSG_UPDATE_CLASH_WARNING_SINGLE, firstTask.getName());
        }
        return warningString;
    }

}

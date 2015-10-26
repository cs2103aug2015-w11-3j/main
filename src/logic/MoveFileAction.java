package logic;

import common.Configuration;
import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class MoveFileAction implements Action {


    private static final String USR_MSG_MOVE_ERROR = "Failed to move storage file";
    private static final String USR_MSG_MOVE_OK = "Storage file moved!";
    
    private Command cCommand;
    private String cDestination;
    private StorageInterface cStore;
    private TasksBag cIntBag;
    
    public MoveFileAction(Command command, TasksBag internalBag, String destination, StorageInterface stor) {
        cCommand = command;
        cStore = stor;
        cIntBag = internalBag;
        cDestination = destination;
    }

    @Override
    public Feedback execute() throws LogicException {
        assert cCommand.getCmdType() == Command.Type.CHANGE_SAVE_LOC : cCommand.getCmdType();
        Feedback fb;
        Configuration config = Configuration.getInstance();
        
        try {
        	config.setUsrFileDirector(cDestination);
            boolean moveStatus = cStore.moveFileTo(cDestination);
            
            if (moveStatus) {
                fb = new Feedback(cCommand, cIntBag, USR_MSG_MOVE_OK);
            } else {
                throw new LogicException(USR_MSG_MOVE_ERROR);
            }
            
            return fb;
        } catch (Exception e) {
        	throw new LogicException(USR_MSG_MOVE_ERROR);
        }
    }
}

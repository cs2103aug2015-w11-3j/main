package logic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import common.Configuration;
import common.ConfigurationInterface;
import common.TasksBag;
import logic.exceptions.LogicException;
import logic.exceptions.PathNotExistsException;
import parser.Command;
import storage.StorageInterface;

public class MoveFileAction implements Action {

    private static final String USR_MSG_MOVE_ERROR = "Failed to move storage file";
    private static final String USR_MSG_MOVE_OK = "Storage file moved!";
    private static final String USR_MSG_MOVE_INVALID_PATH = "Invalid Path!";
    
    private Command cCommand;
    private StorageInterface cStore;
    private TasksBag cBag;
    private Path cPath;
    
    public MoveFileAction(Command command, TasksBag bag, StorageInterface stor) {
        cCommand = command;
        cStore = stor;
        cBag = bag;
        cPath = command.getPath();
    }

    @Override
    public Feedback execute() throws LogicException {
        assert cCommand.getCmdType() == Command.Type.MOVE : cCommand.getCmdType();
        Feedback fb;
        ConfigurationInterface config = Configuration.getInstance();
    	
        try {
            cStore.moveFileTo(cPath.toString());
            
            config.setUsrFileDirector(cPath.toString());
            
            fb = new Feedback(cCommand, cBag, USR_MSG_MOVE_OK);

            return fb;
        } catch (Exception e) {
        	System.out.println(e);
        	throw new LogicException(USR_MSG_MOVE_ERROR);
        }
    }
}

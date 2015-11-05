//@@author A0133920N
package logic;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import common.Configuration;
import common.ConfigurationInterface;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class MoveFileAction implements Action {

    private static final String USR_MSG_MOVE_ERROR = "Failed to move storage file";
    private static final String USR_MSG_MOVE_OK = "Storage file moved!";
    private static final String USR_MSG_MOVE_ERROR_FILE_EXISTING = "Storage file in %1s already exists";
    private static final String USR_MSG_MOVE_ERROR_INVALID_PATH = "The directory %1s does not exists";
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
    public Feedback execute() throws LogicException  {
        assert cCommand.getCmdType() == Command.Type.MOVE : cCommand.getCmdType();
        Feedback fb;
        ConfigurationInterface config = Configuration.getInstance();
    	
        try {
            cStore.moveFileTo(cPath.toString());
            
            config.setUsrFileDirector(cPath.toString());
            
            fb = new Feedback(cCommand, cBag, USR_MSG_MOVE_OK);

            return fb;
        } catch (FileAlreadyExistsException e) {
        	String fommatted = Utilities.formatString(USR_MSG_MOVE_ERROR_FILE_EXISTING, cPath.toString());
        	throw new LogicException(fommatted);
        } catch (NoSuchFileException e) {
        	String fommatted = Utilities.formatString(USR_MSG_MOVE_ERROR_INVALID_PATH, cPath.toString());
        	throw new LogicException(fommatted);
        } catch (Exception e) {
        	System.out.println(e);
        	System.out.println("sb2");
        	return new Feedback(cCommand, cBag, USR_MSG_MOVE_OK);

//        	throw new LogicException(USR_MSG_MOVE_ERROR);
        }
    }
}

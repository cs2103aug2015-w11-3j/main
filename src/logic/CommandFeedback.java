//@@author A0125546E
package logic;

import common.TasksBag;
import parser.Command;

/*
 * Wrapper class to contain Command type and 
 * CelebiBag for UI
 */
public class CommandFeedback extends Feedback {

    private final Command cCommand;
    private String cMsg;

    public CommandFeedback(Command comd, TasksBag bag) {
        super(bag);
        cCommand = comd;
    }

    public CommandFeedback(Command comd, TasksBag bag, String msg) {
        this(comd, bag);
        cMsg = msg;
    }

    public Command getCommand() {
        return cCommand;
    }

    public String getMsg() {
        return cMsg;
    }

    public void setMsg(String msg) {
        cMsg = msg;
    }

}

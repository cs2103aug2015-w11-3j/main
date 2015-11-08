//@@author A0125546E
package logic;

import common.TasksBag;
import parser.commands.CommandData;

/*
 * Wrapper class to contain Command type and 
 * CelebiBag for UI
 */
public class CommandFeedback extends Feedback {

    private final CommandData cCommand;
    private String cMsg;
    private String cWarning;

    public CommandFeedback(CommandData comd, TasksBag bag) {
        super(bag);
        cCommand = comd;
    }

    public CommandFeedback(CommandData comd, TasksBag bag, String msg) {
        this(comd, bag);
        cMsg = msg;
    }

    public CommandFeedback(CommandData comd, TasksBag bag, String msg, String warning) {
        this(comd, bag);
        cMsg = msg;
        cWarning = warning;
    }

    public CommandData getCommand() {
        return cCommand;
    }

    public String getMsg() {
        return cMsg;
    }

    public String getWarningMsg() {
        return cWarning;
    }

    public void setMsg(String msg) {
        cMsg = msg;
    }

}

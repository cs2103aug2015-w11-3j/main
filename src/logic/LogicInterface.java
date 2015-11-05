//@@author A0125546E
package logic;


import common.TasksBag;
import javafx.scene.input.KeyCode;
import logic.exceptions.LogicException;

public interface LogicInterface {

    public void init();

    public boolean initData(String s); // Returns true if successfully init data

    public CommandFeedback executeCommand(String cmd) throws LogicException;
    public KeyEventFeedback executeKeyEvent(KeyCode whichKey) throws LogicException;
    public TasksBag getTaskBag();

    public TasksBag getDefaultBag();

    public void close();
}

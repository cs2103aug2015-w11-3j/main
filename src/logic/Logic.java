//@@author A0125546E
package logic;

import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import common.TasksBag;
import common.TasksBag.FilterBy;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import logic.exceptions.UnknownCommandException;
import parser.Command;
import parser.Command.Type;
import parser.Parser;
import parser.ParserInterface;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

    private static final String USR_MSG_UNKNOWN_COMMAND = "I couldn't understand you... (>.<)";

    // The default view when UI first query the bag
    private static final FilterBy DEFAULT_UI_VIEW = TasksBag.FilterBy.TODAY;
    private static final KeyCode TOGGLE_FILTER_STATE_KEY = KeyCode.TAB;
    private StorageInterface cStorage;
    private ParserInterface cParser;
    private TasksBag cInternalBag;
    private ActionInvoker cInvoker;
    private Logger log;

    public Logic() {
        cInternalBag = new TasksBag();
        cInvoker = new ActionInvoker();
        log = Logger.getLogger("Filter");
    }

    @Override
    public void init() {

        cStorage = Storage.getStorage();
        cStorage.init();
        cParser = Parser.getParser();
        cParser.init();

        System.out.println("Logic Init complete");
    }

    /**
     * Passes string param to parser then evaluates the command type
     * 
     * @exception IntegrityCommandException
     *                When given input violates validity
     * @exception InvalidCommandException
     *                When given input cannot be understood
     * @param userString
     *            string value entered by user
     */
    @Override
    public CommandFeedback executeCommand(String userString) throws LogicException {
        assert userString != null;

        Command rtnCmd = cParser.parseCommand(userString);
        log.info("executing " + userString);
        return executeParsed(rtnCmd);
    }

    private CommandFeedback executeParsed(Command rtnCmd) throws LogicException {

        Feedback fb;
        switch (rtnCmd.getCmdType()) {
            case ADD:
                fb = cInvoker.placeAction(new AddAction(rtnCmd, cInternalBag, cStorage));
                break;
            case DELETE:
                fb = cInvoker.placeAction(new DeleteAction(rtnCmd, cInternalBag, cStorage));
                break;
            case SHOW_COMPLETE:
                fb = cInvoker.placeAction(new FilterAction(rtnCmd, cInternalBag, TasksBag.FilterBy.COMPLETE_TASKS));
                break;
            case UPDATE:
                fb = cInvoker.placeAction(new UpdateAction(rtnCmd, cInternalBag, cStorage));
                break;
            case SHOW_INCOMPLETE:
                fb = cInvoker.placeAction(new FilterAction(rtnCmd, cInternalBag, TasksBag.FilterBy.INCOMPLETE_TASKS));
                break;
            case MARK:
                fb = cInvoker.placeAction(new MarkAction(rtnCmd, cInternalBag, cStorage));
                break;
            case UNMARK:
                fb = cInvoker.placeAction(new UnmarkAction(rtnCmd, cInternalBag, cStorage));
                break;
            case UNDO:
                fb = cInvoker.undoAction();
                break;
            case REDO:
                fb = cInvoker.redoAction();
                break;
            case FILTER_DATE:
                fb = cInvoker.placeAction(new FilterDateAction(rtnCmd, cInternalBag));
                break;
            case MOVE:
                fb = cInvoker.placeAction(new MoveFileAction(rtnCmd, cInternalBag, cStorage));
                break;
            case QUIT:
                fb = new CommandFeedback(rtnCmd, null);
                break;
            case SEARCH:
                fb = cInvoker.placeAction(new SearchAction(rtnCmd, cInternalBag));
                break;
            case SHOW_DEFAULT:
                fb = cInvoker.placeAction(new FilterAction(rtnCmd, cInternalBag, DEFAULT_UI_VIEW));
                break;
            case HELP:
                fb = cInvoker.placeAction(new HelpAction(rtnCmd, cInternalBag));
                break;
            case INVALID:
                throw new UnknownCommandException(USR_MSG_UNKNOWN_COMMAND);
            default:
                assert false : rtnCmd.getCmdType();
                fb = new CommandFeedback(rtnCmd, cInternalBag);
                break;
        }
        return (CommandFeedback)fb;
    }

    @Override
    public boolean initData(String s) {

        boolean rtnVal = cStorage.load(s, cInternalBag);
        return rtnVal;
    }

    @Override
    public TasksBag getTaskBag() {
        return cInternalBag;
    }

    public void setStorage(StorageInterface storageStub) {
        System.out.println("Stub added for storage");
        cStorage = storageStub;
    }

    public void setParser(ParserInterface parserStub) {
        System.out.println("STUB ADDED FOR PARSER");
        cParser = parserStub;
    }

    @Override
    public TasksBag getDefaultBag() {
        cInternalBag.setFilterState(DEFAULT_UI_VIEW);
        return cInternalBag.getFiltered();
    }

    public void close() {
        cStorage.close();
    }

    @Override
    public KeyEventFeedback executeKeyEvent(KeyCode whichKey) throws LogicException {
        KeyEventFeedback fb = null;
        if(whichKey == TOGGLE_FILTER_STATE_KEY){
            fb = (KeyEventFeedback)cInvoker.placeAction(new FilterToggle(cInternalBag, whichKey));
        }
        return fb;
    }
}

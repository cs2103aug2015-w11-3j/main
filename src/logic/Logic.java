package logic;

import java.util.logging.Logger;

import common.Task;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import logic.exceptions.UnknownCommandException;
import common.TasksBag;
import common.TasksBag.FilterBy;
import parser.Command;
import parser.Parser;
import parser.ParserInterface;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

    // The default view when UI first query the bag
    private static final FilterBy DEFAULT_UI_VIEW = TasksBag.FilterBy.INCOMPLETE_TASKS;

    private StorageInterface cStorage;
    private ParserInterface cParser;
    private TasksBag cInternalBag;
    private ActionInvoker cInvoker;
    private Logger log;

    public Logic() {
        cInternalBag = new TasksBag();
        cInvoker = new ActionInvoker();
        log = Logger.getLogger("Logic");
    }

    @Override
    public void init() {

        cStorage = new Storage();
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
    public Feedback executeCommand(String userString) throws LogicException {
        Command rtnCmd = cParser.parseCommand(userString);

        log.info("executing " + userString);
        return executeParsed(rtnCmd);
    }

    private Feedback executeParsed(Command rtnCmd) throws LogicException {

        Feedback fb;
        switch (rtnCmd.getCmdType()) {
            case ADD:
                fb = cInvoker.placeAction(new AddAction(rtnCmd, cInternalBag, cStorage));
                break;
            case DELETE:
                fb = cInvoker.placeAction(new DeleteAction(rtnCmd, cInternalBag, cStorage));
                break;
            case SHOW_COMPLETE:
                fb = cInvoker.placeAction(new SortAction(rtnCmd, cInternalBag, TasksBag.FilterBy.COMPLETE_TASKS));
                break;
            case UPDATE:
                // doUpdate(rtnCmd);
                fb = cInvoker.placeAction(new UpdateAction(rtnCmd, cInternalBag, cStorage));
                        //new Feedback(rtnCmd, cInternalBag);
                break;
            case SHOW_INCOMPLETE:
                fb = cInvoker.placeAction(new SortAction(rtnCmd, cInternalBag, DEFAULT_UI_VIEW));
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
                // TODO Filter date implementation
                //fb = cInvoker.placeAction(new FilterDateAction(rtnCmd, cInternalBag, cStorage));
                fb = new Feedback(rtnCmd, null, "Not implemented yet.");
                break;
            case SEARCH:
                // TODO Search implementation
                fb = new Feedback(rtnCmd, null, "Not implemented yet.");
                break;
            case MOVE_FILE:
                // TODO Check implementation of user specified location. No idea which function to call yet
                fb = cInvoker.placeAction(new MoveFileAction(rtnCmd, cInternalBag, null, cStorage));
            case QUIT:
                log.info("recevied quit");
                fb = new Feedback(rtnCmd, null);
                break;
            case SEARCH:
            	fb = cInvoker.placeAction(new SearchAction(rtnCmd, cInternalBag));
                break;
            case INVALID:
                log.info("recevied invalid type");
                throw new UnknownCommandException("I couldn't understand you... (>.<)");

            default:
                assert false : rtnCmd.getCmdType();
                fb = new Feedback(rtnCmd, cInternalBag);
                break;
        }
        return fb;
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
        cInternalBag.setSortState(DEFAULT_UI_VIEW);
        return cInternalBag.getFiltered();
    }

}

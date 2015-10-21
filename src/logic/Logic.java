package logic;

import java.util.logging.Logger;

import common.Task;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import logic.exceptions.NoRedoActionException;
import logic.exceptions.NoUndoActionException;
import logic.exceptions.UnknownCommandException;
import common.TasksBag;
import parser.Command;
import parser.Parser;
import parser.ParserInterface;
import storage.Storage;
import storage.StorageInterface;

public class Logic implements LogicInterface {

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

//        if (userString.equals("undo")) {
//            rtnCmd = cParser.makeType(Command.Type.UNDO);
//        }
//        if (userString.equals("redo")) {
//            rtnCmd = cParser.makeType(Command.Type.REDO);
//        }
//        if (userString.equals("sort")) {
//            rtnCmd = cParser.makeSort();
//        }
//        if (userString.equals("mark")) {
//            rtnCmd = cParser.makeType(Command.Type.MARK);
//        }
//        if (userString.equals("unmark")) {
//            rtnCmd = cParser.makeType(Command.Type.UNMARK);
//        }
//        if (userString.equals("show uc")) {
//            rtnCmd = cParser.makeType(Command.Type.SHOW_INCOMPLETE);
//        }
//        if (userString.equals("show c")) {
//            rtnCmd = cParser.makeType(Command.Type.SHOW_COMPLETE);
//        }

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
                fb = cInvoker.placeAction(new SortAction(rtnCmd, cInternalBag, TasksBag.FliterBy.COMPLETE_TASKS));
                break;
            case UPDATE:
                // Not command pattern yet
                doUpdate(rtnCmd);
                fb = new Feedback(rtnCmd, cInternalBag);
                break;
            case SHOW_INCOMPLETE:
                fb = cInvoker.placeAction(new SortAction(rtnCmd, cInternalBag, TasksBag.FliterBy.INCOMPLETE_TASKS));
                break;
            case MARK:
                fb = cInvoker.placeAction(new MarkAction(rtnCmd, cInternalBag, cStorage));
                break;
            case UNMARK:
                // Not yet command action
                if (cInternalBag.getFlitered().isEmpty()) {
                    throw new IntegrityCommandException("Provided index not on list.");
                } else {
                    Task t2 = cInternalBag.getFlitered().getTask(0);
                    t2.setComplete(false);
                }
                fb = new Feedback(rtnCmd, cInternalBag);
                break;
            case UNDO:
                cInvoker.undoAction();
                fb = new Feedback(rtnCmd, cInternalBag);
                break;
            case REDO:
                cInvoker.redoAction();
                fb = new Feedback(rtnCmd, cInternalBag);
                break;
            case QUIT:
                log.info("recevied quit");
                fb = new Feedback(rtnCmd, null);
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

    private void doUpdate(Command rtnCmd) throws IntegrityCommandException {

        // verify UID
        int UID = rtnCmd.getTaskUID();
        if (UID < 0 || UID >= cInternalBag.size()) {
            throw new IntegrityCommandException("Given index out of bound");
        } else {
            Task toBeUpdated = cInternalBag.getTask(UID);
            assert toBeUpdated != null;

            switch (rtnCmd.getTaskField()) {
                case BLOCKED_PERIODS:
                    System.out.println("Not supported yet");
                    break;
                case DATE_END:
                    assert rtnCmd.getEnd() != null;
                    toBeUpdated.setEnd(rtnCmd.getEnd());
                    cStorage.save(toBeUpdated);
                    break;
                case DATE_START:
                    assert rtnCmd.getStart() != null;
                    toBeUpdated.setStart(rtnCmd.getStart());
                    cStorage.save(toBeUpdated);
                    break;
                case DESCRIPTION:
                    System.out.println("Not supporting");
                    break;
                case ID:
                    System.out.println("Not supporting"); // Should never be
                                                          // ran?
                    break;
                case IS_COMPLETED:
                    System.out.println("Not use mark/unmark?");
                    break;
                case NAME:
                    assert rtnCmd.getName() != null;
                    toBeUpdated.setName(rtnCmd.getName());
                    cStorage.save(toBeUpdated);
                    break;
                case PRIORITY:
                    System.out.println("Not supported yet");
                    break;
                case SCHEDULED_DAYS:
                    System.out.println("Not supporting");
                    break;
                case TAGS:
                    System.out.println("Not supporting");
                    break;
                default:
                    assert false : rtnCmd.getTaskField();
                    System.out.println("Invalid field type"); // Should be
                                                              // marked as
                                                              // invalid
                                                              // command
                                                              // in parser
                    break;

            }
        }
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

    public void setStorage(StorageInterface storageStub){
        System.out.println("Stub added for storage");
        cStorage = storageStub;
    }
    public void setParser(ParserInterface parserStub) {
        System.out.println("STUB ADDED FOR PARSER");
        cParser = parserStub;
    }

}

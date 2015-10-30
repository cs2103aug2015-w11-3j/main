package logic;

import common.TasksBag;
import common.TasksBag.FilterBy;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;

/**
 * @author MonoChrome Changes the state of the internal taskbags to given sort
 *         state Undo sets the internal bag state to previous state
 */
public class SortAction implements Action {
    private static final String USR_MSG_SORT_COMPLETE = "Switching view to completed tasks";
    private static final String USR_MSG_SORT_INCOMPLETE = "Switching view to incompleted tasks";
    private static final String USR_MSG_SORT_TODAY = "Switching view to today tasks";

    private Command cCommand;
    private TasksBag cBag;
    private FilterBy cSortBy;
    // private FliterBy cPrevSortBy;

    public SortAction(Command command, TasksBag internalBag, FilterBy sortBy) throws LogicException {
        cCommand = command;
        cBag = internalBag;
        cSortBy = sortBy;
    }

    @Override
    public Feedback execute() throws LogicException {
        String msg = "";
        switch(cSortBy){
            case COMPLETE_TASKS:
                msg = USR_MSG_SORT_COMPLETE;
                break;
            case INCOMPLETE_TASKS:
                msg = USR_MSG_SORT_INCOMPLETE;
                break;
            case NONE:
                assert false;
                break;
            case TODAY:
                msg = USR_MSG_SORT_TODAY;
                break;
            default:
                assert false;
                break;
            
        }
        cBag.setSortState(cSortBy);
        
        // both search string and filter date will be reset
        cBag.setSearchState(null);
        cBag.setFilterDateState(null, null);
        return new Feedback(cCommand, cBag, msg);
    }

    /*
     * @Override public void undo() { cBag.setSortState(cPrevSortBy); }
     * 
     * @Override public void redo() throws LogicException { execute(); }
     */

}

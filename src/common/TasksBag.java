//@@author A0125546E
package common;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import common.Task.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Storage for Tasks Provides adding, deleting and sorting
 */
public class TasksBag implements Iterable<Task> {

    public static enum ViewType {
        COMPLETED, INCOMPLETE, DEFAULT
    }

    public static enum FilterDateState {
        NONE, AFTER, BEFORE, BETWEEN
    }

    private static final int FLOAT_LIMIT = 2;
    private static final int TASKS_LIMIT = 7;
    private static final int DEFAULT_DAY_RANGE = 3;

    private ViewType cViewType = null;
    private String cSearchState = null;
    private ObservableList<Task> tasks;

    private Date cFilterDateStart;
    private Date cFilterDateEnd;
    private FilterDateState cDateState = null;
    private Logger log;

    public TasksBag() {
        tasks = FXCollections.observableArrayList();
        log = Logger.getLogger("TasksBag");
        cViewType = ViewType.DEFAULT;
        cDateState = FilterDateState.NONE;
    }

    public FilterDateState getDateState() {
        return cDateState;
    }

    public TasksBag(ObservableList<Task> t) {
        tasks = t;
    }

    public Task getTask(int index) {
        assert index < tasks.size() : index;
        return tasks.get(index);
    }

    public Task addTask(Task c) {
        assert c != null : c;

        tasks.add(c);
        return c;
    }

    public void addTask(int index, Task c) {
        assert c != null : c;
        assert index >= 0 : index;

        tasks.add(index, c);
    }

    public int size() {
        return tasks.size();
    }

    public ObservableList<Task> getList() {
        return tasks;
    }

    public void setView(ViewType attribute) {
        assert attribute != null;
        cViewType = attribute;
    }

    public void setSearchState(String keyword) {
        cSearchState = keyword;
    }

    /**
     * Sort will return a new container as specified by current view state
     * Then sort by chronological date order
     */
    public TasksBag getFilteredView() {

        ObservableList<Task> newContainer = null;

        switch (cViewType) {
            case COMPLETED:
                newContainer = getTasksComplete();
                break;
            case INCOMPLETE:
                newContainer = getTasksIncomplete();
                break;
            case DEFAULT:
                newContainer = getTasksToday();
                break;
            default:
                assert false;
                break;
        }

        sortDateChronological(newContainer);
        // Transfer the current state to the new bag
        // UI uses the sort state to identify current tab
        TasksBag rtnBag = copyBagState(newContainer);
        return rtnBag;
    }

    /**
     * Transfers the current state of the bag into the new bag
     * 
     * @param whichBag
     *            to copy state onto
     * @return whichBag that has the same state as this bag
     */
    private TasksBag copyBagState(ObservableList<Task> whichBag) {
        TasksBag rtnBag = new TasksBag(whichBag);
        rtnBag.setView(cViewType);
        rtnBag.setSearchState(cSearchState);
        rtnBag.setFilterDateState(cFilterDateStart, cFilterDateEnd);
        return rtnBag;
    }

    private void sortDateChronological(ObservableList<Task> container) {
        Collections.sort(container, (Task t1, Task t2) -> compareDate(t1, t2));
    }

    private ObservableList<Task> getTasksComplete() {
        ObservableList<Task> rtnList = FXCollections.observableArrayList();
        /* @formatter:off */
        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() && curTask.hasKeyword(cSearchState) 
                    && checkDateIfWithinFilter(curTask)) {
                rtnList.add(curTask);
            }
        }
        /* @formatter:on */
        return rtnList;
    }

    private ObservableList<Task> getTasksIncomplete() {
        ObservableList<Task> rtnList = FXCollections.observableArrayList();
        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() == false && curTask.hasKeyword(cSearchState)
                    && checkDateIfWithinFilter(curTask)) {
                rtnList.add(curTask);
            }
        }
        return rtnList;
    }

    private ObservableList<Task> getTasksToday() {
        ObservableList<Task> rtnList = FXCollections.observableArrayList();
        // count # of floating
        ObservableList<Task> taskFloat = getIncompleteFloatingTasks();
        // ObservableList<Task> taskFloat2 =

        ObservableList<Task> taskNonFloat = getIncompleteDatedTasksWithDayLimit(DEFAULT_DAY_RANGE);

        int totalCount = taskFloat.size() + taskNonFloat.size();
        System.out.println(taskFloat.size());
        if (totalCount <= TASKS_LIMIT) {
            // take all
            rtnList.addAll(taskNonFloat);
            rtnList.addAll(taskFloat);
        } else {

            if (taskFloat.size() <= FLOAT_LIMIT) {
                // float count is smaller
                // fill with float then the rest with nonfloat
                rtnList.addAll(taskFloat);
                trimList(taskNonFloat, TASKS_LIMIT - rtnList.size());
                rtnList.addAll(taskNonFloat);
            } else {
                // non float count is smaller
                // fill with non float then the rest with floats
                trimList(taskNonFloat, TASKS_LIMIT - FLOAT_LIMIT);
                rtnList.addAll(taskNonFloat);

                randomizeList(taskFloat);
                trimList(taskFloat, TASKS_LIMIT - rtnList.size());
                rtnList.addAll(taskFloat);
            }
        }
        log.info("Float: " + taskFloat.size() + " Dated: " + taskNonFloat.size());
        return rtnList;
    }

    private void randomizeList(ObservableList<Task> list) {
        Collections.shuffle(list);
    }

    /**
     * Reduce the size of the list from the end of the list
     * 
     * @param taskNonFloat
     * @param i
     */
    private void trimList(ObservableList<Task> taskList, int size) {
        taskList.remove(size, taskList.size());
    }

    /**
     * Counts the number of tasks which are incomplete and has at least noOfDays
     * date
     * 
     * @return
     */
    private ObservableList<Task> getIncompleteDatedTasksWithDayLimit(int noOfDays) {
        /* @formatter:off */
        ObservableList<Task> taskList = new TaskBagBuilder(this)
                .hasDate()
                .isNotComplete()
                .hasSearchKeyword(getSearchState())
                .isWithinDayLimit(DEFAULT_DAY_RANGE)
                .build();
        /* @formatter:on */
        return taskList;
    }

    private ObservableList<Task> getInCompleteDateTasksAll() {
        /* @formatter:off */
        ObservableList<Task> taskList = new TaskBagBuilder(this)
                .isNotComplete()
                .hasDate()
                .build();
        /* @formatter:on */
        return taskList;
    }

    /**
     * Counts the number of tasks which are incomplete and has at least 1 date
     * 
     * @return
     */
    private ObservableList<Task> getIncompleteFloatingTasks() {
        /* @formatter:off */
        ObservableList<Task> taskList = new TaskBagBuilder(this)
                .noDate()
                .isNotComplete()
                .hasSearchKeyword(getSearchState())
                .build();

        /* @formatter:on */
        return taskList;
    }

    private boolean checkDateIfWithinFilter(Task curTask) {
        if (cFilterDateEnd == null || cFilterDateStart == null) {
            return true;
        } else {
            return curTask.isWithinDate(cFilterDateStart, cFilterDateEnd);
        }
    }

    public Task removeTask(int index) {
        assert index >= 0 : index;
        assert index <= tasks.size() - 1 : index;

        Task rtnCelebi = tasks.remove(index);
        return rtnCelebi;
    }

    public int removeTask(Task t) {
        assert t != null : "Null task";

        int rtnIndex = tasks.indexOf(t);
        assert rtnIndex >= 0 : rtnIndex;

        tasks.remove(rtnIndex);

        return rtnIndex;
    }

    private int compareDate(Task t1, Task t2) {
        assert t1 != null;
        assert t2 != null;

        Date firstCom, secCom;

        firstCom = getAnyDate(t1);
        secCom = getAnyDate(t2);

        if (firstCom == null && secCom == null) {
            return 0;
        } else if (secCom == null) {
            return -1;
        } else if (firstCom == null) {
            return 1;
        } else {
            return firstCom.compareTo(secCom);
        }
    }

    private Date getAnyDate(Task t1) {
        Date date = null;
        if (t1.getStart() != null) {
            date = t1.getStart();
        } else if (t1.getEnd() != null) {
            date = t1.getEnd();
        }
        return date;
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    public ViewType getView() {
        return cViewType;
    }

    public boolean isEmpty() {
        return tasks.size() == 0;
    }

    public void setFilterDateState(Date start, Date end) {
        cFilterDateStart = start;
        cFilterDateEnd = end;
        updateFilterDateState();
    }

    public void updateFilterDateState() {
        if (cFilterDateStart == null && cFilterDateEnd == null) {
            cDateState = FilterDateState.NONE;
        } else if (cFilterDateStart.equals(Utilities.absBeginningTime())) {
            cDateState = FilterDateState.BEFORE;
        } else if (cFilterDateEnd.equals(Utilities.absEndingTime())) {
            cDateState = FilterDateState.AFTER;
        } else {
            cDateState = FilterDateState.BETWEEN;
        }
    }

    public Date getStartDate() {
        return cFilterDateStart;
    }

    public Date getEndDate() {
        return cFilterDateEnd;
    }

    public String getSearchState() {
        return cSearchState;
    }

    public void toggleView() {
        switch (cViewType) {
            case COMPLETED:
                cViewType = ViewType.INCOMPLETE;
                break;
            case INCOMPLETE:
                cViewType = ViewType.DEFAULT;
                break;
            case DEFAULT:
                cViewType = ViewType.COMPLETED;
                break;
            default:
                log.severe("Default view state encountered during toggleFilter.");
                cViewType = ViewType.DEFAULT;
                break;
        }
    }

    public ObservableList<Task> findClashesWithIncomplete(Task t) {
        ObservableList<Task> clashList = FXCollections.observableArrayList();
        ObservableList<Task> lists = getInCompleteDateTasksAll();

        if (t.getType() != Type.EVENT) {
            return clashList;
        }

        lists.forEach(curTask -> {
            if (t.clashesWith(curTask)) {
                clashList.add(curTask);
            }
        });
        return clashList;
    }
}
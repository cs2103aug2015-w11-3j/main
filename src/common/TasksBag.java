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

    private static final int FLOAT_LIMIT = 3;
    private static final int TASKS_LIMIT = 15;
    private static final int DEFAULT_DAY_RANGE = 3;

    private ViewType cViewType = null; // setting to default
    private String cSearchState = null;
    private ObservableList<Task> tasks;
    private Date cFilterDateStart;
    private Date cFilterDateEnd;
    private FilterDateState cDateState = FilterDateState.NONE;
    private Logger log;

    public TasksBag() {
        tasks = FXCollections.observableArrayList();
        log = Logger.getLogger("TasksBag");
        cViewType = ViewType.INCOMPLETE; // setting to default
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
     * Sort will return a new container as specified by current sorted state
     * Then sort by rev date
     */
    public TasksBag getFiltered() {
        // assert attribute != null;

        ObservableList<Task> newContainer = FXCollections.observableArrayList();

        switch (cViewType) {
            case COMPLETED:
                filterTasksComplete(newContainer);
                break;
            case INCOMPLETE:
                filterTasksIncomplete(newContainer);
                break;
            case DEFAULT:
                filterTasksToday(newContainer);
                break;
            default:
                assert false;
                break;
        }

        // Sorting by chronological date before returning
        sortDateChronological(newContainer);
        // Transfer the current state to the new bag
        // UI uses the sort state to identify current tab
        TasksBag rtnBag = new TasksBag(newContainer);
        rtnBag.setView(cViewType);
        rtnBag.setSearchState(cSearchState);
        rtnBag.setFilterDateState(cFilterDateStart, cFilterDateEnd);
        return rtnBag;
    }

    private void sortDateChronological(ObservableList<Task> container) {
        Collections.sort(container, (Task t1, Task t2) -> compareDate(t1, t2));
    }

    private void filterTasksComplete(ObservableList<Task> container) {
        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() && curTask.hasKeyword(cSearchState) && checkDateIfWithinFilter(curTask)) {
                container.add(curTask);
            }
        }
    }

    private void filterTasksIncomplete(ObservableList<Task> container) {
        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() == false && curTask.hasKeyword(cSearchState) && checkDateIfWithinFilter(curTask)) {
                container.add(curTask);
            }
        }
    }

    private void filterTasksToday(ObservableList<Task> container) {
        // count # of floating
        ObservableList<Task> taskFloat = getIncompleteFloatingTasks();
        ObservableList<Task> taskFloat2 = new TaskBagBuilder(this).noDate().isNotComplete()
                .hasSearchKeyword(getSearchState()).build();
        // if (curTask.isComplete() == false && curTask.hasDate() == false &&
        // curTask.hasKeyword(cSearchState)) {

        System.out.println("Builder diff: " + taskFloat.size() + " " + taskFloat2.size());
        // count # of dateline/event
        ObservableList<Task> taskNonFloat = getIncompleteDatedTasksWithDayLimit(DEFAULT_DAY_RANGE);
        ObservableList<Task> taskNonFloat2 = new TaskBagBuilder(this).hasDate().isNotComplete()
                .hasSearchKeyword(getSearchState()).isWithinDayLimit(DEFAULT_DAY_RANGE).build();
        
        System.out.println("Builder diff: " + taskNonFloat.size() + " " + taskNonFloat2.size());

        int totalCount = taskFloat.size() + taskNonFloat.size();
        System.out.println(taskFloat.size());
        if (totalCount <= TASKS_LIMIT) {
            // take all
            container.addAll(taskNonFloat);
            container.addAll(taskFloat);
        } else {

            if (taskFloat.size() <= FLOAT_LIMIT) {// float count is
                                                  // smaller
                // fill with float then the rest with nonfloat
                container.addAll(taskFloat);
                trimList(taskNonFloat, TASKS_LIMIT - container.size());
                container.addAll(taskNonFloat);
            } else {// non float count is smaller
                // fill with non float then the rest with floats
                trimList(taskNonFloat, TASKS_LIMIT - FLOAT_LIMIT);
                container.addAll(taskNonFloat);

                randomizeList(taskFloat);
                trimList(taskFloat, TASKS_LIMIT - container.size());
                container.addAll(taskFloat);

            }
        }
        log.info("Float: " + taskFloat.size() + " Dated: " + taskNonFloat.size());
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
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() == false && curTask.hasDate() && curTask.isWithinDays(noOfDays)
                    && curTask.hasKeyword(cSearchState)) {
                taskList.add(curTask);
            }
        }
        return taskList;
    }

    private ObservableList<Task> getInCompleteDateTasksAll() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() == false && curTask.hasDate()) {
                taskList.add(curTask);
            }
        }
        return taskList;
    }

    /**
     * Counts the number of tasks which are incomplete and has at least 1 date
     * 
     * @return
     */
    private ObservableList<Task> getIncompleteFloatingTasks() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isCompleted() == false && curTask.hasDate() == false && curTask.hasKeyword(cSearchState)) {
                taskList.add(curTask);
            }
        }
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
                log.severe("Default filter state encountered during toggleFilter.");
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

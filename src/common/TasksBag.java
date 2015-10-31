package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import common.TasksBag.FilterBy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Storage for Tasks Provides adding, deleting and sorting
 */
public class TasksBag implements Iterable<Task> {

    public static enum FilterBy {
        COMPLETE_TASKS, INCOMPLETE_TASKS, NONE, TODAY
    }

    public static enum FilterDateState {
        NONE, AFTER, BEFORE, BETWEEN
    }

    private static final int FLOAT_LIMIT = 3;
    private static final int TASKS_LIMIT = 15;

    private FilterBy cFliterState = FilterBy.INCOMPLETE_TASKS;
    private String cSearchState = null;
    private ObservableList<Task> tasks;
    private Date cFilterDateStart;
    private Date cFilterDateEnd;
    private FilterDateState cDateState = FilterDateState.NONE;

    public FilterDateState getDateState() {
        return cDateState;
    }

    public TasksBag() {
        tasks = FXCollections.observableArrayList();
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

    public void setSortState(FilterBy attribute) {
        assert attribute != null;
        cFilterState = attribute;
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

        ObservableList<Task> newContainer = null;
        
        switch (cFilterState) {
            /*
             * Not support date filtering case DATE: // Reverse sorting with
             * earliest on top newContainer = TasksBag.copy(tasks);
             * Collections.sort(newContainer, (Task t1, Task t2) ->
             * compareDate(t2, t1)); break;
             */
            case NONE:
                newContainer = FXCollections.observableArrayList();
                for (int i = 0; i < tasks.size(); i++) {
                    Task curTask = tasks.get(i);
                    if (curTask.hasKeyword(cSearchState) && checkDate(curTask)) {
                        newContainer.add(curTask);
                    }
                }
                break;
            case COMPLETE_TASKS:
                newContainer = FXCollections.observableArrayList();
                for (int i = 0; i < tasks.size(); i++) {
                    Task curTask = tasks.get(i);
                    if (curTask.isComplete() && curTask.hasKeyword(cSearchState) && checkDate(curTask)) {
                        newContainer.add(curTask);
                    }
                }
                break;
            case INCOMPLETE_TASKS:
                newContainer = FXCollections.observableArrayList();
                for (int i = 0; i < tasks.size(); i++) {
                    Task curTask = tasks.get(i);
                    if (curTask.isComplete() == false && curTask.hasKeyword(cSearchState) && checkDate(curTask)) {
                        newContainer.add(curTask);
                    }
                }
                break;
            case TODAY:
                newContainer = FXCollections.observableArrayList();
                // count # of floating
                ObservableList<Task> taskFloat = getIncompleteFloatingTasks();
                // count # of dateline/event
                ObservableList<Task> taskNonFloat = getIncompleteDatedTasks();

                int totalCount = taskFloat.size() + taskNonFloat.size();
                if (totalCount <= TASKS_LIMIT) {
                    // take all
                    newContainer.addAll(taskNonFloat);
                    newContainer.addAll(taskFloat);
                } else {

                    if (taskFloat.size() <= FLOAT_LIMIT) {// float count is
                                                          // smaller
                        // fill with float then the rest with nonfloat
                        newContainer.addAll(taskFloat);
                        trimList(taskNonFloat, TASKS_LIMIT - newContainer.size());
                        newContainer.addAll(taskNonFloat);
                    } else {// non float count is smallerF
                        // fill with non float then the rest with floats
                        randomizeList(taskFloat);
                        trimList(taskFloat, FLOAT_LIMIT);
                        newContainer.addAll(taskFloat);
                        trimList(taskNonFloat, TASKS_LIMIT - FLOAT_LIMIT);
                        newContainer.addAll(taskNonFloat);
                    }
                }
                System.out.println("Float: " + taskFloat.size() + " Dated: " + taskNonFloat.size());
                break;
            default:
                assert false;
                break;
        }

        // Sorting by date before returning
        Collections.sort(newContainer, (Task t1, Task t2) -> compareDate(t1, t2));

        // Transfer the current state to the new bag
        // UI uses the sort state to identify current tab
        TasksBag rtnBag = new TasksBag(newContainer);
        rtnBag.setSortState(cFilterState);
        return rtnBag;
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
        // TODO Auto-generated method stub
        // taskList.size();
        taskList.remove(size, taskList.size());
    }

    /**
     * Counts the number of tasks which are incomplete and has at least 1 date
     * 
     * @return
     */
    private ObservableList<Task> getIncompleteDatedTasks() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        for (int i = 0; i < tasks.size(); i++) {
            Task curTask = tasks.get(i);
            if (curTask.isComplete() == false && curTask.hasDate() && curTask.isToday()) {
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
            if (curTask.isComplete() == false && curTask.hasDate() == false) {
                taskList.add(curTask);
            }
        }
        return taskList;
    }

    private boolean checkDate(Task curTask) {
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

    public FilterBy getState() {
        return cFilterState;
    }

    public boolean isEmpty() {
        return tasks.size() == 0;
    }

    public void filterDate(Date start, Date end) {

    }

    public void setFilterDateState(Date start, Date end) {
        cFilterDateStart = start;
        cFilterDateEnd = end;
    }
}

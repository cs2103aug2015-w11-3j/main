package common;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import common.TasksBag.SortBy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Storage for Tasks Provides adding, deleting and sorting
 */
public class TasksBag implements Iterable<Task> {

    public enum SortBy {
        DATE, MARK, UNMARK, NONE
    }

    private SortBy cSortState = SortBy.NONE;
    private ObservableList<Task> tasks;

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

    public void setSortState(SortBy attribute) {
        assert attribute != null;
        cSortState = attribute;
    }

    /**
     * Sort will return a new container as specified by current sorted state
     */
    public TasksBag getSorted() {
        // assert attribute != null;

        ObservableList<Task> newContainer = null;

        switch (cSortState) {
            case DATE:
                // Reverse sorting with earliest on top
                newContainer = TasksBag.copy(tasks);
                Collections.sort(newContainer, (Task t1, Task t2) -> compareDate(t2, t1));
                break;
            case NONE:
                // No sorting required
                newContainer = TasksBag.copy(tasks);
                break;
            case MARK:
                newContainer = FXCollections.observableArrayList();
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).isComplete()) {
                        newContainer.add(tasks.get(i));
                    }
                }
                break;
            case UNMARK:
                newContainer = FXCollections.observableArrayList();
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).isComplete() == false) {
                        newContainer.add(tasks.get(i));
                    }
                }
                break;
            default:
                assert false;
                break;
        }
        /*
         * Collections.sort(newContainer, (Task t1, Task t2)-> t2.getId() -
         * t1.getId()); newContainer.forEach( t -> System.out.println(t.getId())
         * );
         */

        return new TasksBag(newContainer);
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
            return 1;
        } else if (firstCom == null) {
            return -1;
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

    /**
     * Makes a copy of the all the tasks reference
     * 
     * @param t
     * @return
     */
    public static ObservableList<Task> copy(ObservableList<Task> t) {
        ObservableList<Task> rtn = FXCollections.observableArrayList();

        t.forEach(e -> rtn.add(e));
        return rtn;
    }

    public SortBy getState() {
        return cSortState;
    }

    public boolean isEmpty() {
        return tasks.size() == 0;
    }
}

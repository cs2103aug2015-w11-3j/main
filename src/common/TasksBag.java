package common;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Storage for Tasks
 * Provides adding, deleting and sorting
 */
public class TasksBag implements Iterable<Task> {

	public enum SortBy {
		START_DATE,
		END_DATE
	}

	private ObservableList<Task> tasks;

	public TasksBag() {
		tasks = FXCollections.observableArrayList();
	}

	public TasksBag(ObservableList<Task> t){
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

	/**
	 * Sort will return a new container as specified by sorted type
	 */
	public TasksBag sort(SortBy attribute) {
		//assert attribute != null;
		
		ObservableList<Task> newContainer = TasksBag.copy(tasks);
		/*
		switch(attribute){
		case END_DATE:
			//Collections.sort(newContainer, (Task t1, Task t2)-> t2.getId() - t1.getId());
			break;
		case START_DATE:
			break;
		default:
			break;
		}
		*/
		Collections.sort(newContainer, (Task t1, Task t2)-> t2.getId() - t1.getId());
		newContainer.forEach( 
				t -> System.out.println(t.getId())
				);
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

	@Override
	public Iterator<Task> iterator() {
		return tasks.iterator();
	}

	/**
	 * Makes a copy of the all the tasks reference
	 * @param t
	 * @return
	 */
	public static ObservableList<Task> copy(ObservableList<Task> t){
		ObservableList<Task> rtn = FXCollections.observableArrayList();
		
		t.forEach(e -> rtn.add(e));
		return rtn;
	}
}

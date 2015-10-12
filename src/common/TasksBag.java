package common;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Storage for Tasks
 * Provides sorting
 */
public class TasksBag implements Iterable<Task>, Cloneable {
	
	public enum SortBy{
		
	}
	
	private ObservableList<Task> tasks;
	
	public TasksBag(){
		tasks = FXCollections.observableArrayList();
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
	
	public void addTask(int index, Task c){
		assert c != null : c;
		assert index >= 0 : index;
		
		tasks.add(index, c);
	}
	public int size(){
		return tasks.size();
	}
	
	public ObservableList<Task> getList() {
		return tasks;
	}
	/*
	 * Sort will return a new container with sorted type
	 */
	public TasksBag sort(SortBy attribute){
		TasksBag newContainer = new TasksBag();
		return newContainer;
	}

	public Task removeTask(int index){
		assert index >= 0 : index;
		assert index <= tasks.size() - 1: index;
		
		Task rtnCelebi = tasks.remove(index);
		return rtnCelebi;
	}
	
	public int removeTask(Task t){
		assert t != null : "Null task";
		
		int rtnIndex = tasks.indexOf(t);
		assert rtnIndex >= 0 : rtnIndex;
		
		tasks.remove(rtnIndex);
		
		return rtnIndex;
	}
	@Override
	public Iterator<Task> iterator() {
		// TODO Auto-generated method stub
		return tasks.iterator();
	}
	
}

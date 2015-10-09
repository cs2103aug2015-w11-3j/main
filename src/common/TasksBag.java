package common;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Storage for Tasks
 * Provides sorting
 */
public class TasksBag implements Iterable<Task> {
	
	public enum SortBy{
		
	}
	
	private ObservableList<Task> celebis;
	
	public TasksBag(){
		celebis = FXCollections.observableArrayList();
	}
	
	public Task getCelebi(int index) {		
		return celebis.get(index);
	}
	
	public Task addCelebi(Task c) {
		// TODO
		celebis.add(c);
		return c;
	}
	
	public int size(){
		return celebis.size();
	}
	
	public ObservableList<Task> getList() {
		return celebis;
	}
	/*
	 * Sort will return a new container with sorted type
	 */
	public TasksBag sort(SortBy attribute){
		TasksBag newContainer = new TasksBag();
		return newContainer;
	}

	public Task removeCelebi(int index){
		assert index >= 0 : index;
		assert index < celebis.size() - 1: index;
		
		Task rtnCelebi = celebis.remove(index);
		return rtnCelebi;
	}
	
	@Override
	public Iterator<Task> iterator() {
		// TODO Auto-generated method stub
		return celebis.iterator();
	}
}

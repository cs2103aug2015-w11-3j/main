package common;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Storage for Celebis
 * Provides sorting
 */
public class CelebiBag implements Iterable<Celebi> {
	
	public enum SortBy{
		
	}
	
	private ObservableList<Celebi> celebis;
	
	public CelebiBag(){
		celebis = FXCollections.observableArrayList();
	}
	
	public Celebi getCelebi(int index) {		
		return celebis.get(index);
	}
	
	public Celebi addCelebi(Celebi c) {
		// TODO
		celebis.add(c);
		return c;
	}
	
	public int size(){
		return celebis.size();
	}
	
	public ObservableList<Celebi> getList() {
		return celebis;
	}
	/*
	 * Sort will return a new container with sorted type
	 */
	public CelebiBag sort(SortBy attribute){
		CelebiBag newContainer = new CelebiBag();
		return newContainer;
	}

	public Celebi removeCelebi(int index){
		assert index >= 0 : index;
		assert index < celebis.size() - 1: index;
		
		Celebi rtnCelebi = celebis.remove(index);
		return rtnCelebi;
	}
	
	@Override
	public Iterator<Celebi> iterator() {
		// TODO Auto-generated method stub
		return celebis.iterator();
	}
}

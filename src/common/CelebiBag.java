package common;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Storage for Celebis
 * Provides sorting
 */
public class CelebiBag implements Iterable<Celebi> {
	
	public enum SortBy{
		
	}
	
	private ArrayList<Celebi> celebis;
	
	public CelebiBag(){
		celebis = new ArrayList<Celebi>();
	}
	
	public Celebi getCelebi(int index) {		
		return celebis.get(index);
	}
	
	public Celebi addCelebi(Celebi c) {
		// TODO
		celebis.add(c);
		return c;
	}
	
	
	public Celebi removeCelebi(int index){
		assert index >= 0 : index;
		assert index < celebis.size() - 1: index;
		
		Celebi rtnCelebi = celebis.remove(index);
		return rtnCelebi;
	}
	
	public int size(){
		return celebis.size();
	}
	
	/*
	 * Sort will return a new container with sorted type
	 */
	public CelebiBag sort(SortBy attribute){
		CelebiBag newContainer = new CelebiBag();
		return newContainer;
	}
	
	@Override
	public Iterator<Celebi> iterator() {
		// TODO Auto-generated method stub
		return celebis.iterator();
	}
	
}

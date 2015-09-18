package common;

import java.util.ArrayList;

/*
 * Storage for Celebis
 * Provides sorting
 */
public class CelebiBag {
	
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
		
		return null;
	}
	
	/*
	 * Sort will return a new container with sorted type
	 */
	public CelebiBag sort(SortBy attribute){
		CelebiBag newContainer = new CelebiBag();
		return newContainer;
	}
}

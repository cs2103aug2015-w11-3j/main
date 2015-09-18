package common;

import java.util.ArrayList;

/*
 * Storage for Celebis
 * Provides sorting
 */
public class CelebiContainer {
	
	public enum SortBy{
		
	}
	
	private ArrayList<Celebi> celebis;
	
	public CelebiContainer(){
		celebis = new ArrayList<Celebi>();
	}
	
	public Celebi getCelebi(int index) {		
		return celebis.get(index);
	}
	
	public Celebi addtCelebi(Celebi c) {
		// TODO
		
		return null;
	}
	
	/*
	 * Sort will return a new container with sorted type
	 */
	public CelebiContainer sort(SortBy attribute){
		CelebiContainer newContainer = new CelebiContainer();
		return newContainer;
	}
}

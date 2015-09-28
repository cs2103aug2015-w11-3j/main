package common;

import java.util.Date;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;

/*
 * Our task object.
 * Stores task data
 */
public class Celebi {

	// Reference for possible Celebi data fields
	public static final enum Data {
		ID, 
		NAME, DESCRIPTION,
		DATE_START, DATE_END,
		TAGS, PRIORITY, 
		IS_COMPLETED,
		BLOCKED_PERIODS,
		SCHEDULED_DAYS
	}

	public static final enum Priority {
		LOW, NORMAL, HIGH, CRITICAL
	}

	private int cId;

	private String cName;
	private String cDescription;

	private Date cStart;
	private Date cEnd;

	private Priority cPriority;
	private boolean cIsCompleted;

	/*
		Abstract data structure used is Set.
		Primary operations: find, remove, iterateAll.
		Constraints: no duplicates.

		Use LinkedHashSet implementation.
		HashMap provide O(1) access and removal.
		LinkedHashSet provides O(n) iterateAll compared
		to HashMap's O(m). 
		n = number of values, m = table size, n < m.
	*/
	private Set<String> cTags;
	// Implementation: ArrayList
	private List<Period> cBlocked;

	
	// constructor
	public Celebi (String name, Date start, Date end) {
		cName = name;
		cStart = start;
		cEnd = end;
	}
	
	

	// setters
	public void setComplete(boolean isComplete) {
		cIsComplete = isComplete;
	}
	
	public void setId(int id) {
		cId = id;
	}
	
	public void setName(String name) {
		cName = name;
	}	
	
	public void setStart(Date start) {
		cStart = start.clone();
	}
	
	public void setEnd(Date end) {
		cEnd = end.clone();
	}	
	
	
	//getters
	public boolean isComplete() {
		return cIsComplete;
	}
	
	public int getId() {
		return cId;
	}
	
	public String getName() {
		return cName;
	}
	
	public Date getStart() {
		return cStart.clone();
	}
	
	public Date getEnd() {
		return cEnd.clone();
	}
	
	
	// private methods
	
}

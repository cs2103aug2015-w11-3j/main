package common;

import java.util.Date;

/*
 * Our task object.
 * Stores task data
 */
public class Celebi {

	public static final enum CelebiPriority {
		Low, Normal, High, Critical
	}

	private int cId;
	private String cName;
	private Date cStart;
	private Date cEnd;
	boolean cIsComplete;
	// ArrayList<String> tags;
	// int priorityLevel; // Unknown usage
	// ArrayList<TimePeriod> blockOffPeriods;
	
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
		cStart = cloneDate(start);
	}
	
	public void setEnd(Date end) {
		cEnd = cloneDate(end);
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
		return cloneDate(cStart);
	}
	
	public Date getEnd() {
		return cloneDate(cEnd);
	}
	
	
	// private methods
	
	// generate a new date to prevent the data in this Celebi from being changed
	private Date cloneDate(Date date) {
		if (date == null) {
			return null;
		} else {
			return new Date(date.getTime());
		}
	}
}

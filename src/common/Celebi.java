package common;

import java.util.Date;
import java.util.Set;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedHashSet;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

/*
 * Our task object.
 * Stores task data
 */
public class Celebi {

	// Reference for possible Celebi data fields
	public static enum Data {
		ID, 
		NAME, DESCRIPTION,
		DATE_START, DATE_END,
		TAGS, PRIORITY, 
		IS_COMPLETED,
		BLOCKED_PERIODS,
		SCHEDULED_DAYS
	}

	public static enum Priority {
		LOW, NORMAL, HIGH, CRITICAL
	}

	private int cId;

	private StringProperty cName;
	private StringProperty cDescription;

	private final ObjectProperty<Date> cStart;
	private final ObjectProperty<Date> cEnd;

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

	
	// constructor, use property instead of value
	public Celebi (String name, Date start, Date end) {
		cName = new SimpleStringProperty(name);
		cStart = new SimpleObjectProperty<Date>(start);
		cEnd = new SimpleObjectProperty<Date>(end);
		
		//cStart = new SimpleObjectProperty<LocalDate>(localStart);
		//cEnd = new SimpleObjectProperty<LocalDate>(localEnd);
	}
	
	

	// setters
	public void setComplete(boolean isComplete) {
		cIsCompleted = isComplete;
	}
	
	public void setId(int id) {
		cId = id;
	}
	
	public void setName(String name) {
		cName.set(name);
	}	
	
	public void setStart(Date start) {
		cStart.set(start);
		//cStart.set(convertToLocalDate((Date)start.clone()));
	}
	
	public void setEnd(Date end) {
		cEnd.set(end);
		//cEnd.set(convertToLocalDate((Date)end.clone()));
	}	
	
	
	//getters
	public boolean isComplete() {
		return cIsCompleted;
	}
	
	public int getId() {
		return cId;
	}
	
	public String getName() {
		return cName.get();
	}
	
	public Date getStart() {
		return (Date)cStart.get().clone();
	}
	
	public Date getEnd() {
		return (Date)cEnd.get().clone();
	}
	
	// get properties
	public StringProperty nameProperty() {
        return cName;
    }
	
	public ObjectProperty<Date> startProperty() {
        return cStart;
    }
	
	public ObjectProperty<Date> endProperty() {
        return cEnd;
    }
	
	// private methods
	/*
	private LocalDate convertToLocalDate(Date date) {
		LocalDate localDate;
		if (date != null) {
			localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		else {
			localDate = null;
		}
		return localDate;
	}
	
	private Date convertToDate(LocalDate localDate) {
		Date date;
		if (localDate != null) {
			date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		else {
			date = null;
		}
		return date;
	}
	*/
}

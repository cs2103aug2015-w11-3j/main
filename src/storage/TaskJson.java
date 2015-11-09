//@@author A0133920N

/*
 * @class: TaskJson
 * 
 * A subclass of JSONObject which specifically represents a task in Celebi. 
 * It is designed for the translation between JSON object and the Task class. 
 * It has the same fields as the Task class, but in the key-value pair format.
 */

package storage;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;

import common.*;

class TaskJson extends LinkedHashMap<String, String>{
	private static final long serialVersionUID = 1L;
	
	// All keys for a task
	static final String KEY_NAME_ID = "ID";
	static final String KEY_NAME_NAME = "NAME";
	static final String KEY_NAME_DATE_START = "DATE_START";
	static final String KEY_NAME_DATE_END = "DATE_END";
	static final String KEY_NAME_IS_COMPLETED = "IS_COMPLETED";
	static final String[] KEYS = { 
		KEY_NAME_ID, KEY_NAME_NAME, KEY_NAME_DATE_START, 
		KEY_NAME_DATE_END, KEY_NAME_IS_COMPLETED 
	};
	
	// Some special values used in this class
	static final String VALUE_NULL = "null";
	static final String VALUE_TRUE = "true";
	static final String VALUE_FALSE = "false";
	
	// Tools to parse date from string
	static final String DATE_FOTMAT = "yyyy-MM-dd HH:mm";
	static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FOTMAT);
	
	boolean _isValid = true;
	
	// Two constructors, one is from Task class, the other is from JSON object
	TaskJson (Task c) {
		String id = Integer.toString(c.getId());
		String name = c.getName();
		String start = formatDate(c.getStart());
		String end = formatDate(c.getEnd());
		String isCompleted = c.isCompleted() ? VALUE_TRUE : VALUE_FALSE;
		
		put(KEY_NAME_ID, id);
		put(KEY_NAME_NAME, name);
		put(KEY_NAME_DATE_START, start);
		put(KEY_NAME_DATE_END, end);
		put(KEY_NAME_IS_COMPLETED, isCompleted);
	}
	
	TaskJson (JSONObject j) {
		mapFromJSONObject(j);
				
		// It is possible for the content in storage file being corrupted,
		// therefore validation is needed in this case
		if (isValid()) {
			validateId ();
			validateName ();
			validateDates ();
			validateIsCompleted ();
		}
	}
	
	// Transfer itself into a Task object
	Task toTask () {
		int id = Integer.parseInt(get(KEY_NAME_ID));
		String name = get(KEY_NAME_NAME);
		Date start = parseDate(get(KEY_NAME_DATE_START));
		Date end = parseDate(get(KEY_NAME_DATE_END));
		boolean isCompleted = get(KEY_NAME_IS_COMPLETED).equals(VALUE_TRUE);
		
		Task c = new Task(name, start, end);
		c.setId(id);
		c.setComplete(isCompleted);
		
		return c;
	}
	
	// Getters
	boolean isValid () {
		return _isValid;
	}
	
	int getId () {
		String id = get(KEY_NAME_ID);
		if (id == null) {
			return 0;
		} else {
			return Integer.parseInt(get(KEY_NAME_ID));
		}
	}
	
	// Setters
	void setId (int id) {
		put(KEY_NAME_ID, Integer.toString(id));
	}

	void update (TaskJson cj) {
		// make all its attributes the same as the input TaskJson
		put(KEY_NAME_ID, cj.get(KEY_NAME_ID));
		put(KEY_NAME_NAME, cj.get(KEY_NAME_NAME));
		put(KEY_NAME_DATE_START, cj.get(KEY_NAME_DATE_START));
		put(KEY_NAME_DATE_END, cj.get(KEY_NAME_DATE_END));
		put(KEY_NAME_IS_COMPLETED, cj.get(KEY_NAME_IS_COMPLETED));
	}
	
	// Private Methods
	private String formatDate (Date d) {
		if (d == null) {
			return VALUE_NULL;
		}
		return formatter.format(d);
	}
	
	private Date parseDate (String s) {
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(s, pos); 
	}
	
	private void mapFromJSONObject(JSONObject j) {
		for (int i = 0; i < KEYS.length; i++) {
			checkAndMapField(j, KEYS[i]);
		}
	}
	
	private void checkAndMapField (JSONObject j, String key) {
		if (j.get(key) == null) {
			_isValid = false;
		} else {
			put(key, (String)j.get(key));
		}
	}
	
	// Validators
	private void validateId () {
		try {
			int id = Integer.parseInt(get(KEY_NAME_ID));
			if (id < 1) {
				_isValid = false;
			}
		} catch (NumberFormatException e) {
			_isValid = false;
		}
	}
	
	private void validateName () {
		if (get(KEY_NAME_NAME).trim().equals("")) {
			_isValid = false;
		}
	}
	
	private void validateDates () {
		Date parsedStart = parseDate(get(KEY_NAME_DATE_START));
		Date parsedEnd = parseDate(get(KEY_NAME_DATE_END));
		
		if (!get(KEY_NAME_DATE_START).equals("null") && parsedStart == null) {
			_isValid = false;
		} else if (!get(KEY_NAME_DATE_END).equals("null") && parsedEnd == null) {
			_isValid = false;
		} else if (parsedStart != null && parsedEnd != null && parsedStart.compareTo(parsedEnd) > 0) {
			_isValid = false;
		}		
	}
	
	private void validateIsCompleted () {
		if (!get(KEY_NAME_IS_COMPLETED).equals(VALUE_TRUE) && 
			!get(KEY_NAME_IS_COMPLETED).equals(VALUE_FALSE)) {
			_isValid = false;
		}
	}
	
	// End of Private Methods
	
	// An inner class specially designed to compare two tasks
	static class TJComparator implements Comparator<TaskJson> {
		@Override
		public int compare(TaskJson tj1, TaskJson tj2) {
			int id1 = tj1.getId();
			int id2 = tj2.getId();
			
			assert(id1 > 0 && id2 > 0);
			
			if (id1 < id2) {
				return -1;
			} else if (id1 == id2) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	
	static TJComparator getComparator () {
		return new TJComparator();
	}
}

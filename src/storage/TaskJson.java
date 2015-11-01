//@@author A0133920N
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
	
	static final String KEY_NAME_ID = "ID";
	static final String KEY_NAME_NAME = "NAME";
	static final String KEY_NAME_DATE_START = "DATE_START";
	static final String KEY_NAME_DATE_END = "DATE_END";
	static final String KEY_NAME_IS_COMPLETED = "IS_COMPLETED";
	
	static final String VALUE_NULL = "null";
	static final String VALUE_TRUE = "true";
	static final String VALUE_FALSE = "false";
	
	static final String DATE_FOTMAT = "yyyy-MM-dd HH:mm";
	static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FOTMAT);
	
	// constructor
	public TaskJson (Task c) {
		String id = Integer.toString(c.getId());
		String name = c.getName();
		String start = formatDate(c.getStart());
		String end = formatDate(c.getEnd());
		String isCompleted = c.isComplete() ? VALUE_TRUE : VALUE_FALSE;
		
		put(KEY_NAME_ID, id);
		put(KEY_NAME_NAME, name);
		put(KEY_NAME_DATE_START, start);
		put(KEY_NAME_DATE_END, end);
		put(KEY_NAME_IS_COMPLETED, isCompleted);
	}
	
	public TaskJson (JSONObject j) {
		put(KEY_NAME_ID, (String)j.get(KEY_NAME_ID));
		put(KEY_NAME_NAME, (String)j.get(KEY_NAME_NAME));
		put(KEY_NAME_DATE_START, (String)j.get(KEY_NAME_DATE_START));
		put(KEY_NAME_DATE_END, (String)j.get(KEY_NAME_DATE_END));
		put(KEY_NAME_IS_COMPLETED, (String)j.get(KEY_NAME_IS_COMPLETED));
		
		// check whether the date of JSON object is valid
		// if not, set null and rewrite next time
		if (parseDate(get(KEY_NAME_DATE_START)) == null) {
			put(KEY_NAME_DATE_START, VALUE_NULL);
		}
		
		if (parseDate(get(KEY_NAME_DATE_END)) == null) {
			put(KEY_NAME_DATE_END, VALUE_NULL);
		}
	}
	
	public Task toCelebi () {
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
	
	public void setId (int id) {
		put(KEY_NAME_ID, Integer.toString(id));
	}
	
	public int getId () {
		String id = get(KEY_NAME_ID);
		if (id == null) {
			return 0;
		} else {
			return Integer.parseInt(get(KEY_NAME_ID));
		}
	}
	
	public void update (TaskJson cj) {
		put(KEY_NAME_ID, cj.get(KEY_NAME_ID));
		put(KEY_NAME_NAME, cj.get(KEY_NAME_NAME));
		put(KEY_NAME_DATE_START, cj.get(KEY_NAME_DATE_START));
		put(KEY_NAME_DATE_END, cj.get(KEY_NAME_DATE_END));
		put(KEY_NAME_IS_COMPLETED, cj.get(KEY_NAME_IS_COMPLETED));
	}
	
	public static TJComparator getComparator () {
		return new TJComparator();
	}
	
	// private methods
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
	
	static class TJComparator implements Comparator<TaskJson> {
		@Override
		public int compare(TaskJson tj1, TaskJson tj2) {
			int id1 = tj1.getId();
			int id2 = tj2.getId();
			
			if (id1 <= 0 || id2 <= 0) {
				throw new IllegalArgumentException("Trying to compare TaskJson without ID");
			} else if (id1 < id2) {
				return -1;
			} else if (id1 == id2) {
				return 0;
			} else {
				return 1;
			}
		}
	}

}

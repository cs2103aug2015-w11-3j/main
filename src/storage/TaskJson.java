package storage;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;

import common.*;

class TaskJson extends LinkedHashMap<String, String>{
	private static final long serialVersionUID = 1L;
	
	static final String DATE_FOTMAT = "yyyy-MM-dd HH:mm";
	static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FOTMAT);
	
	// constructor
	public TaskJson (Task c) {
		String id = Integer.toString(c.getId());
		String name = c.getName();
		String start = formatDate(c.getStart());
		String end = formatDate(c.getEnd());
		
		put("ID", id);
		put("NAME", name);
		put("DATE_START", start);
		put("DATE_END", end);
	}
	
	public TaskJson (JSONObject j) {
		put("ID", (String)j.get("ID"));
		put("NAME", (String)j.get("NAME"));
		put("DATE_START", (String)j.get("DATE_START"));
		put("DATE_END", (String)j.get("DATE_END"));
		
		// check whether the date of JSON object is valid
		// if not, set null and rewrite next time
		if (parseDate(get("DATE_START")) == null) {
			put("DATE_START", "null");
		}
		
		if (parseDate(get("DATE_END")) == null) {
			put("DATE_START", "null");
		}
	}
	
	public Task toCelebi () {
		String name = get("NAME");
		Date start = parseDate(get("DATE_START"));
		Date end = parseDate(get("DATE_END"));
		
		Task c = new Task(name, start, end);
		c.setId(Integer.parseInt(get("ID")));
		
		return c;
	}
	
	public void setId (int id) {
		put("ID", Integer.toString(id));
	}
	
	public int getId () {
		String id = get("ID");
		if (id == null) {
			return 0;
		} else {
			return Integer.parseInt(get("ID"));
		}
	}
	
	public void update (TaskJson cj) {
		put("ID", cj.get("ID"));
		put("NAME", cj.get("NAME"));
		put("DATE_START", cj.get("DATE_START"));
		put("DATE_END", cj.get("DATE_END"));
	}
	
	// private methods
	private String formatDate (Date d) {
		if (d == null) {
			return "null";
		}
		return formatter.format(d);
	}
	
	private Date parseDate (String s) {
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(s, pos); 
	}

}

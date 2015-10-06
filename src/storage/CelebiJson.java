package storage;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;

import common.*;

public class CelebiJson extends LinkedHashMap<String, String>{
	private static final long serialVersionUID = 1L;
	
	static final String DATE_FOTMAT = "yyyy-MM-dd HH:mm";
	static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FOTMAT);
	
	// constructor
	public CelebiJson (Celebi c) {
		String id = Integer.toString(c.getId());
		String name = c.getName();
		String start = formatDate(c.getStart());
		String end = formatDate(c.getEnd());
		
		put("ID", id);
		put("NAME", name);
		put("DATE_START", start);
		put("DATE_END", end);
	}
	
	public CelebiJson (JSONObject j) {
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
	
	public Celebi toCelebi () {
		String name = get("NAME");
		Date start = parseDate(get("DATE_START"));
		Date end = parseDate(get("DATE_END"));
		
		System.out.println("aaaaaaaaaaaaaaaa");
		System.out.println(start);
		System.out.println(end);
		
		Celebi c = new Celebi(name, start, end);
		c.setId(Integer.parseInt(get("ID")));
		
		return c;
	}
	
	public void setId (int id) {
		put("ID", Integer.toString(id));
	}
	
	public void update (CelebiJson cj) {
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

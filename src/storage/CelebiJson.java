package storage;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import common.*;

public class CelebiJson extends LinkedHashMap<Celebi.DataType, String>{
	private static final long serialVersionUID = 1L;
	
	static final String DATE_FOTMAT = "yyyy-MM-dd HH:mm";
	static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FOTMAT);
	
	// constructor
	public CelebiJson (Celebi c) {
		String id = Integer.toString(c.getId());
		String name = c.getName();
		String start= formatDate(c.getStart());
		String end = formatDate(c.getEnd());
		
		put(Celebi.DataType.ID, id);
		put(Celebi.DataType.NAME, name);
		put(Celebi.DataType.DATE_START, start);
		put(Celebi.DataType.DATE_END, end);
	}
	
	public Celebi toCelebi () {
		String name = get(Celebi.DataType.NAME);
		Date start = parseDate(get(Celebi.DataType.DATE_START));
		Date end = parseDate(get(Celebi.DataType.DATE_END));
		
		Celebi c = new Celebi(name, start, end);
		c.setId(Integer.parseInt(get(Celebi.DataType.ID)));
		
		return c;
	}
	
	// private methods
	private String formatDate(Date d) {
		return formatter.format(d);
	}
	
	private Date parseDate(String s) {
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(s, pos); 
	}

}

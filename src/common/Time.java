// @author Liu Yang
package common;

public class Time {
	private final int VALID_LENGTH = 5;
	private final int VALID_FIELD_NUM = 2;
	
	private final int INDEX_HOUR = 0;
	private final int INDEX_MIN = 1;
	
	private final int UPPER_BOUND_HOUR = 23;
	private final int UPPER_BOUND_MIN = 59;
	private final int LOWER_BOUND_HOUR = 0;
	private final int LOWER_BOUND_MIN = 0;
	
	private int m_hour;
	private int m_min;
	
	private boolean m_isValid;
	
	public Time(String t) {
		m_isValid = parseValue(t);
	}
	
	public int getHour() {
		return m_hour;
	}
	
	public int getMin() {
		return m_min;
	}
	
	public boolean isValid() {
		 return m_isValid;
	}
	
	private boolean parseValue(String t) {
		String[] ts;
		String hour, min;
		
		if(t == null || t.length() != VALID_LENGTH) {
			return false;
		}
		
		ts = t.split(":");
		
		if(ts.length != VALID_FIELD_NUM) {
			return false;
		}
		
		hour = ts[INDEX_HOUR];
		min = ts[INDEX_MIN];
		
		return parseHour(hour) && parseMin(min);
	}
	
	private boolean parseHour(String s) {
		try {
			m_hour = Integer.parseInt(s);
			return LOWER_BOUND_HOUR <= m_hour && m_hour <= UPPER_BOUND_HOUR;
		} catch(NumberFormatException e){
			return false;
	    }
	}
	
	public boolean parseMin(String s) {
		try {
			m_min = Integer.parseInt(s);
			return LOWER_BOUND_MIN <= m_min && m_min <= UPPER_BOUND_MIN;
		} catch(NumberFormatException e){
			return false;
	    }
	}
}

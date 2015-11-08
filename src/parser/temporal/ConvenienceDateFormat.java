//@@author A0131891E
package parser.temporal;

import static java.util.Calendar.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import common.Configuration;
import common.Time;
public class ConvenienceDateFormat implements CelebiDateParser {
	
	public ConvenienceDateFormat () {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Date parseDate (String s, boolean isStart) throws ParseException {
		Calendar cal = new GregorianCalendar();
		Time t;
		if (isStart) {
			t = Configuration.getInstance().getDefaultStartTime();
		} else {
			t = Configuration.getInstance().getDefaultEndTime();
		}
		cal.set(HOUR_OF_DAY, t.getHour());
		cal.set(MINUTE, t.getMin());
		switch (s.trim().toLowerCase()) {
		
			case "none" :
			case "empty" :
			case "clear" :
			case "remove" :
			case "null" :
				return null;
		
			// current time
			case "now" :
				return new Date();
				
			case "today" :
				return cal.getTime();
				
			// +24h
			case "tmr" :		// Fallthrough
			case "tomorrow" :
				cal.add(DAY_OF_YEAR, 1);
				return cal.getTime();
				
			// +24*7h
			case "next week" :
				cal.add(WEEK_OF_YEAR, 1);
				return cal.getTime();
				
			case "sun" :
			case "sunday" :
				cal.set(DAY_OF_WEEK, SUNDAY);
				return cal.getTime();
			case "mon" :
			case "monday" :
				cal.set(DAY_OF_WEEK, MONDAY);
				return cal.getTime();
			case "tue" :
			case "tues" :
			case "tuesday" :
				cal.set(DAY_OF_WEEK, TUESDAY);
				return cal.getTime();
			case "wed" :
			case "wednesday" :
				cal.set(DAY_OF_WEEK, WEDNESDAY);
				return cal.getTime();
			case "thu" :
			case "thur" :
			case "thurs" :
			case "thursday" :
				cal.set(DAY_OF_WEEK, THURSDAY);
				return cal.getTime();
			case "fri" :
			case "friday" :
				cal.set(DAY_OF_WEEK, FRIDAY);
				return cal.getTime();
			case "sat" :
			case "saturday" :
				cal.set(DAY_OF_WEEK, SATURDAY);
				return cal.getTime();
			
			case "next mon" :
			case "next monday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, MONDAY);
				return cal.getTime();
				
			case "next tue" :
			case "next tues" :
			case "next tuesday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, TUESDAY);
				return cal.getTime();
				
			case "next wed" :
			case "next wednesday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, WEDNESDAY);
				return cal.getTime();
				
			case "next thu" :
			case "next thur" :
			case "next thursday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, THURSDAY);
				return cal.getTime();
				
			case "next fri" :
			case "next friday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, FRIDAY);
				return cal.getTime();

			case "next sat" :
			case "next saturday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, SATURDAY);
				return cal.getTime();

			case "next sun" :
			case "next sunday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, SUNDAY);
				return cal.getTime();
				
			default:
				throw new ParseException("", -1);
		}
	}
	
	public static void main (String[] args) {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		SimpleDateFormat df = new SimpleDateFormat("EEE");
		while (true) {
			try {
				GregorianCalendar cal = new GregorianCalendar();
				GregorianCalendar cal2 = new GregorianCalendar();
				cal.setTime(df.parse(sc.nextLine()));
				cal2.set(Calendar.DAY_OF_WEEK, cal.get(Calendar.DAY_OF_WEEK));
				System.out.println(cal2.getTime());
			} catch (ParseException pe) {
				;
			}
		}
	}
}

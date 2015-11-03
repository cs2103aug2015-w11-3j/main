//@@author A0131891E
package ui;

import static java.util.Calendar.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import parser.DateParser;

public class DateFormatter {

	public DateFormatter () {
		// TODO Auto-generated constructor stub
	}

	private static final DateFormat YEAR_LVL = new SimpleDateFormat("h:mma EEEE, d MMM YYYY");
	private static final DateFormat MONTH_LVL = new SimpleDateFormat("h:mma EEEE, d MMMM");
	private static final DateFormat WEEK_LVL = new SimpleDateFormat("h:mma EEEE d");
	private static final DateFormat WEEKDAY_LVL = new SimpleDateFormat("h:mma EEEE '(this week)'");
	private static final DateFormat TMR_LVL = new SimpleDateFormat("h:mma 'tomorrow'");
	private static final DateFormat TODAY_LEVEL = new SimpleDateFormat("h:mma 'today'");
	
	public String formatDate (Date d) {
		final GregorianCalendar now = new GregorianCalendar();
		final GregorianCalendar date = new GregorianCalendar();
		date.setTime(d);
		String temp;
		if (now.get(YEAR) != date.get(YEAR)) {
			return YEAR_LVL.format(d);
		}
		if (now.get(MONTH) != date.get(MONTH)) {
			return MONTH_LVL.format(d);
		}
		if (now.get(WEEK_OF_YEAR) != date.get(WEEK_OF_YEAR)) {
			return  WEEK_LVL.format(d);
		}
		if (now.get(DAY_OF_YEAR) + 1 == date.get(DAY_OF_YEAR)) {
			return TMR_LVL.format(d);
		}
		if (now.get(DAY_OF_YEAR) != date.get(DAY_OF_YEAR)) {
			return WEEKDAY_LVL.format(d);
		}
		return TODAY_LEVEL.format(d);
	}
	
	private static String numeralSuffix (char lastDigit) {
		switch (lastDigit) {
			case '1' :
				return "st";
			case '2' :
				return "nd";
			case '3' :
				return "rd";
			default :
				return "th";
		}
	}
	
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		DateFormatter df = new DateFormatter();
		DateParser dp = new DateParser();
		while (true) {
			try {
				System.out.println(df.formatDate(dp.parseDate(sc.nextLine(), false)));
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
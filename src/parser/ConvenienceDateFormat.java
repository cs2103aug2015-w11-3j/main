//@@author A0131891E
package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
public class ConvenienceDateFormat implements CelebiDateFormatter {

	private static final String[] k = {};
	
	public ConvenienceDateFormat () {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Date parseDate (String s) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main (String[] args) {
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

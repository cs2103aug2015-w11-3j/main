package parser;
import java.util.Date;
import java.text.ParseException;
public interface DateParsingFormat {
	public Date parse (String s) throws ParseException;
	//public String format (Date d);
}

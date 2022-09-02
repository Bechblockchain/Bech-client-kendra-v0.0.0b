package Util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Helper {

	  public static String longToDateTime(long timestamp) {
	    	Date date = new Date(timestamp * 1000);
	    	DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	    	return formatter.format(date);
	  }
	
}

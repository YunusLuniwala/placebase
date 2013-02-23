/**
 * Helper methods for handling dates 
 * 
 * uses the Joda time 2.1 API
 * http://joda-time.sourceforge.net/apidocs/overview-summary.html
 * 
 * // http://stackoverflow.com/questions/11190346/android-using-joda-time-to-calculate-diference-between-dates
 */
package com.ag.masters.placebase.handlers;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

public class DateHandler {
	
	public DateHandler(){
		
	}
	
	// get current Date
	public LocalDate getCurrentDate() {
		LocalDate date = new LocalDate();
		return date;
	}
	
	/**
	 * Calculate the number of days between specified date and current date
	 * @param String endDate from db
	 * @return int
	 */
	public int getDaysAgo(String endDate) {
		// get current time
		DateTime start = DateTime.now();
		
		// convert end time from db String to joda DateTime
		DateTime end = stringToDateTime(endDate);
		
		// calculate interval
		Days interval = Days.daysBetween(start, end);
		
		// get number of days represented by the interval
		int numDays = interval.getDays();
		
		// return number of Days ago
		return Math.abs(numDays);
	}
	
	/**
	 * convert DateTime to String to Store in database
	 * @param dateTime
	 * @return
	 */
	public String dateTimetoString(DateTime dateTime) {
		
		DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		b.append(ISODateTimeFormat.yearMonthDay());
		b.appendLiteral(" ");
		b.append(ISODateTimeFormat.hourMinuteSecond());
		
		DateTimeFormatter fmt = b.toFormatter();
		
		 // print using the defaults (default locale, chronology/zone of the datetime)
		String dateStr = fmt.print(dateTime);
		
		return dateStr;
	}
	
	/**
	 * Convert String to DateTime to use in Joda calculations
	 * @param String dateStr
	 * @return DateTime
	 */
	public DateTime stringToDateTime(String dateStr) {
		DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		b.append(ISODateTimeFormat.yearMonthDay());
		b.appendLiteral(" ");
		b.append(ISODateTimeFormat.hourMinuteSecond());
		
		DateTimeFormatter fmt = b.toFormatter();
		// fmt can now be used to parse "yyyy-MM-dd hh:mm:ss" patterns, like so:
		// DateTime end = fmt.parseDateTime("2010-04-20 20:13:59");
		DateTime dateTime = fmt.parseDateTime(dateStr);
		
		return dateTime;
	}
}

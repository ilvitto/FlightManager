package it.unifi.ing.swam.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.inject.Model;

import it.unifi.ing.swam.model.Flight;

@Model
public class Util implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	public static String getOnlyHoursAndMinutes(Date d) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(d);
	}
	
	public static String getOnlyHoursAndMinutes(int minutes) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date d = new Date(minutes*60*1000 - 60*60*1000);
		return formatter.format(d);
	}
	
	public static String getArrivalTime(Flight flight){
		int gmt1 = flight.getSourceAirport().getGMT();
		int gmt2 = flight.getDestinationAirport().getGMT();
		int timeDifference = (gmt2-gmt1)*60;
		
		return addMinutesToDate(flight.getDepartureTime(),flight.getFlightDuration()+timeDifference);
	}
	
	public static String addMinutesToDate(Date beforeTime, int minutes) {
	    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
	    long curTimeInMs = beforeTime.getTime();
	    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(beforeTime);
	    int daybefore = cal.get(Calendar.DAY_OF_YEAR);
	    cal.setTime(afterAddingMins);
	    int dayafter = cal.get(Calendar.DAY_OF_YEAR);
	    
	    if(dayafter == daybefore)
	    	return getOnlyHoursAndMinutes(afterAddingMins);
	    else
	    	return getOnlyHoursAndMinutes(afterAddingMins) + " +1 ";
	}
	
	public static Date addDays(Date d, int days) {
		for (int i = 0; i < days; i++) 
			d = incrementDay(d);
		return d;
	}
	
	public static Date incrementDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}
	
	public static String getOnlyDate(Date d) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(d);
	}
	
	public static Date subOneHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR, -1);
		return c.getTime();
	}
	
	public static float round(float number, int scale) {
		BigDecimal bd = new BigDecimal(Float.toString(number));
        bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
	
	public static String digest(String input) {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; ++idx) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			
		}

		return hash.toString();
	}

}

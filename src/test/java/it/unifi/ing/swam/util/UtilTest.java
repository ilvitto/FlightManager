package it.unifi.ing.swam.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.util.Util;

public class UtilTest {
	
	@Test
	public void testGetOnlyHoursAndMinutes() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		String expectedResult = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		
		assertEquals(expectedResult, Util.getOnlyHoursAndMinutes(date));
		
		assertEquals("01:10", Util.getOnlyHoursAndMinutes(70));
	}
	
	@Test
	public void testGetArrivalTime() {
		Flight flight;
		Airport sourceAirport;
		Airport destinationAirport;
		
		sourceAirport = ModelFactory.airport();
		sourceAirport.setName("Pisa");
		sourceAirport.setZIP(12345);
		
		destinationAirport = ModelFactory.airport();
		destinationAirport.setName("Palermo");
		destinationAirport.setZIP(54321);
		
		flight = ModelFactory.flight();
		flight.setFlightNumber("F1234");
		flight.setDepartureDate( new Date() );
		flight.setDepartureTime( new Date() );
		flight.setTotalSeats(100);
		flight.setPricePerPerson(50);
		flight.setFlightDuration(90);
		flight.setSourceAirport(sourceAirport);
		flight.setDestinationAirport(destinationAirport);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(flight.getDepartureDate());
		int day = cal.get(Calendar.DAY_OF_YEAR);
		cal.add(Calendar.MINUTE, flight.getFlightDuration());
		
		String expected = new SimpleDateFormat("HH:mm").format(cal.getTime());
		
		if(cal.get(Calendar.DAY_OF_YEAR) != day)
			expected += " +1 ";
		
		assertEquals(expected, Util.getArrivalTime(flight));
	}
	
	@Test
	public void testAddMinutesToDate() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 10);
		
		String expectedResult = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		
		assertEquals(expectedResult, Util.addMinutesToDate(date, 10));
	}
	
	@Test
	public void testAddDays() {
		Date date = new Date();
		int days = 5;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		
		assertEquals(cal.getTime(), Util.addDays(date, days));
	}
	
	@Test
	public void testIncrementDay() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		
		assertEquals(cal.getTime(), Util.incrementDay(date));
	}
	
	@Test
	public void testGetOnlyDate() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		String expectedResult = String.format("%02d/%02d/%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
		
		assertEquals(expectedResult, Util.getOnlyDate(date));
	}
	
	@Test
	public void testSubOneHour() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, -1);
		
		assertEquals(cal.getTime(), Util.subOneHour(date));
	}
	
	@Test
	public void testRound() {
		assertEquals(1.11, Util.round(1.1111f, 2), 5.96e-08);
	}
	
	@Test
	public void testDigest() {
		String clearPassword = "examplepasswordtest"; 
		String digest = "dc1e006a624367b3914a1962caa6bf3073c88b34";
		String fakeDigest = "dc1e007a624367b3914a1962cba6bf3073c88b34";
		
		assertEquals(Util.digest(clearPassword), digest);
		assertFalse(Util.digest(clearPassword).equals(fakeDigest));
	}
}

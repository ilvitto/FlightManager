package it.unifi.ing.swam.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.dao.AirportDao;
import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;

public class InsertFlightControllerTest {
	
	private InsertFlightController insertFlightController;
	
	private FlightDao flightDao;
	
	private AirportDao airportDao;
	
	private Airport sourceAirport;
	private Airport destinationAirport;
	private Airport anotherAirport;
	private Airport anotherOneAirport;
	
	private List<Airport> airports;
	
	private Flight flight1;
	private Flight flight2;
	
	private List<Flight> flights;
	
	@Before
	public void setUp() throws InitializationError {
		insertFlightController = new InsertFlightController();
		
		flightDao = mock(FlightDao.class);
		
		airportDao = mock(AirportDao.class);
		
		sourceAirport = ModelFactory.airport();
		sourceAirport.setName("Pisa");
		sourceAirport.setZIP(12345);
		sourceAirport.setGMT(1);
		
		destinationAirport = ModelFactory.airport();
		destinationAirport.setName("Palermo");
		destinationAirport.setZIP(54321);
		destinationAirport.setGMT(1);
		
		anotherAirport = ModelFactory.airport();
		anotherAirport.setName("Bologna");
		anotherAirport.setZIP(23456);
		anotherAirport.setGMT(1);
		
		anotherOneAirport = ModelFactory.airport();
		anotherOneAirport.setName("Roma");
		anotherOneAirport.setZIP(56789);
		anotherOneAirport.setGMT(1);
		
		airports = new ArrayList<Airport>();
		airports.add(sourceAirport);
		airports.add(destinationAirport);
		
		flight1 = ModelFactory.flight();
		flight1.setFlightNumber("F1234");
		flight1.setDepartureDate( new Date() );
		flight1.setDepartureTime( new Date() );
		flight1.setTotalSeats(100);
		flight1.setPricePerPerson(50);
		flight1.setFlightDuration(90);
		flight1.setSourceAirport(sourceAirport);
		flight1.setDestinationAirport(destinationAirport);
		
		flight1 = ModelFactory.flight();
		flight1.setFlightNumber("F4321");
		flight1.setDepartureDate( new Date() );
		flight1.setDepartureTime( new Date() );
		flight1.setTotalSeats(90);
		flight1.setPricePerPerson(60);
		flight1.setFlightDuration(90);
		flight1.setSourceAirport(destinationAirport);
		flight1.setDestinationAirport(sourceAirport);
		
		flights = new ArrayList<Flight>();
		flights.add(flight1);
		flights.add(flight2);
		
		try {
			FieldUtils.writeField(sourceAirport, "id", Long.valueOf(10), true);
			FieldUtils.writeField(destinationAirport, "id", Long.valueOf(20), true);
			FieldUtils.writeField(anotherAirport, "id", Long.valueOf(30), true);
			FieldUtils.writeField(insertFlightController, "flightDao", flightDao, true);
			FieldUtils.writeField(insertFlightController, "airportDao", airportDao, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testReturnInsertFlightPage() {
		assertTrue(insertFlightController.returnInsertFlightPage().contains("insert-flights"));
	}
	
	@Test
	public void testReset() throws InitializationError {
		insertFlightController.reset();
		
		assertNull(insertFlightController.getSourceAirportId());
		assertNull(insertFlightController.getDestinationAirportId());
		
		try {
			FieldUtils.writeField(insertFlightController, "airports", airports, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		insertFlightController.reset();
		
		assertEquals(airports.get(0).getId(), insertFlightController.getSourceAirportId());
		assertEquals(airports.get(0).getId(), insertFlightController.getDestinationAirportId());
	}
	
	@Test
	public void testGetFlight() {
		assertEquals(0, insertFlightController.getFlight().getReservedSeats());
	}
	
	@Test
	public void testGetAirports() {
		when(airportDao.getAllAirports()).thenReturn(airports);
		
		assertEquals(airports, insertFlightController.getAirports());
	}
	
	@Test
	public void testGetFlights() throws InitializationError {
		assertTrue(insertFlightController.getFlights().isEmpty());
		
		try {
			FieldUtils.writeField(insertFlightController, "flights", flights, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		assertEquals(flights, insertFlightController.getFlights());
	}
	
	@Test
	public void testSearchFlights() throws InitializationError {
		Date date = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 10);
		Date dateAfterFiveMinutes = cal.getTime();
		
		when(flightDao.getAllFlights( date )).thenReturn(flights);
		when(flightDao.getAllFlights( dateAfterFiveMinutes )).thenReturn(null);
		when(flightDao.getFlightsFromDestination(sourceAirport, date)).thenReturn(Arrays.asList(flight2));
		when(flightDao.getFlightsFromDestination(destinationAirport, date)).thenReturn(Arrays.asList(flight1));
		when(flightDao.getFlightsFromDestination(anotherAirport, date)).thenReturn(null);
		when(flightDao.getFlightsFromSource(sourceAirport, date)).thenReturn(Arrays.asList(flight1));
		when(flightDao.getFlightsFromSource(destinationAirport, date)).thenReturn(Arrays.asList(flight2));
		when(flightDao.getFlightsFromSource(anotherAirport, date)).thenReturn(null);
		when(flightDao.getFlights(sourceAirport, destinationAirport, date)).thenReturn(Arrays.asList(flight1));
		when(flightDao.getFlights(destinationAirport, sourceAirport, date)).thenReturn(Arrays.asList(flight2));
		when(flightDao.getFlights(sourceAirport, anotherAirport, date)).thenReturn(null);
		when(flightDao.getFlightsFromDate(sourceAirport, destinationAirport, date)).thenReturn(Arrays.asList(flight1));
		when(flightDao.getFlightsFromDate(destinationAirport, sourceAirport, date)).thenReturn(Arrays.asList(flight2));
		when(flightDao.getFlightsFromDate(sourceAirport, anotherAirport, date)).thenReturn(null);
		
		when(airportDao.findById( Long.valueOf(10) )).thenReturn(sourceAirport);
		when(airportDao.findById( Long.valueOf(20) )).thenReturn(destinationAirport);
		when(airportDao.findById( Long.valueOf(30) )).thenReturn(anotherAirport);
		
		/*-----------------------------------------------if--------------------------------------------------------*/
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(0), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(0), true);
			FieldUtils.writeField(insertFlightController, "fromDate", date, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		String result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().equals(""));
		
		try {
			FieldUtils.writeField(insertFlightController, "fromDate", dateAfterFiveMinutes, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().contains("No available flights"));
		/*-------------------------------------------else if-------------------------------------------------------*/
		try {
			FieldUtils.writeField(insertFlightController, "fromDate", date, true);
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(0), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(20), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().equals(""));
		
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(0), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(30), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().contains("No available flights"));
		/*-------------------------------------------else if-------------------------------------------------------*/
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(10), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(0), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().equals(""));
		
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(30), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(0), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().contains("No available flights"));
		/*-------------------------------------------else----------------------------------------------------------*/
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(10), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(20), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().equals(""));
		
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(10), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(30), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		result = insertFlightController.searchFlights();
		assertTrue(result.contains("show-flights"));
		assertTrue(insertFlightController.getError().contains("No available flights"));
	}
	
	@Test
	public void testSaveFlight() throws InitializationError {		
		when(airportDao.findById( Long.valueOf(10) )).thenReturn(sourceAirport);
		when(airportDao.findById( Long.valueOf(20) )).thenReturn(destinationAirport);
		when(airportDao.findById( Long.valueOf(30) )).thenReturn(anotherAirport);
		
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportType", "existing", true);
			FieldUtils.writeField(insertFlightController, "destinationAirportType", "existing", true);
			FieldUtils.writeField(insertFlightController, "sourceAirportId", Long.valueOf(10), true);
			FieldUtils.writeField(insertFlightController, "destinationAirportId", Long.valueOf(20), true);
			FieldUtils.writeField(insertFlightController, "flight", flight1, true);
			FieldUtils.writeField(insertFlightController, "repeats", Integer.valueOf(5), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		String result = insertFlightController.saveFlight();
		assertTrue(result.contains("insert-flight-success"));
		assertTrue(insertFlightController.getError().equals(""));
		verify(flightDao, times(1)).save(flight1);
		
		//Source airport is new but it already exists
		List<String> airportNames = new ArrayList<String>();
		airportNames.add(sourceAirport.getName());
		airportNames.add(destinationAirport.getName());
		
		when(airportDao.getAllAirportsNames()).thenReturn(airportNames);
		
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportType", "new", true);
			FieldUtils.writeField(insertFlightController, "sourceAirportNew", sourceAirport, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		result = insertFlightController.saveFlight();
		assertTrue(insertFlightController.getError().contains("airport already exist"));
		assertTrue(result.equals("insert-flight"));
		
		//Source airport is new but it doesn't exists
		try {
			FieldUtils.writeField(insertFlightController, "sourceAirportType", "new", true);
			FieldUtils.writeField(insertFlightController, "sourceAirportNew", anotherAirport, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		result = insertFlightController.saveFlight();
		assertTrue(insertFlightController.getError().equals(""));
		assertTrue(result.contains("insert-flight-success"));
		verify(airportDao, times(1)).save(anotherAirport);
		
		//Destination airport is new but it already exists
		try {
			FieldUtils.writeField(insertFlightController, "destinationAirportType", "new", true);
			FieldUtils.writeField(insertFlightController, "destinationAirportNew", destinationAirport, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		result = insertFlightController.saveFlight();
		assertTrue(insertFlightController.getError().contains("airport already exist"));
		assertTrue(result.equals("insert-flight"));
		
		//Source airport is new but it doesn't exists
		try {
			FieldUtils.writeField(insertFlightController, "destinationAirportType", "new", true);
			FieldUtils.writeField(insertFlightController, "destinationAirportNew", anotherOneAirport, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		result = insertFlightController.saveFlight();
		assertTrue(insertFlightController.getError().equals(""));
		assertTrue(result.contains("insert-flight-success"));
		verify(airportDao, times(1)).save(anotherOneAirport);
	}
	
	@Test
	public void testDeleteFlight() {
		when(flightDao.findById( Long.valueOf(10) )).thenReturn(flight1);
		
		assertTrue(insertFlightController.deleteFlight("10").contains("delete-flight-success"));
		
		verify(flightDao, times(1)).delete(flight1);
	}
	
	@Test
	public void testBackToInsertFlight() {
		assertTrue(insertFlightController.backToInsertFlight().contains("insert-flights"));
		assertTrue(insertFlightController.getError().equals(""));
	}
	
	@Test
	public void testShowFlights() {
		assertTrue(insertFlightController.showFlights().contains("show-flights"));
		assertTrue(insertFlightController.getError().equals(""));
	}
}

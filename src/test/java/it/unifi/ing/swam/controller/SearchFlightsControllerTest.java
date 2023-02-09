package it.unifi.ing.swam.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import javax.enterprise.context.Conversation;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.dao.AirportDao;
import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.dao.TemporaryReservationSeatsDao;
import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;

public class SearchFlightsControllerTest {
	
	private SearchFlightsController searchFlightsController;
	
	private FlightDao flightDao;
	
	private AirportDao airportDao;
	
	private TemporaryReservationSeatsDao temporaryReservationSeatsDao;
	
	private Conversation conversation;
	
	private Airport sourceAirport;
	
	private Airport destinationAirport;
	
	private Flight flight;
	
	@Before
	public void setUp() throws InitializationError {
		searchFlightsController = new SearchFlightsController();
		
		flightDao = mock(FlightDao.class);
		
		airportDao = mock(AirportDao.class);
		
		temporaryReservationSeatsDao = mock(TemporaryReservationSeatsDao.class);
		
		conversation = mock(Conversation.class);
		
		sourceAirport = ModelFactory.airport();
		sourceAirport.setName("Pisa");
		sourceAirport.setZIP(12345);
		sourceAirport.setGMT(1);
		
		destinationAirport = ModelFactory.airport();
		destinationAirport.setName("Palermo");
		destinationAirport.setZIP(54321);
		destinationAirport.setGMT(1);
		
		flight = ModelFactory.flight();
		flight.setFlightNumber("F1234");
		flight.setDepartureDate( new Date() );
		flight.setDepartureTime( new Date() );
		flight.setTotalSeats(100);
		flight.setPricePerPerson(50);
		flight.setFlightDuration(90);
		flight.setSourceAirport(sourceAirport);
		flight.setDestinationAirport(destinationAirport);
		
		try {
			FieldUtils.writeField(sourceAirport, "id", Long.valueOf(10), true);
			FieldUtils.writeField(destinationAirport, "id", Long.valueOf(20), true);
			FieldUtils.writeField(searchFlightsController, "flightDao", flightDao, true);
			FieldUtils.writeField(searchFlightsController, "airportDao", airportDao, true);
			FieldUtils.writeField(searchFlightsController, "temporaryReservationSeatsDao", temporaryReservationSeatsDao, true);
			FieldUtils.writeField(searchFlightsController, "nPassengers", 10, true);
			FieldUtils.writeField(searchFlightsController, "conversation", conversation, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testStopConversationAndReturnHome() {
		assertTrue(searchFlightsController.stopConversationAndReturnHome().contains("index"));
	}
	
	@Test
	public void testGetSources() {
		when(airportDao.getAllAirportsNames()).thenReturn(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()));
		when(flightDao.getSourceAirports()).thenReturn(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()));
		
		assertEquals(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()), searchFlightsController.getSources());
	}
	
	@Test
	public void testGetDestinations() {
		when(airportDao.getAllAirportsNames()).thenReturn(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()));
		when(flightDao.getDestinationAirportsFromSource( sourceAirport.getName() )).thenReturn(Arrays.asList(destinationAirport.getName()));
		
		assertEquals(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()), searchFlightsController.getDestinations());
	}
	
	@Test
	public void testGetEnabledSources() {
		when(airportDao.getAllAirportsNames()).thenReturn(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()));
		
		assertEquals(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()), searchFlightsController.getEnabledSources());
	}
	
	@Test
	public void testGetEnabledDestinationsSourceNull() {
		when(airportDao.getAllAirportsNames()).thenReturn(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()));
		assertEquals(Arrays.asList(sourceAirport.getName(), destinationAirport.getName()), searchFlightsController.getEnabledDestinations());
	}
	
	public void testGetEnabledDestinationSourceNotNull() throws InitializationError {
		try {
			FieldUtils.writeField(searchFlightsController, "source", sourceAirport.getName(), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		when(flightDao.getDestinationAirportsFromSource(sourceAirport.getName())).thenReturn(Arrays.asList(destinationAirport.getName()));
		assertEquals(Arrays.asList(destinationAirport.getName()), searchFlightsController.getEnabledDestinations());
	}
	
	@Test
	public void testGetEnabledDate() throws InitializationError {
		try {
			FieldUtils.writeField(searchFlightsController, "source", sourceAirport.getName(), true);
			FieldUtils.writeField(searchFlightsController, "destination", destinationAirport.getName(), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		when(flightDao.getAvailableDateFromSourceToDestination(sourceAirport.getName(), destinationAirport.getName())).thenReturn(Arrays.asList( new Date() ));
		
		assertEquals(Arrays.asList( new Date() ), searchFlightsController.getEnabledDate());
	}
	
	@Test
	public void testGetEnabledDateBack() throws InitializationError {
		try {
			FieldUtils.writeField(searchFlightsController, "source", sourceAirport.getName(), true);
			FieldUtils.writeField(searchFlightsController, "destination", destinationAirport.getName(), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		when(flightDao.getAvailableDateFromSourceToDestination(destinationAirport.getName(), sourceAirport.getName())).thenReturn(Arrays.asList( new Date() ));
		
		assertEquals(Arrays.asList( new Date() ), searchFlightsController.getEnabledDateBack());
	}
	
	@Test
	public void testGetError() {
		assertTrue(searchFlightsController.getError().equals(""));
		
		//Not tested because Mockito cannot mock class String
		/*searchFlightsController.setNocid("5");
		assertTrue(searchFlightsController.getError().contains("session expired"));*/
	}
	
	@Test
	public void testGetFlightsOut() throws InitializationError {
		when(flightDao.getFlights( any(String.class) , any(String.class), any(Date.class) )).thenReturn(Arrays.asList(flight));
		
		try {
			FieldUtils.writeField(searchFlightsController, "source", sourceAirport.getName(), true);
			FieldUtils.writeField(searchFlightsController, "destination", destinationAirport.getName(), true);
			FieldUtils.writeField(searchFlightsController, "departureDate", new Date(), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		assertEquals(Arrays.asList(flight), searchFlightsController.getFlightsOut());
	}
	
	@Test
	public void testEmptyGetFlightsOut() {
		when(flightDao.getFlights( any(String.class) , any(String.class), any(Date.class) )).thenReturn(null);
		
		assertTrue(searchFlightsController.getFlightsOut().isEmpty());
		assertTrue(searchFlightsController.getErrorOut().contains("No flights available"));
	}
	
	@Test
	public void testGetFlightsBack() throws InitializationError {
		try {
			FieldUtils.writeField(searchFlightsController, "oneWay", "return", true);
			FieldUtils.writeField(searchFlightsController, "flightsBack", null, true);
			FieldUtils.writeField(searchFlightsController, "source", sourceAirport.getName(), true);
			FieldUtils.writeField(searchFlightsController, "destination", destinationAirport.getName(), true);
			FieldUtils.writeField(searchFlightsController, "backDate", new Date(), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		when(flightDao.getFlights( any(String.class) , any(String.class), any(Date.class) )).thenReturn(Arrays.asList(flight));
		
		assertEquals(Arrays.asList(flight), searchFlightsController.getFlightsOut());
	}
	
	@Test
	public void testEmptyGetFlightsBack() throws InitializationError {
		try {
			FieldUtils.writeField(searchFlightsController, "oneWay", "return", true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		when(flightDao.getFlights( any(String.class) , any(String.class), any(Date.class) )).thenReturn(null);
		
		assertTrue(searchFlightsController.getFlightsBack().isEmpty());
		assertTrue(searchFlightsController.getErrorBack().contains("No flights available"));
	}
	
	@Test
	public void testIsAvailableTrue() {
		when(temporaryReservationSeatsDao.getTemporaryReservedSeats(flight)).thenReturn(5);
		
		assertTrue(searchFlightsController.isAvailable(flight));
	}
	
	@Test
	public void testIsAvailableFalse() {
		when(temporaryReservationSeatsDao.getTemporaryReservedSeats(flight)).thenReturn(95);
		
		assertFalse(searchFlightsController.isAvailable(flight));
	}
	
	@Test
	public void testAvailableSeats() {
		when(temporaryReservationSeatsDao.getTemporaryReservedSeats(flight)).thenReturn(5);
		
		assertEquals(95, searchFlightsController.availableSeats(flight));
	}
	
	@Test
	public void testIsAvailableStrTrue() {
		when(temporaryReservationSeatsDao.getTemporaryReservedSeats(flight)).thenReturn(5);
		
		assertTrue(searchFlightsController.isAvailableStr(flight).equals(""));
	}
	
	@Test
	public void testIsAvailableStrFalse() {
		when(temporaryReservationSeatsDao.getTemporaryReservedSeats(flight)).thenReturn(95);
		
		assertTrue(searchFlightsController.isAvailableStr(flight).equals("disabled"));
	}
	
	@Test
	public void testExistResult() throws InitializationError {
		try {
			FieldUtils.writeField(searchFlightsController, "flightsOut", Arrays.asList(flight), true);
			FieldUtils.writeField(searchFlightsController, "flightsBack", Arrays.asList(flight), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		assertTrue(searchFlightsController.existResult());
		
		/*-------------------------------------------------------------------------------------------------*/
		try {
			FieldUtils.writeField(searchFlightsController, "flightsOut", null, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		assertTrue(searchFlightsController.existResult());
		
		/*-------------------------------------------------------------------------------------------------*/
		try {
			FieldUtils.writeField(searchFlightsController, "flightsOut", Arrays.asList(flight), true);
			FieldUtils.writeField(searchFlightsController, "flightsBack", null, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		assertTrue(searchFlightsController.existResult());
		
		/*-------------------------------------------------------------------------------------------------*/
		try {
			FieldUtils.writeField(searchFlightsController, "flightsOut", null, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		assertFalse(searchFlightsController.existResult());
	}
	
	@Test
	public void testSearchFlight() {
		assertTrue(searchFlightsController.searchFlight().contains("flights-result"));
	}
	
	@Test
	public void testConfirmFlights() {
		assertTrue(searchFlightsController.confirmFlights().contains("confirm"));
	}
}

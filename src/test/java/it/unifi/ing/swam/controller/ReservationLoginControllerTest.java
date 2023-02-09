package it.unifi.ing.swam.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.bean.ReservationSessionBean;
import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Passenger;
import it.unifi.ing.swam.model.Reservation;

public class ReservationLoginControllerTest {
	
	private ReservationLoginController reservationLoginController;
	
	private ReservationSessionBean reservationSession;
	
	private ReservationDao reservationDao;

	private Reservation reservationData;
	
	private Airport sourceAirport;
	private Airport destinationAirport;
	
	private Flight flightOut;
	private Flight flightBack;
	
	private Passenger passenger;
	
	@Before
	public void setUp() throws InitializationError {
		reservationLoginController = new ReservationLoginController();
		
		reservationDao = mock(ReservationDao.class);
		
		reservationSession = new ReservationSessionBean();
		
		sourceAirport = ModelFactory.airport();
		sourceAirport.setName("Pisa");
		sourceAirport.setZIP(12345);
		
		destinationAirport = ModelFactory.airport();
		destinationAirport.setName("Palermo");
		destinationAirport.setZIP(54321);
		
		flightOut = ModelFactory.flight();
		flightOut.setFlightNumber("F1234");
		flightOut.setDepartureDate( new Date() );
		flightOut.setDepartureTime( new Date() );
		flightOut.setTotalSeats(100);
		flightOut.setPricePerPerson(50);
		flightOut.setFlightDuration(90);
		flightOut.setSourceAirport(sourceAirport);
		flightOut.setDestinationAirport(destinationAirport);
		
		flightBack = ModelFactory.flight();
		flightBack.setFlightNumber("F4321");
		flightBack.setDepartureDate( new Date() );
		flightBack.setDepartureTime( new Date() );
		flightBack.setTotalSeats(100);
		flightBack.setPricePerPerson(60);
		flightBack.setFlightDuration(90);
		flightBack.setSourceAirport(destinationAirport);
		flightBack.setDestinationAirport(sourceAirport);
		
		passenger = ModelFactory.passenger();
		passenger.setFirstName("James");
		passenger.setSurname("Smith");
		passenger.setIdCard("AB12345");
		passenger.setDateOfBirth(new Date());
		
		List<Flight> flights = new ArrayList<Flight>();
		flights.add(flightOut);
		flights.add(flightBack);
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		passengers.add(passenger);
		
		reservationData = ModelFactory.reservation();
		reservationData.setDate( new Date() );
		reservationData.setEmail("mail@example.com");
		reservationData.setFlights(flights);
		reservationData.setPassengers(passengers);
		reservationData.setPrice(110);
		
		try {
			FieldUtils.writeField(reservationData, "id", Long.valueOf(10), true);
			FieldUtils.writeField(reservationLoginController, "reservationDao", reservationDao, true);
			FieldUtils.writeField(reservationLoginController, "reservationSession", reservationSession, true);
			FieldUtils.writeField(reservationSession, "reservationDao", reservationDao, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testLogin() {
		when(reservationDao.searchReservation( any(String.class), any(String.class) )).thenReturn(reservationData);
		when(reservationDao.findById( any(Long.class))).thenReturn(reservationData);
		
		String result = reservationLoginController.login();
		
		assertTrue(result.contains("show-reservation"));
		assertEquals(reservationData.getId(), reservationSession.getId());
		assertTrue(reservationSession.isLoggedIn());
	}
	
	@Test
	public void testLoginError() {
		when(reservationDao.searchReservation( any(String.class), any(String.class) )).thenReturn(null);
		
		assertTrue(reservationLoginController.login().contains("mybooking-login"));
	}
	
	@Test
	public void testLogout() {
		when(reservationDao.searchReservation( any(String.class), any(String.class) )).thenReturn(reservationData);
		when(reservationDao.findById( any(Long.class))).thenReturn(reservationData);
		
		reservationLoginController.login();
		String result = reservationLoginController.logout();
		
		assertTrue(result.contains("index"));
		assertNull(reservationSession.getId());
		assertFalse(reservationSession.isLoggedIn());
	}
}
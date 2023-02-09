package it.unifi.ing.swam.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.bean.ReservationSessionBean;
import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Passenger;
import it.unifi.ing.swam.model.Reservation;

public class ManageReservationControllerTest {
	
	private ManageReservationController manageReservationController;
	
	private ReservationDao reservationDao;
	
	private FlightDao flightDao;
	
	private ReservationSessionBean reservationSession;
	
	private Reservation reservation;
	
	private Passenger passenger;
	
	private Flight flightOut;
	
	private Flight flightBack;
	
	private Airport sourceAirport;
	
	private Airport destinationAirport;
	
	@Before
	public void setUp() throws InitializationError {
		manageReservationController = new ManageReservationController();
		
		reservationDao = mock(ReservationDao.class);
		
		flightDao = mock(FlightDao.class);
		
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
		
		reservation = ModelFactory.reservation();
		reservation.setDate( new Date() );
		reservation.setEmail("mail@example.com");
		reservation.setFlights(Arrays.asList(flightOut, flightBack));
		reservation.setPassengers(Arrays.asList(passenger));
		reservation.setPrice(160);
		
		reservationSession.setReservation(reservation);
		
		try {
			FieldUtils.writeField(flightOut, "id", Long.valueOf(10), true);
			FieldUtils.writeField(flightBack, "id", Long.valueOf(20), true);
			FieldUtils.writeField(manageReservationController, "reservationDao", reservationDao, true);
			FieldUtils.writeField(manageReservationController, "flightDao", flightDao, true);
			FieldUtils.writeField(manageReservationController, "reservation", reservation, true);
			FieldUtils.writeField(reservationSession, "reservationDao", reservationDao, true);
			FieldUtils.writeField(manageReservationController, "reservationSession", reservationSession, true);
			FieldUtils.writeField(manageReservationController, "passengers", Arrays.asList(passenger), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testGetReservation() {
		when(reservationDao.findById(Long.valueOf(10))).thenReturn(reservation);
		
		reservationSession.setId(Long.valueOf(10));
		
		assertEquals(reservation, manageReservationController.getReservation());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetReservationBadIdFormat() throws InitializationError {
		try {
			FieldUtils.writeField(reservationSession, "id", "hello", true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
		
		manageReservationController.getReservation();
	}
	
	@Test
	public void testUpdatePassengers() {
		String result = manageReservationController.updatePassengers();
		
		assertTrue(result.contains("update-passengers-success"));
		assertTrue(manageReservationController.isJustUpdatePassengers());
		
		verify(reservationDao, times(1)).save(reservation);
	}
	
	@Test
	public void testDeleteReservation() {
		when(flightDao.findById(Long.valueOf(10))).thenReturn(flightOut);
		when(flightDao.findById(Long.valueOf(20))).thenReturn(flightBack);
		
		assertTrue(manageReservationController.deleteReservation().contains("delete-reservation-success"));
		
		verify(flightDao, times(1)).save(flightOut);
		verify(flightDao, times(1)).save(flightBack);
		verify(reservationDao, times(1)).delete(reservation);
		
	}
}

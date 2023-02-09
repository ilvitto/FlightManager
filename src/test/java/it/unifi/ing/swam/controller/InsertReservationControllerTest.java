package it.unifi.ing.swam.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.dao.TemporaryReservationSeatsDao;
import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Passenger;
import it.unifi.ing.swam.model.Reservation;
import it.unifi.ing.swam.model.strategy.AllNightLong;
import it.unifi.ing.swam.model.strategy.CrazyWednesday;
import it.unifi.ing.swam.model.strategy.NoDiscount;
import it.unifi.ing.swam.model.temp.TemporaryReservationSeats;

public class InsertReservationControllerTest {
	
	private InsertReservationController insertReservationController;
	
	private ReservationDao reservationDao;
	
	private FlightDao flightDao;
	
	private TemporaryReservationSeatsDao temporaryReservationSeatsDao;
	
	private Reservation reservation;
	
	private TemporaryReservationSeats trsOut;
	
	private TemporaryReservationSeats trsBack;
	
	private Passenger passenger;
	
	private Flight flightOut;
	
	private Flight flightBack;
	
	private Airport sourceAirport;
	
	private Airport destinationAirport;
	
	@Before
	public void setUp() throws InitializationError {
		insertReservationController = new InsertReservationController();
		
		reservationDao = mock(ReservationDao.class);
		
		flightDao = mock(FlightDao.class);
		
		temporaryReservationSeatsDao = mock(TemporaryReservationSeatsDao.class);
		
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
		
		trsOut = ModelFactory.temporaryReservationSeats();
		trsOut.setDate( new Date() );
		trsOut.setFlight(flightOut);
		trsOut.setnPassengers(1);
		
		trsBack = ModelFactory.temporaryReservationSeats();
		trsBack.setDate( new Date() );
		trsBack.setFlight(flightBack);
		trsBack.setnPassengers(1);
		
		try {
			
			FieldUtils.writeField(insertReservationController, "reservationDao", reservationDao, true);
			FieldUtils.writeField(insertReservationController, "flightDao", flightDao, true);
			FieldUtils.writeField(insertReservationController, "temporaryReservationSeatsDao", temporaryReservationSeatsDao, true);
			FieldUtils.writeField(insertReservationController, "flightOut", Long.valueOf(10), true);
			FieldUtils.writeField(insertReservationController, "flightBack", Long.valueOf(20), true);
			FieldUtils.writeField(insertReservationController, "nPassengers", 1, true);
			FieldUtils.writeField(reservation, "id", Long.valueOf(100), true);
			FieldUtils.writeField(insertReservationController, "reservation", reservation, true);
			FieldUtils.writeField(insertReservationController, "temporaryReservationSeatsList", Arrays.asList(Long.valueOf(500), Long.valueOf(600)), true);
			FieldUtils.writeField(trsOut, "id", Long.valueOf(500), true);
			FieldUtils.writeField(trsBack, "id", Long.valueOf(600), true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testGetReservation() throws IllegalAccessException {
		FieldUtils.writeField(insertReservationController, "reservation", null, true);
		FieldUtils.writeField(insertReservationController, "strategy", new NoDiscount(), true);
		
		when(flightDao.findById(Long.valueOf(10))).thenReturn(flightOut);
		when(flightDao.findById(Long.valueOf(20))).thenReturn(flightBack);
		
		Reservation result = insertReservationController.getReservation();
		
		assertTrue(result instanceof Reservation);
		assertNull(result.getEmail());
		assertEquals(2, result.getFlights().size());
		
		FieldUtils.writeField(insertReservationController, "reservation", reservation, true);
	}
	
	@Test
	public void testGetFlights() {
		when(flightDao.findById(Long.valueOf(10))).thenReturn(flightOut);
		when(flightDao.findById(Long.valueOf(20))).thenReturn(flightBack);
		
		List<Flight> result = insertReservationController.getFlights();
		
		assertEquals(2, result.size());
		assertEquals(Arrays.asList(flightOut, flightBack), result);
	}
	
	@Test
	public void testGetForwardFlight() {
		when(flightDao.findById(Long.valueOf(10))).thenReturn(flightOut);
		
		assertEquals(flightOut, insertReservationController.getForwardFlight());
	}
	
	@Test
	public void testGetReturnFlight() {
		when(flightDao.findById(Long.valueOf(20))).thenReturn(flightBack);
		
		assertEquals(flightBack, insertReservationController.getReturnFlight());
	}
	
	@Test
	public void testIsDiscounted() throws IllegalAccessException {
		FieldUtils.writeField(insertReservationController, "strategy", new NoDiscount(), true);
		assertFalse(insertReservationController.isDiscounted());
	}
	
	@Test
	public void testUpdateReservedSeats() {
		insertReservationController.updateReservedSeats();
		
		assertEquals(1, insertReservationController.getReservation().getFlights().get(0).getReservedSeats());
		assertEquals(1, insertReservationController.getReservation().getFlights().get(1).getReservedSeats());
		
		verify(flightDao, times(1)).save(flightOut);
		verify(flightDao, times(1)).save(flightBack);
	}
	
	@Test
	public void testReserveSeats() {
		when(temporaryReservationSeatsDao.getLastAdded()).thenReturn(trsOut);
		
		insertReservationController.reserveSeats();
		
		assertEquals(2, insertReservationController.getTemporaryReservationSeatsList().size());
	}
	
	@Test
	public void testSaveReservation() {
		when(flightDao.findById(Long.valueOf(10))).thenReturn(flightOut);
		when(flightDao.findById(Long.valueOf(20))).thenReturn(flightBack);
		when(reservationDao.getIdFromLastReservation()).thenReturn(reservation.getId() - 1);
		
		String result = insertReservationController.saveReservation();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime( new Date() );
		
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
			assertTrue(insertReservationController.getStrategy() instanceof CrazyWednesday);
		else if(cal.get(Calendar.HOUR_OF_DAY) < 6)
			assertTrue(insertReservationController.getStrategy() instanceof AllNightLong);
		else
			assertTrue(insertReservationController.getStrategy() instanceof NoDiscount);
		
		assertTrue(result.contains("summary"));
		
		verify(reservationDao, times(1)).save(reservation);
		
		verify(temporaryReservationSeatsDao, times(1)).delete(Long.valueOf(500));
		verify(temporaryReservationSeatsDao, times(1)).delete(Long.valueOf(600));
	}
	
	@Test
	public void testCleanTemporaryReservations() {
		insertReservationController.cleanTemporaryReservations();
		
		verify(temporaryReservationSeatsDao, times(1)).delete(Long.valueOf(500));
		verify(temporaryReservationSeatsDao, times(1)).delete(Long.valueOf(600));
		
		verify(temporaryReservationSeatsDao, times(1)).cleanExpiredTemporaryReservation(10);
	}
	
}

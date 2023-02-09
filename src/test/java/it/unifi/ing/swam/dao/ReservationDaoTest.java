package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Passenger;
import it.unifi.ing.swam.model.Reservation;

public class ReservationDaoTest extends JpaTest {
	
	private ReservationDao reservationDao;
	private Reservation reservation;
	private Passenger passenger;
	private Flight flightOut;
	private Flight flightBack;
	private Airport sourceAirport;
	private Airport destinationAirport;
	
	@Override
	protected void init() throws InitializationError {
		
		sourceAirport = ModelFactory.airport();
		sourceAirport.setName("Pisa");
		sourceAirport.setZIP(12345);
		
		entityManager.persist(sourceAirport);
		
		destinationAirport = ModelFactory.airport();
		destinationAirport.setName("Palermo");
		destinationAirport.setZIP(54321);
		
		entityManager.persist(destinationAirport);
		
		flightOut = ModelFactory.flight();
		flightOut.setFlightNumber("F1234");
		flightOut.setDepartureDate( new Date() );
		flightOut.setDepartureTime( new Date() );
		flightOut.setTotalSeats(100);
		flightOut.setPricePerPerson(50);
		flightOut.setFlightDuration(90);
		flightOut.setSourceAirport(sourceAirport);
		flightOut.setDestinationAirport(destinationAirport);
		
		entityManager.persist(flightOut);
		
		flightBack = ModelFactory.flight();
		flightBack.setFlightNumber("F4321");
		flightBack.setDepartureDate( new Date() );
		flightBack.setDepartureTime( new Date() );
		flightBack.setTotalSeats(100);
		flightBack.setPricePerPerson(60);
		flightBack.setFlightDuration(90);
		flightBack.setSourceAirport(destinationAirport);
		flightBack.setDestinationAirport(sourceAirport);
		
		entityManager.persist(flightBack);
		
		passenger = ModelFactory.passenger();
		passenger.setFirstName("James");
		passenger.setSurname("Smith");
		passenger.setIdCard("AB12345");
		passenger.setDateOfBirth(new Date());
		
		entityManager.persist(passenger);
		
		List<Flight> flights = new ArrayList<Flight>();
		flights.add(flightOut);
		flights.add(flightBack);
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		passengers.add(passenger);
		
		reservation = ModelFactory.reservation();
		reservation.setDate( new Date() );
		reservation.setEmail("mail@example.com");
		reservation.setFlights(flights);
		reservation.setPassengers(passengers);
		reservation.setPrice(160);
		
		entityManager.persist(reservation);
		
		reservationDao = new ReservationDao();
		
		try {
			FieldUtils.writeField(reservationDao, "entityManager", entityManager, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testSave() {
		List<Flight> flights = new ArrayList<Flight>();
		flights.add(flightBack);
		flights.add(flightOut);
		entityManager.merge(flightBack);
		entityManager.merge(flightOut);
		
		Passenger anotherPassenger = ModelFactory.passenger();
		anotherPassenger.setFirstName("Bob");
		anotherPassenger.setSurname("Smith");
		anotherPassenger.setIdCard("AB54321");
		anotherPassenger.setDateOfBirth(new Date());
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		passengers.add(anotherPassenger);
		
		Reservation anotherReservation = ModelFactory.reservation();
		anotherReservation.setDate( new Date() );
		anotherReservation.setEmail("mail2@example2.com");
		anotherReservation.setFlights(flights);
		anotherReservation.setPassengers(passengers);
		anotherReservation.setPrice(160);
		
		reservationDao.save(anotherReservation);
		
		assertEquals(anotherReservation, entityManager
				.createQuery("from Reservation where uuid = :uuid")
				.setParameter("uuid", anotherReservation.getUuid())
				.getSingleResult());
	}
	
	@Test
	public void testFindById() {
		Reservation result = reservationDao.findById(reservation.getId());
		
		assertEquals(reservation, result);
	}
	
	@Test
	public void testFindByWrongId() {
		Reservation result = reservationDao.findById(new Long(9999));
		
		assertNull(result);
	}
	
	@Test
	public void testDelete() {
		reservationDao.delete(reservation);
		
		assertNull(reservationDao.findById(reservation.getId()));
	}
	
	@Test
	public void testGetIdFromLastReservation() {
		assertEquals(new Long(1), reservationDao.getIdFromLastReservation());
	}
	
	@Test
	public void testEmptyGetIdFromLastReservation() {
		reservationDao.delete(reservation);
		assertEquals(new Long(0), reservationDao.getIdFromLastReservation());
	}
	
	@Test
	public void testSearchReservation() {
		List<Flight> flights = new ArrayList<Flight>();
		flights.add(flightBack);
		flights.add(flightOut);
		entityManager.merge(flightBack);
		entityManager.merge(flightOut);
		
		Passenger anotherPassenger = ModelFactory.passenger();
		anotherPassenger.setFirstName("Bob");
		anotherPassenger.setSurname("Smith");
		anotherPassenger.setIdCard("AB54321");
		anotherPassenger.setDateOfBirth(new Date());
		
		List<Passenger> passengers = new ArrayList<Passenger>();
		passengers.add(anotherPassenger);
		
		Long lastId = reservationDao.getIdFromLastReservation();
		
		Reservation anotherReservation = ModelFactory.reservation();
		anotherReservation.setDate( new Date() );
		anotherReservation.setEmail("mail2@example2.com");
		anotherReservation.setFlights(flights);
		anotherReservation.setPassengers(passengers);
		anotherReservation.setReservationId("FMID-" + (lastId + 1));
		anotherReservation.setPrice(160);
		
		reservationDao.save(anotherReservation);
		
		assertEquals(anotherReservation, reservationDao.searchReservation(anotherReservation.getReservationId(),
																			anotherReservation.getEmail()));
	}
	
	@Test
	public void testEmptySearchReservation() {
		assertNull(reservationDao.searchReservation("FMID-9999", "inexistent@email.com"));
	}
}

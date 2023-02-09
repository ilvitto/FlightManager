package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.temp.TemporaryReservationSeats;

public class TemporaryReservationSeatsDaoTest extends JpaTest {
	//TODO: 
	
	private TemporaryReservationSeatsDao temporaryReservationSeatsDao;
	private TemporaryReservationSeats temporaryReservationSeats;
	
	private Airport sourceAirport;
	private Airport destinationAirport;
	private Flight flight;
	
	@Override
	protected void init() throws InitializationError {
		
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
		
		temporaryReservationSeats = ModelFactory.temporaryReservationSeats();
		temporaryReservationSeats.setDate( new Date() );
		temporaryReservationSeats.setnPassengers(5);
		temporaryReservationSeats.setFlight(flight);
		
		entityManager.persist(sourceAirport);
		entityManager.persist(destinationAirport);
		entityManager.persist(flight);
		entityManager.persist(temporaryReservationSeats);
		
		temporaryReservationSeatsDao = new TemporaryReservationSeatsDao();
		
		try {
			FieldUtils.writeField(temporaryReservationSeatsDao, "entityManager", entityManager, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testSave() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F4321");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(50);
		anotherFlight.setPricePerPerson(60);
		anotherFlight.setFlightDuration(120);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		this.entityManager.persist(anotherFlight);
		
		TemporaryReservationSeats anotherTemporaryReservationSeats = ModelFactory.temporaryReservationSeats();
		anotherTemporaryReservationSeats.setDate( new Date() );
		anotherTemporaryReservationSeats.setnPassengers(10);
		anotherTemporaryReservationSeats.setFlight(anotherFlight);
		
		temporaryReservationSeatsDao.save(anotherTemporaryReservationSeats);
		
		assertEquals(anotherTemporaryReservationSeats, entityManager
				.createQuery("from TemporaryReservationSeats where uuid = :uuid")
				.setParameter("uuid", anotherTemporaryReservationSeats.getUuid())
				.getSingleResult());
	}
	
	@Test
	public void testFindById() {
		TemporaryReservationSeats result = temporaryReservationSeatsDao.findById(temporaryReservationSeats.getId());
		
		assertEquals(temporaryReservationSeats, result);
	}
	
	@Test
	public void testFindByWrongId() {
		assertNull(temporaryReservationSeatsDao.findById(Long.valueOf(9999)));
	}
	
	@Test
	public void testDelete() {
		temporaryReservationSeatsDao.delete(temporaryReservationSeats);
		
		assertNull(temporaryReservationSeatsDao.findById(temporaryReservationSeats.getId()));
	}
	
	@Test
	public void testGetTemporaryReservedSeats() {
		int result = temporaryReservationSeatsDao.getTemporaryReservedSeats(flight);
		
		assertEquals(5, result);
	}
	
	@Test
	public void testGetWrongTemporaryReservedSeats() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F4321");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(50);
		anotherFlight.setPricePerPerson(60);
		anotherFlight.setFlightDuration(120);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		int result = temporaryReservationSeatsDao.getTemporaryReservedSeats(anotherFlight);
		
		assertEquals(0, result);
	}
	
	@Test
	public void testCleanExpiredTemporaryReservation() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -15);
		
		TemporaryReservationSeats anotherTemporaryReservationSeats = ModelFactory.temporaryReservationSeats();
		anotherTemporaryReservationSeats.setDate( cal.getTime() );
		anotherTemporaryReservationSeats.setnPassengers(10);
		anotherTemporaryReservationSeats.setFlight(flight);
		
		temporaryReservationSeatsDao.save(anotherTemporaryReservationSeats);
		
		temporaryReservationSeatsDao.cleanExpiredTemporaryReservation(10);
		
		assertNull(temporaryReservationSeatsDao.findById(anotherTemporaryReservationSeats.getId()));
	}
}

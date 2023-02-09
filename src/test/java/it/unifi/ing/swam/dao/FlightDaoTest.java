package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;

public class FlightDaoTest extends JpaTest {
	private Flight flight;
	private Airport sourceAirport;
	private Airport destinationAirport;
	private FlightDao flightDao;
	
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
		
		entityManager.persist(sourceAirport);
		entityManager.persist(destinationAirport);
		entityManager.persist(flight);
		
		flightDao = new FlightDao();
		
		try {
			FieldUtils.writeField(flightDao, "entityManager", entityManager, true);
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
		
		flightDao.save(anotherFlight);
		
		assertEquals(anotherFlight, entityManager
								.createQuery("from Flight where uuid = :uuid", Flight.class)
								.setParameter("uuid", anotherFlight.getUuid())
								.getSingleResult());
	}
	
	@Test
	public void testFindById() {
		Flight result = flightDao.findById(flight.getId());
		
		assertEquals(flight, result);
	}
	
	@Test
	public void testFindByWrongId() {
		Flight result = flightDao.findById(new Long(9999));
		
		assertNull(result);
	}
	
	@Test
	public void testDelete() {
		flightDao.delete(flight);
		
		assertNull(flightDao.findById(flight.getId()));
	}
	
	@Test
	public void testFindByFlightNumber() {
		Flight result = flightDao.findByFlightNumber(flight.getFlightNumber());
		
		assertEquals(flight, result);
	}
	
	@Test
	public void testFindByWrongFlightNumber() {
		Flight result = flightDao.findByFlightNumber("F2345");
		
		assertNull(result);
	}
	
	@Test
	public void testGetAllFlights() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F4321");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(50);
		anotherFlight.setPricePerPerson(60);
		anotherFlight.setFlightDuration(120);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		List<Flight> result = flightDao.getAllFlights( new Date() );
		
		assertEquals(2, result.size());
		assertEquals(flight, result.get(0));
		assertEquals(anotherFlight, result.get(1));
	}
	
	@Test
	public void testEmptyGetAllFlights() {
		flightDao.delete(flight);
		
		List<Flight> result = flightDao.getAllFlights( new Date() );
		
		assertNull(result);
	}
	
	@Test
	public void testGetFlightsFromDate() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F4321");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(50);
		anotherFlight.setPricePerPerson(60);
		anotherFlight.setFlightDuration(120);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		List<Flight> result = flightDao.getFlightsFromDate(sourceAirport, destinationAirport, new Date());
		
		assertEquals(1, result.size());
		assertEquals(flight, result.get(0));
	}
	
	@Test
	public void testEmptyGetFlightsFromDate() {
		flightDao.delete(flight);
		
		List<Flight> result = flightDao.getFlightsFromDate(sourceAirport, destinationAirport, new Date());
		
		assertNull(result);
	}
	
	@Test
	public void testGetFlightsWithDate() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		List<Flight> result = flightDao.getFlights(destinationAirport, sourceAirport, anotherFlight.getDepartureDate());
		
		assertEquals(1, result.size());
		assertEquals(anotherFlight, result.get(0));
	}
	
	@Test
	public void testEmptyGetFlightsWithDate() {
		flightDao.delete(flight);
		
		Date tomorrow = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(tomorrow); 
		c.add(Calendar.DATE, 1);
		tomorrow = c.getTime();
		
		assertNull(flightDao.getFlights(sourceAirport, destinationAirport, tomorrow));
	}
	
	@Test
	public void testGetFlightsStringWithDate() throws ParseException {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(flight.getDepartureDate()));
		
		List<Flight> result = flightDao.getFlights(sourceAirport.getName(), destinationAirport.getName(), date);
				
		assertEquals(1, result.size());
		assertEquals(flight, result.get(0));
	}
	
	@Test
	public void testEmptyGetFlightsStringWithDate() {
		flightDao.delete(flight);
		
		Date tomorrow = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(tomorrow); 
		c.add(Calendar.DATE, 1);
		tomorrow = c.getTime();
		
		assertNull(flightDao.getFlights(sourceAirport.getName(), destinationAirport.getName(), tomorrow));
	}
	
	@Test
	public void testGetFlights() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		List<Flight> result = flightDao.getFlights(destinationAirport, sourceAirport, new Date());
		
		assertEquals(1, result.size());
		assertEquals(anotherFlight, result.get(0));
	}
	
	@Test
	public void testEmptyGetFlights() {
		flightDao.delete(flight);
		
		assertNull(flightDao.getFlights(destinationAirport, sourceAirport, new Date()));
	}
	
	@Test
	public void testGetFlightsFromDestination() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(sourceAirport);
		anotherFlight.setDestinationAirport(destinationAirport);
		
		flightDao.save(anotherFlight);
		
		List<Flight> result = flightDao.getFlightsFromDestination(destinationAirport, new Date());
		
		assertEquals(2, result.size());
		assertEquals(flight, result.get(0));
		assertEquals(anotherFlight, result.get(1));
	}
	
	@Test
	public void testEmptyGetFlightsFromDestination() {
		List<Flight> result = flightDao.getFlightsFromDestination(sourceAirport, new Date());
		
		assertNull(result);
	}
	
	@Test
	public void testGetFlightsFromSource() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(sourceAirport);
		anotherFlight.setDestinationAirport(destinationAirport);
		
		flightDao.save(anotherFlight);
		
		List<Flight> result = flightDao.getFlightsFromSource(sourceAirport, new Date());
		
		assertEquals(2, result.size());
		assertEquals(flight, result.get(0));
		assertEquals(anotherFlight, result.get(1));
	}
	
	@Test
	public void testEmptyGetFlightsFromSource() {
		List<Flight> result = flightDao.getFlightsFromSource(destinationAirport, new Date());
		
		assertNull(result);
	}
	
	@Test
	public void testGetSourceAirports() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		//Results are given in ascendent order (i.e. First Palermo, Second Pisa)
		List<String> result = flightDao.getSourceAirports();
		
		assertEquals(2, result.size());
		assertEquals(flight.getSourceAirport().getName(), result.get(1));
		assertEquals(anotherFlight.getSourceAirport().getName(), result.get(0));
	}
	
	@Test
	public void testEmptyGetSourceAirports() {
		flightDao.delete(flight);
		List<String> result = flightDao.getSourceAirports();
		
		assertNull(result);
	}
	
	@Test
	public void testGetDestinationAirports() {
		Flight anotherFlight = ModelFactory.flight();
		anotherFlight.setFlightNumber("F3456");
		anotherFlight.setDepartureDate( new Date() );
		anotherFlight.setDepartureTime( new Date() );
		anotherFlight.setTotalSeats(120);
		anotherFlight.setPricePerPerson(70);
		anotherFlight.setFlightDuration(100);
		anotherFlight.setSourceAirport(destinationAirport);
		anotherFlight.setDestinationAirport(sourceAirport);
		
		flightDao.save(anotherFlight);
		
		//Results are given in ascendent order (i.e. First Palermo, Second Pisa)
		List<String> result = flightDao.getDestinationAirports();
		
		assertEquals(2, result.size());
		assertEquals(flight.getSourceAirport().getName(), result.get(1));
		assertEquals(anotherFlight.getSourceAirport().getName(), result.get(0));
	}
	
	@Test
	public void testEmptyGetDestinationAirports() {
		flightDao.delete(flight);
		List<String> result = flightDao.getDestinationAirports();
		
		assertNull(result);
	}
	
	@Test
	public void testGetDestinationAirportsFromSource() {
		List<String> result = flightDao.getDestinationAirportsFromSource(sourceAirport.getName());
		
		assertEquals(1, result.size());
		assertEquals(destinationAirport.getName(), result.get(0));
	}
	
	@Test
	public void testEmptyGetDestinationAirportsFromSource() {
		List<String> result = flightDao.getDestinationAirportsFromSource(destinationAirport.getName());
		
		assertNull(result);
	}
	
	@Test
	public void testGetAvailableDateFromSourceToDestination() {
		List<Date> result = flightDao.getAvailableDateFromSourceToDestination(sourceAirport.getName(), destinationAirport.getName());
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String expected = formatter.format(flight.getDepartureDate());
		
		assertEquals(1, result.size());
		assertEquals(expected, result.get(0).toString());
	}
	
	@Test
	public void testEmptyGetAvailableDateFromSourceToDestination() {
		List<Date> result = flightDao.getAvailableDateFromSourceToDestination(destinationAirport.getName(), sourceAirport.getName());
		
		assertNull(result);
	}
}

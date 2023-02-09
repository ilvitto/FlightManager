package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.ModelFactory;

public class AirportDaoTest extends JpaTest {
	
	private AirportDao airportDao;
	private Airport airport;
	
	@Override
	protected void init() throws InitializationError {
		airport = ModelFactory.airport();
		airport.setName("Palermo");
		airport.setZIP(12345);
		
		entityManager.persist(airport);
		
		airportDao = new AirportDao();
		
		try {
			FieldUtils.writeField(airportDao, "entityManager", entityManager, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testSave() {
		Airport anotherAirport = ModelFactory.airport();
		anotherAirport.setName("Pisa");
		anotherAirport.setZIP(54321);

		airportDao.save(anotherAirport);
		
		assertEquals(anotherAirport, entityManager
				.createQuery("from Airport where uuid = :uuid", Airport.class)
				.setParameter("uuid", anotherAirport.getUuid())
				.getSingleResult());
	}
	
	@Test
	public void testFindById() {
		Airport result = airportDao.findById(airport.getId());
		
		assertEquals(airport, result);
	}
	
	@Test
	public void testFindByWrongId() {
		Airport result = airportDao.findById(new Long(9999));
		
		assertNull(result);
	}
	
	@Test
	public void testDelete() {
		airportDao.delete(airport);
		
		assertNull(airportDao.findById(airport.getId()));
	}
	
	@Test
	public void testGetAllAirports() {
		Airport anotherAirport = ModelFactory.airport();
		anotherAirport.setName("Pisa");
		anotherAirport.setZIP(54321);

		airportDao.save(anotherAirport);
		
		List<Airport> result = airportDao.getAllAirports();
		
		assertEquals(2, result.size());
		assertEquals(airport, result.get(0));
		assertEquals(anotherAirport, result.get(1));
	}
	
	@Test
	public void testEmptyGetAllAirports() {
		airportDao.delete(airport);
		
		List<Airport> result = airportDao.getAllAirports();
		
		assertNull(result);
	}
	
	@Test
	public void testGetAllAirportsName() {
		Airport anotherAirport = ModelFactory.airport();
		anotherAirport.setName("Pisa");
		anotherAirport.setZIP(54321);

		airportDao.save(anotherAirport);
		
		List<Airport> result = airportDao.getAllAirports();
		
		assertEquals(2, result.size());
		assertEquals(airport, result.get(0));
		assertEquals(anotherAirport, result.get(1));
	}
	
	@Test
	public void testEmptyGetAllAirportsName() {
		airportDao.delete(airport);
		
		List<String> result = airportDao.getAllAirportsNames();
		
		assertNull(result);
	}
	
	@Test
	public void testGetAirport() {
		Airport result = airportDao.getAirport(airport);
		
		assertEquals(airport, result);
	}
	
	@Test
	public void testGetWrongAirport() {
		Airport anotherAirport = ModelFactory.airport();
		anotherAirport.setName("Pisa");
		anotherAirport.setZIP(54321);
		
		Airport result = airportDao.getAirport(anotherAirport);
		
		assertNull(result);
	}
}

package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Passenger;

public class PassengerDaoTest extends JpaTest {
	
	private PassengerDao passengerDao;
	private Passenger passenger;
	
	@Override
	protected void init() throws InitializationError {
		passenger = ModelFactory.passenger();
		passenger.setFirstName("James");
		passenger.setSurname("Smith");
		passenger.setIdCard("AB12345");
		passenger.setDateOfBirth(new Date());
		
		entityManager.persist(passenger);
		
		passengerDao = new PassengerDao();
		
		try {
			FieldUtils.writeField(passengerDao, "entityManager", entityManager, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testSave() {
		Passenger anotherPassenger = ModelFactory.passenger();
		anotherPassenger.setFirstName("Bob");
		anotherPassenger.setSurname("Smith");
		anotherPassenger.setIdCard("AB54321");
		anotherPassenger.setDateOfBirth(new Date());
		
		passengerDao.save(anotherPassenger);
		
		assertEquals(anotherPassenger, entityManager
				.createQuery("from Passenger where uuid = :uuid", Passenger.class)
				.setParameter("uuid", anotherPassenger.getUuid())
				.getSingleResult());
	}
	
	@Test
	public void testFindById() {
		Passenger result = passengerDao.findById(passenger.getId());
		
		assertEquals(passenger, result);
	}
	
	@Test
	public void testFindByWrongId() {
		Passenger result = passengerDao.findById(new Long(9999));
		
		assertNull(result);
	}
	
	@Test
	public void testDelete() {
		passengerDao.delete(passenger);
		
		assertNull(passengerDao.findById(passenger.getId()));
	}
}

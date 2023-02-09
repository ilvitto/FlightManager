package it.unifi.ing.swam.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BaseEntityTest {
	
	private Airport airport;
	
	@Before
	public void setUp() {
		airport = ModelFactory.airport();
	}
	
	@Test
	public void testBaseEntityEquals() {
		assertTrue(airport.equals(airport));
		assertFalse(airport.equals(null));
		
		Airport anotherAirport = ModelFactory.airport();
		assertFalse(airport.equals(anotherAirport));
		
		anotherAirport = new Airport(airport.getUuid());
		assertTrue(airport.equals(anotherAirport));
		
		assertFalse(airport.equals(Long.valueOf(10)));
	}
}

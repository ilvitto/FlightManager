package it.unifi.ing.swam.bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Reservation;

public class ReservationSessionBeanTest {
	
	private ReservationSessionBean reservationSessionBean;
	
	private ReservationDao reservationDao;
	
	private Reservation reservation;
	
	@Before
	public void setUp() throws InitializationError {
		
		reservationSessionBean = new ReservationSessionBean();
		
		reservationDao = mock(ReservationDao.class);
		
		reservation = ModelFactory.reservation();
		
		try {
			FieldUtils.writeField(reservationSessionBean, "reservationDao", reservationDao, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testIsLoggedIn() {
		assertFalse(reservationSessionBean.isLoggedIn());
		
		when(reservationDao.findById(Long.valueOf(10))).thenReturn(reservation);
		reservationSessionBean.setId(Long.valueOf(10));
		assertTrue(reservationSessionBean.isLoggedIn());
	}

}

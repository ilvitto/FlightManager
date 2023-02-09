package it.unifi.ing.swam.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.bean.AdministratorSessionBean;
import it.unifi.ing.swam.dao.AdministratorDao;
import it.unifi.ing.swam.model.Administrator;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.util.Util;

public class AdministratorLoginControllerTest {
	
	private AdministratorLoginController administratorLoginController;
	
	private AdministratorSessionBean administratorSession;
	
	private AdministratorDao administratorDao;

	private Administrator administrator;
	
	
	@Before
	public void setUp() throws InitializationError {
		administratorLoginController = new AdministratorLoginController();
		
		administratorDao = mock(AdministratorDao.class);
		
		administratorSession = new AdministratorSessionBean();
		
		administrator = ModelFactory.administrator();
		administrator.setUsername("admin");
		administrator.setPassword(Util.digest("examplePassword"));
		
		try {
			FieldUtils.writeField(administrator, "id", Long.valueOf(10), true);
			FieldUtils.writeField(administratorLoginController, "administratorDao", administratorDao, true);
			FieldUtils.writeField(administratorLoginController, "administratorSession", administratorSession, true);
			FieldUtils.writeField(administratorSession, "administratorDao", administratorDao, true);
			FieldUtils.writeField(administratorLoginController, "administratorData", administrator, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testLogin() {
		when(administratorDao.login( any(Administrator.class) )).thenReturn(administrator);
		when(administratorDao.findById( any(Long.class) )).thenReturn(administrator);
		
		String result = administratorLoginController.login();
		
		assertTrue(result.contains("insert-flights"));
		assertEquals(administrator.getId(), administratorSession.getAdministratorId());
		assertTrue(administratorSession.isLoggedIn());
	}
	
	@Test
	public void testLoginError() {
		when(administratorDao.login( any(Administrator.class) )).thenReturn(null);
		
		assertTrue(administratorLoginController.login().contains("administrator-login"));
	}
	
	@Test
	public void testLogout() {
		when(administratorDao.login( any(Administrator.class) )).thenReturn(administrator);
		when(administratorDao.findById( any(Long.class) )).thenReturn(administrator);
		
		administratorLoginController.login();
		String result = administratorLoginController.logout();
		
		assertTrue(result.contains("index"));
		assertNull(administratorSession.getAdministratorId());
		assertFalse(administratorSession.isLoggedIn());
	}
}

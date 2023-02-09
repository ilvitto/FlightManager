package it.unifi.ing.swam.bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.dao.AdministratorDao;
import it.unifi.ing.swam.model.Administrator;
import it.unifi.ing.swam.model.ModelFactory;

public class AdministratorSessionBeanTest {
	
	private AdministratorSessionBean administratorSessionBean;
	
	private AdministratorDao administratorDao;
	
	private Administrator administrator;
	
	@Before
	public void setUp() throws InitializationError {
		
		administratorSessionBean = new AdministratorSessionBean();
		
		administratorDao = mock(AdministratorDao.class);
		
		administrator = ModelFactory.administrator();
		administrator.setUsername("user");
		administrator.setPassword("pass");
		
		try {
			FieldUtils.writeField(administratorSessionBean, "administratorDao", administratorDao, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testIsLoggedIn() {
		assertFalse(administratorSessionBean.isLoggedIn());
		
		when(administratorDao.findById(Long.valueOf(10))).thenReturn(administrator);
		administratorSessionBean.setAdministratorId(Long.valueOf(10));
		assertTrue(administratorSessionBean.isLoggedIn());
	}
}

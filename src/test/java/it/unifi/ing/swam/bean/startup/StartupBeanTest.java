package it.unifi.ing.swam.bean.startup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.dao.AdministratorDao;
import it.unifi.ing.swam.model.Administrator;
import it.unifi.ing.swam.model.ModelFactory;

public class StartupBeanTest {
	
	private StartupBean startupBean;
	
	private AdministratorDao administratorDao;
	
	@Before
	public void setUp() throws InitializationError {
		startupBean = new StartupBean();
		
		administratorDao = mock(AdministratorDao.class);
		
		try {
			FieldUtils.writeField(startupBean, "administratorDao", administratorDao, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testInit() throws Exception {
		Administrator admin = ModelFactory.administrator();
		admin.setUsername("admin");
		admin.setPassword("password");
		
		startupBean.init();
		
		verify(administratorDao, times(1)).existsAdministrator();
	}

}

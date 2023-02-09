package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.Administrator;
import it.unifi.ing.swam.model.ModelFactory;

public class AdministratorDaoTest extends JpaTest {
	
	private AdministratorDao administratorDao;
	private Administrator administrator;
	
	@Override
	protected void init() throws InitializationError {
		administrator = ModelFactory.administrator();
		administrator.setUsername("admin");
		administrator.setPassword("password");
		
		entityManager.persist(administrator);
		
		administratorDao = new AdministratorDao();
		
		try {
			FieldUtils.writeField(administratorDao, "entityManager", entityManager, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testSave() {
		Administrator anotherAdmin = ModelFactory.administrator();
		anotherAdmin.setUsername("admin2");
		anotherAdmin.setPassword("examplepassword2");
		
		administratorDao.save(anotherAdmin);
		
		assertEquals(anotherAdmin, entityManager
				.createQuery("from Administrator where uuid = :uuid", Administrator.class)
				.setParameter("uuid", anotherAdmin.getUuid())
				.getSingleResult());
	}
	
	@Test
	public void testFindById() {
		Administrator result = administratorDao.findById(administrator.getId());
		
		assertEquals(administrator, result);
	}
	
	@Test
	public void testFindByWrongId() {
		Administrator result = administratorDao.findById(new Long(9999));
		
		assertNull(result);
	}
	
	@Test
	public void testDelete() {
		administratorDao.delete(administrator);
		
		assertNull(administratorDao.findById(administrator.getId()));
	}
	
	@Test
	public void testExistsAdministrator() {
		assertTrue(administratorDao.existsAdministrator());
		
		administratorDao.delete(administrator);
		
		assertFalse(administratorDao.existsAdministrator());
	}
	
	@Test
	public void testLogin() {
		Administrator credentials = ModelFactory.administrator();
		credentials.setUsername("admin");
		credentials.setPassword("password");
		
		Administrator result = administratorDao.login(credentials);
		
		assertEquals(administrator, result);
	}
	
	@Test
	public void testWrongLogin() {
		Administrator credentials = ModelFactory.administrator();
		credentials.setUsername("admin2");
		credentials.setPassword("examplewrongpassword");
		
		Administrator result = administratorDao.login(credentials);
		
		assertNull(result);
	}
}

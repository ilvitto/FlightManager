package it.unifi.ing.swam.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.model.InitializationError;

public abstract class JpaTest {
	
	private static EntityManagerFactory entityManagerFactory;
	protected EntityManager entityManager;

	@BeforeClass
	public static void setUpClass() {
		entityManagerFactory = Persistence.createEntityManagerFactory("test");
	}

	@Before
	public void setUp() throws InitializationError {
		entityManager = entityManagerFactory.createEntityManager();
		
		truncateAllTables();
		
		entityManager.getTransaction().begin();
		init();
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		entityManager.getTransaction().begin();
	}

	@After
	public void tearDown() {
		if ( entityManager.getTransaction().isActive() ) {
			entityManager.getTransaction().rollback();
		}
		entityManager.close();
	}

	@AfterClass
	public static void tearDownClass() {
		entityManagerFactory.close();
	}
	
	protected abstract void init() throws InitializationError;
	
	protected void truncateAllTables() {
        entityManager.getTransaction().begin();

        @SuppressWarnings("unchecked")
        List<Object[]> tables = entityManager
					        .createNativeQuery("SELECT * FROM information_schema.tables WHERE table_schema IN ('test');")
					        .getResultList();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate();
        for (Object[] tableNameObject : tables) {
            String tableName = (String) tableNameObject[2];
            entityManager.createNativeQuery("TRUNCATE TABLE " + "test." + tableName).executeUpdate();
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate();
        
        entityManager.getTransaction().commit();

    }

}

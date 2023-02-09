package it.unifi.ing.swam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import it.unifi.ing.swam.model.strategy.AllNightLong;
import it.unifi.ing.swam.model.strategy.BigGroup;
import it.unifi.ing.swam.model.strategy.CrazyWednesday;
import it.unifi.ing.swam.model.strategy.NoDiscount;
import it.unifi.ing.swam.model.strategy.Strategy;
import it.unifi.ing.swam.model.strategy.WellInAdvance;

public class StrategyDaoTest extends JpaTest {
	
	private StrategyDao strategyDao;
	private Strategy strategy;
	
	@Override
	protected void init() throws InitializationError {
		strategy = new NoDiscount(UUID.randomUUID().toString());
		strategy.setDate( new Date () );
		
		entityManager.persist(strategy);
		
		strategyDao = new StrategyDao();
		
		try {
			FieldUtils.writeField(strategyDao, "entityManager", entityManager, true);
		} catch (IllegalAccessException e) {
			throw new InitializationError(e);
		}
	}
	
	@Test
	public void testSaveAllNightLong() {
		Strategy anotherStrategy = new AllNightLong(UUID.randomUUID().toString());
		anotherStrategy.setDate( new Date () );
		
		strategyDao.save(anotherStrategy);
		
		assertEquals(anotherStrategy, entityManager
				.createQuery("from AllNightLong where uuid = :uuid", AllNightLong.class)
				.setParameter("uuid", anotherStrategy.getUuid())
				.getSingleResult());
		
	}
	
	@Test
	public void testSaveBigGroup() {
		Strategy anotherStrategy = new BigGroup(UUID.randomUUID().toString());
		anotherStrategy.setDate( new Date () );
		
		strategyDao.save(anotherStrategy);
		
		assertEquals(anotherStrategy, entityManager
				.createQuery("from BigGroup where uuid = :uuid", BigGroup.class)
				.setParameter("uuid", anotherStrategy.getUuid())
				.getSingleResult());
		
	}
	
	@Test
	public void testSaveCrazyWednesday() {
		Strategy anotherStrategy = new CrazyWednesday(UUID.randomUUID().toString());
		anotherStrategy.setDate( new Date () );
		
		strategyDao.save(anotherStrategy);
		
		assertEquals(anotherStrategy, entityManager
				.createQuery("from CrazyWednesday where uuid = :uuid", CrazyWednesday.class)
				.setParameter("uuid", anotherStrategy.getUuid())
				.getSingleResult());
		
	}
	
	@Test
	public void testSaveNoDiscount() {
		Strategy anotherStrategy = new NoDiscount(UUID.randomUUID().toString());
		anotherStrategy.setDate( new Date () );
		
		strategyDao.save(anotherStrategy);
		
		assertEquals(anotherStrategy, entityManager
				.createQuery("from NoDiscount where uuid = :uuid", NoDiscount.class)
				.setParameter("uuid", anotherStrategy.getUuid())
				.getSingleResult());
	}
	
	@Test
	public void testSaveWellInAdvance() {
		Strategy anotherStrategy = new WellInAdvance(UUID.randomUUID().toString());
		anotherStrategy.setDate( new Date () );
		
		strategyDao.save(anotherStrategy);
		
		assertEquals(anotherStrategy, entityManager
				.createQuery("from WellInAdvance where uuid = :uuid", WellInAdvance.class)
				.setParameter("uuid", anotherStrategy.getUuid())
				.getSingleResult());
		
	}
	
	@Test
	public void testFindById() {
		Strategy result = strategyDao.findById(strategy.getId());
		
		assertEquals(strategy.getUuid(), result.getUuid());
	}
	
	@Test
	public void testFindByWrongId() {
		Strategy result = strategyDao.findById( new Long(9999) );
		
		assertNull(result);
	}
	
	@Test
	public void testDelete() {
		strategyDao.delete(strategy);
		
		assertNull(strategyDao.findById(strategy.getId()));
	}
}

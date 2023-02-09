package it.unifi.ing.swam.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.unifi.ing.swam.model.strategy.Strategy;

public class StrategyDao implements Serializable {
	static final long serialVersionUID = 1L;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(Strategy strategy) {
		if(strategy.getId() != null)
			this.entityManager.merge(strategy);
		else
			this.entityManager.persist(strategy);
	}
	
	public void delete(Strategy strategy) {
		this.entityManager.remove(
				this.entityManager.contains(strategy) ? strategy : this.entityManager.merge(strategy));
	}
	
	public Strategy findById(Long id) {
		return entityManager.find(Strategy.class, id);
	}
}

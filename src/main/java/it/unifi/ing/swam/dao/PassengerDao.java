package it.unifi.ing.swam.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.unifi.ing.swam.model.Passenger;

public class PassengerDao implements Serializable{
	
	static final long serialVersionUID = 1L;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(Passenger passenger) {
		if(passenger.getId() != null)
			this.entityManager.merge(passenger);
		else
			this.entityManager.persist(passenger);
	}
	
	public void delete(Passenger passenger) {
		this.entityManager.remove(
				this.entityManager.contains(passenger) ? passenger : this.entityManager.merge(passenger));
	}
	
	public Passenger findById(Long passengerId) {
		return entityManager.find(Passenger.class, passengerId);
	}
}
package it.unifi.ing.swam.dao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.temp.TemporaryReservationSeats;

public class TemporaryReservationSeatsDao implements Serializable{

	static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(TemporaryReservationSeats temp) {
		if(temp.getId() != null)
			this.entityManager.merge(temp);
		else
			this.entityManager.persist(temp);
	}
	
	public void delete(TemporaryReservationSeats temp) {
		System.out.println("Removing... ");
		this.entityManager.remove(
				this.entityManager.contains(temp) ? temp : this.entityManager.merge(temp));
	}
	
	public void delete(Long temp) {
		System.out.println("Removing from id "+temp);
		delete(findById(temp));
	}
	
	public TemporaryReservationSeats findById(Long tempId) {
		return entityManager.find(TemporaryReservationSeats.class, tempId);
	}
	
	public int getTemporaryReservedSeats(Flight flight) {
		Long result = this.entityManager
				.createQuery("SELECT SUM(trs.nPassengers) "
							+ "FROM TemporaryReservationSeats trs "
							+ "WHERE trs.flight.id = :flightId ",
							Long.class)
				.setParameter("flightId", flight.getId())
				.getSingleResult();
		if(result == null)
			return 0;
		return result.intValue();
	}
	
	public TemporaryReservationSeats getLastAdded(){
		List<TemporaryReservationSeats> results = this.entityManager
				.createQuery("SELECT trs "
							+ "FROM TemporaryReservationSeats trs "
							+ "ORDER BY trs.id desc ",
							TemporaryReservationSeats.class)
				.getResultList();
		if(results.isEmpty())
			return null;
		
		return results.get(0);
	}

	public void cleanExpiredTemporaryReservation(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -minutes);
		
		List<TemporaryReservationSeats> result = this.entityManager
				.createQuery("FROM TemporaryReservationSeats tr", TemporaryReservationSeats.class)																				
				.getResultList();
		
		for(int i = 0; i < result.size(); i++){
			if(result.get(i).getDate().before(cal.getTime())){
				System.out.println("Cleaning 1 old reservation.. ");
				delete(result.get(i));
			}
		}
	}
}

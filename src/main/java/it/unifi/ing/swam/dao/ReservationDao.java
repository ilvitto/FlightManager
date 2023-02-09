package it.unifi.ing.swam.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.unifi.ing.swam.model.Reservation;

public class ReservationDao implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(Reservation reservation) {
		if(reservation.getId() != null)
			entityManager.merge(reservation);
		else
			entityManager.persist(reservation);
	}
	
	public void delete(Reservation reservation) {
		this.entityManager.remove(
				this.entityManager.contains(reservation) ? reservation : this.entityManager.merge(reservation));
	}
	
	public Reservation findById(Long id) {
		return entityManager.find(Reservation.class, id);
	}
	
	public Reservation searchReservation(String reservationId, String email) {
		List<Reservation> result = this.entityManager
				.createQuery("SELECT r "
							+ "FROM Reservation r "
							+ "WHERE r.reservationId = :reservationId AND r.email = :email",
							Reservation.class)
				.setParameter("reservationId", reservationId)
				.setParameter("email", email)
				.setMaxResults(1)
				.getResultList();
		
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public Long getIdFromLastReservation() {
		Long result = this.entityManager
				.createQuery("SELECT MAX(r.id) "
							+ "FROM Reservation r", Long.class)
				.getSingleResult();
		
		if(result == null)
			return Long.valueOf(0);
		
		return result;
	}
}

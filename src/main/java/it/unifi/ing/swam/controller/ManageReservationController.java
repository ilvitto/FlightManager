package it.unifi.ing.swam.controller;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import it.unifi.ing.swam.bean.ReservationSessionBean;
import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.Passenger;
import it.unifi.ing.swam.model.Reservation;

@Model
@SessionScoped
@Named
public class ManageReservationController implements Serializable {

	static final long serialVersionUID = 1L;
	
	@Inject
	private ReservationDao reservationDao;
	
	@Inject
	private FlightDao flightDao;
	
	@Inject
	private ReservationSessionBean reservationSession;
	
	private Reservation reservation;
	
	private List<Passenger> passengers;
	
	private boolean justUpdatePassengers;
	
	private boolean justDeleteReservation;
	
	public Reservation getReservation() {
		if(reservation == null || reservation.getId() != reservationSession.getId()) {
			try {
				Long id = Long.valueOf(reservationSession.getId());
				reservation = reservationDao.findById(id);
				passengers = null;
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("id not a number");
			}
		}
		return reservation;
	}

	public List<Passenger> getPassengers() {
		if(passengers == null){
			passengers = getReservation().getPassengers();
		}
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}
	
	public boolean isJustUpdatePassengers() {
		return justUpdatePassengers;
	}

	public void setJustUpdatePassengers(boolean justUpdatePassengers) {
		this.justUpdatePassengers = justUpdatePassengers;
	}
	
	public boolean isJustDeleteReservation() {
		return justDeleteReservation;
	}

	public void setJustDeleteReservation(boolean justDeleteReservation) {
		this.justDeleteReservation = justDeleteReservation;
	}

	@Transactional
	public String updatePassengers() {
		reservation.setPassengers(passengers);
		reservationDao.save(reservation);
		justUpdatePassengers = true;
		return "update-passengers-success?faces-redirect=true";
	}
	
	@Transactional
	public String deleteReservation() {
		reservationDao.delete(reservation);
		List<Flight> flights = reservation.getFlights();
		int nPassengers = reservation.getPassengers().size();
		
		//Make free reserved seats on flight(s)
		for(int i = 0; i < flights.size(); i++) {
			Long id = flights.get(i).getId();
			Flight f = flightDao.findById(id);
			f.setReservedSeats(f.getReservedSeats() - nPassengers);
			flightDao.save(f);
		}
		
		reservationSession.setId(null);
		reservation = null;
		passengers = null;
		justDeleteReservation = true;
		
		return "delete-reservation-success?faces-redirect=true";
	}
}

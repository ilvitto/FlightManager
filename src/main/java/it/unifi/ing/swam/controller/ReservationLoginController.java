package it.unifi.ing.swam.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import it.unifi.ing.swam.bean.ReservationSessionBean;
import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Reservation;

@Model
public class ReservationLoginController {

	@Inject
	private ReservationSessionBean reservationSession;
	
	@Inject
	private ReservationDao reservationDao;

	private Reservation reservationData;
	
	private String error;
	
	public ReservationLoginController() {
		reservationData = ModelFactory.reservation();
	}
	
	public String login() {
		Reservation myBooking = reservationDao.searchReservation(reservationData.getReservationId(), reservationData.getEmail());
		
		if( myBooking == null ) {
			setError("Login failed! Retry");
			return "mybooking-login";
		}
		
		reservationData = myBooking;
		reservationSession.setId(myBooking.getId());
		return "show-reservation?faces-redirect=true";
	}
	
	public String logout() {
		reservationSession.setId(null);
		return "index?faces-redirect=true";
	}
	
	public void unsetLogin(){
		reservationSession.setId(null);
	}

	public Reservation getReservationData() {
		if(reservationData == null && reservationSession.isLoggedIn()){
			reservationData = reservationDao.findById(reservationSession.getId());
		}
		return reservationData;
	}
	
	public void setReservationData(Reservation reservationData) {
		this.reservationData = reservationData;
	}

	public String getError() {
		if(error == null)
			error = "";
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}

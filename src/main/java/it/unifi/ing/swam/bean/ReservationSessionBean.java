package it.unifi.ing.swam.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.model.Reservation;

@SessionScoped
@Named
public class ReservationSessionBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Reservation reservation;
	
	@Inject
	private ReservationDao reservationDao;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
		reservation = id == null ? null : reservationDao.findById(id);
	}
	
	public boolean isLoggedIn() {
		return id != null;
	}
	
	public void checkNotLoggedin() throws IOException {
	    if (!isLoggedIn()) {
	        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	        ec.redirect(ec.getRequestContextPath() + "/mybooking-login.xhtml");
	    }
	}
	
	public void checkLoggedin() throws IOException {
	    if (isLoggedIn()) {
	        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	        ec.redirect(ec.getRequestContextPath() + "/show-reservation.xhtml");
	    }
	}
	
	public Reservation getReservation() {
		return reservation;
	}
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

}

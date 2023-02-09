package it.unifi.ing.swam.model.temp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.unifi.ing.swam.model.BaseEntity;
import it.unifi.ing.swam.model.Flight;

@Entity
@Table(name="temporaryreservationseats")
public class TemporaryReservationSeats extends BaseEntity {
	
	@ManyToOne
	private Flight flight;
	
	private int nPassengers;
	
	private Date date;
	
	public TemporaryReservationSeats() {
		super();
	}
	
	public TemporaryReservationSeats(String uuid) {
		super(uuid);
	}
	
	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public int getnPassengers() {
		return nPassengers;
	}

	public void setnPassengers(int nPassengers) {
		this.nPassengers = nPassengers;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
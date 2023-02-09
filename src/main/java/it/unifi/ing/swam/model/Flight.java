package it.unifi.ing.swam.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="flight")
public class Flight extends BaseEntity {
	
	private String flightNumber;
	
	@Temporal(TemporalType.DATE)
	private Date departureDate;
	
	@Temporal(TemporalType.TIME)
	private Date departureTime;
	
	private int totalSeats;
	
	private int reservedSeats;
	
	private float pricePerPerson;
	
	private int flightDuration;			//in minutes
	
	@ManyToOne
	private Airport sourceAirport;
	
	@ManyToOne
	private Airport destinationAirport;

	public Flight() {
		super();
		reservedSeats = 0;
	}
	
	public Flight(String uuid) {
		super(uuid);
		reservedSeats = 0;
	}
	
	public void copyFlight(Flight f){
		this.flightNumber = f.getFlightNumber();
		this.sourceAirport = f.getSourceAirport();
		this.destinationAirport = f.getDestinationAirport();
		this.totalSeats = f.getTotalSeats();
		this.reservedSeats = f.getReservedSeats();
		this.departureDate = f.getDepartureDate();
		this.departureTime = f.getDepartureTime();
		this.pricePerPerson = f.getPricePerPerson();
		this.flightDuration = f.getFlightDuration();
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public int getReservedSeats() {
		return reservedSeats;
	}

	public void setReservedSeats(int reservedSeats) {
		this.reservedSeats = reservedSeats;
	}

	public float getPricePerPerson() {
		return pricePerPerson;
	}

	public void setPricePerPerson(float pricePerPerson) {
		this.pricePerPerson = pricePerPerson;
	}

	public int getFlightDuration() {
		return flightDuration;
	}

	public void setFlightDuration(int flightDuration) {
		this.flightDuration = flightDuration;
	}

	public Airport getSourceAirport() {
		return sourceAirport;
	}

	public void setSourceAirport(Airport sourceAirport) {
		this.sourceAirport = sourceAirport;
	}

	public Airport getDestinationAirport() {
		return destinationAirport;
	}

	public void setDestinationAirport(Airport destinationAirport) {
		this.destinationAirport = destinationAirport;
	}
	
}

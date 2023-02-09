package it.unifi.ing.swam.model;

import java.util.UUID;

import it.unifi.ing.swam.model.temp.TemporaryReservationSeats;

public class ModelFactory {
	
	private ModelFactory() {
		
	}
	
	public static Administrator administrator() {
		return new Administrator(UUID.randomUUID().toString());
	}
	
	public static Airport airport() {
		return new Airport(UUID.randomUUID().toString());
	}
	
	public static Flight flight() {
		return new Flight(UUID.randomUUID().toString());
	}
	
	public static Reservation reservation() {
		return new Reservation(UUID.randomUUID().toString());
	}
	
	public static Passenger passenger() {
		return new Passenger(UUID.randomUUID().toString());
	}
	
	public static TemporaryReservationSeats temporaryReservationSeats(){
		return new TemporaryReservationSeats(UUID.randomUUID().toString());
	}
}
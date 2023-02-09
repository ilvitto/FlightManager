package it.unifi.ing.swam.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.dao.ReservationDao;
import it.unifi.ing.swam.dao.TemporaryReservationSeatsDao;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.model.Passenger;
import it.unifi.ing.swam.model.Reservation;
import it.unifi.ing.swam.model.strategy.AllNightLong;
import it.unifi.ing.swam.model.strategy.BigGroup;
import it.unifi.ing.swam.model.strategy.CrazyWednesday;
import it.unifi.ing.swam.model.strategy.NoDiscount;
import it.unifi.ing.swam.model.strategy.Strategy;
import it.unifi.ing.swam.model.strategy.WellInAdvance;
import it.unifi.ing.swam.model.temp.TemporaryReservationSeats;
import it.unifi.ing.swam.util.Util;

@Model
@ConversationScoped
@Named
public class InsertReservationController implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	private final int maxMinutesTemporaryReservation = 10;
	
	@Inject
	private ReservationDao reservationDao;
	
	@Inject
	private FlightDao flightDao;
	
	@Inject
	private TemporaryReservationSeatsDao temporaryReservationSeatsDao;
	
	private Long flightOut;
	private Long flightBack;
	private Flight forwardFlight;
	private Flight returnFlight;
	
	private List<Flight> flights;

	private Integer nPassengers;
	private List<Passenger> passengers;
	
	private Reservation reservation;
	
	private Strategy strategy;
	
	private List<Long> temporaryReservationSeatsList;
	
	private boolean justBooked;
	
	private boolean discounted;
	
	public boolean isJustBooked() {
		return justBooked;
	}

	public void setJustBooked(boolean justBooked) {
		this.justBooked = justBooked;
	}
	
	public void unsetJustBooked() {
		this.justBooked = false;
	}

	public Long getFlightOut() {
		return flightOut;
	}

	public void setFlightOut(Long flightOut) {
		this.flightOut = flightOut;
	}

	public Long getFlightBack() {
		return flightBack;
	}

	public void setFlightBack(Long flightBack) {
		this.flightBack = flightBack;
	}

	public Integer getnPassengers() {
		if(nPassengers == null)
			nPassengers = new Integer(1);
		
		return nPassengers;
	}

	public void setnPassengers(Integer nPassengers) {
		this.nPassengers = nPassengers;
	}

	public List<Long> getTemporaryReservationSeatsList() {
		return temporaryReservationSeatsList;
	}

	public Reservation getReservation(){
		if(reservation == null) {
			reservation = ModelFactory.reservation();
			reservation.setDate( new Date() );
			reservation.setPassengers(getPassengers());
			reservation.setFlights(getFlights());
			reservation.setPriceCalculation(getStrategy());
			reservation.setPrice(getTotalPriceFR());
			reservation.setFinalPrice(getFinalPriceDiscounted());
		}
		
		return reservation;
	}
	
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public List<Passenger> getPassengers(){
		if(passengers == null){
			passengers = new ArrayList<Passenger>();
			for (int i = 0; i < getnPassengers(); i++) {
				passengers.add(ModelFactory.passenger());
			}
		}
		
		return passengers;
	}
	
	public List<Flight> getFlights() {
		if(flights == null || flights.isEmpty()){
			flights = new ArrayList<Flight>();
			if(flightOut != null && flightOut != 0)
				flights.add(flightDao.findById(flightOut));
			if(flightBack != null && flightBack != 0)
				flights.add(flightDao.findById(flightBack));
		}
		
		return flights;
	}
	
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
	
	public Flight getForwardFlight() {
		if(forwardFlight == null){
			if(flightOut != null && flightOut != 0)
				forwardFlight = flightDao.findById(flightOut);
		}
		
		return forwardFlight;
	}
	
	public Flight getReturnFlight(){
		if(returnFlight == null){
			if(flightBack != null && flightBack != 0)
				returnFlight = flightDao.findById(flightBack);
		}
		
		return returnFlight;
	}
	
	public float getTotalPriceForward(){
		if(flightOut != null && flightOut != 0)
			return Util.round(getForwardFlight().getPricePerPerson() * getnPassengers().floatValue(), 2);
		
		return 0;
	}
	
	public float getTotalPriceReturn(){
		if(flightBack != null && flightBack != 0)
			return Util.round(getReturnFlight().getPricePerPerson() * getnPassengers().floatValue(), 2);
		
		return 0;
	}
	
	public float getTotalPriceFR(){
		return getTotalPriceForward() + getTotalPriceReturn();
	}
	
	public float getFinalPriceDiscounted(){
		return Util.round(getTotalPriceFR() - getStrategy().getFinalDiscount(getReservation()), 2);
	}
	
	public boolean isDiscounted() {
		if(strategy != null && getStrategy().getFinalDiscount(getReservation()) > 0)
			discounted = true;
		else 
			discounted = false;
		
		return discounted;
	}

	public void setDiscounted(boolean discounted) {
		this.discounted = discounted;
	}

	public Strategy getStrategy() {
		if(strategy == null)
			strategy = chooseStrategy();
		
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	@Transactional
	public void updateReservedSeats() {
		Flight flightOut = reservation.getFlights().get(0);
		flightOut.setReservedSeats(flightOut.getReservedSeats() + reservation.getPassengers().size());
		flightDao.save(flightOut);
		
		if(reservation.getFlights().size() > 1) {
			Flight flightBack = reservation.getFlights().get(1);
			flightBack.setReservedSeats(flightBack.getReservedSeats() + reservation.getPassengers().size());
			flightDao.save(flightBack);
		}
	}
	
	public Strategy chooseStrategy() {
		if(reservation != null){
			Calendar c = Calendar.getInstance();
			c.setTime(reservation.getDate());
			Date today = new Date();
			if(!reservation.getFlights().isEmpty() && ((reservation.getFlights().get(0).getDepartureDate().getTime() - today.getTime()) / (24 * 60 * 60 * 1000)) >= 90)
				strategy = new WellInAdvance(UUID.randomUUID().toString());
			
			else if(getnPassengers() >= 10)
				strategy = new BigGroup(UUID.randomUUID().toString());
			
			else if(c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
				strategy = new CrazyWednesday(UUID.randomUUID().toString());
			
			else if(c.get(Calendar.HOUR_OF_DAY) < 6)
				strategy = new AllNightLong(UUID.randomUUID().toString());
			
			else if(getnPassengers() >= 5)
				strategy = new BigGroup(UUID.randomUUID().toString());
			
			else
				strategy = new NoDiscount(UUID.randomUUID().toString());
			
			strategy.setDate( today );
		}
		
		return strategy;
	}
	
	@Transactional
	public void reserveSeats() {
		if(temporaryReservationSeatsList == null || temporaryReservationSeatsList.isEmpty()) {
			temporaryReservationSeatsList = new ArrayList<Long>();
			TemporaryReservationSeats temporaryReservationSeats;
			System.out.println("Temporary reservation for " + nPassengers + " passengers");
			for(int i = 0; i < getFlights().size(); i++) {
				temporaryReservationSeats = ModelFactory.temporaryReservationSeats();
				temporaryReservationSeats.setDate( new Date() );
				temporaryReservationSeats.setFlight(getFlights().get(i));
				temporaryReservationSeats.setnPassengers(nPassengers);
				
				temporaryReservationSeatsDao.save(temporaryReservationSeats);
				temporaryReservationSeats = temporaryReservationSeatsDao.getLastAdded();
				temporaryReservationSeatsList.add(temporaryReservationSeats.getId());
			}
		}
	}
	
	@Transactional
	public String saveReservation() {
		
		reservation.setPassengers(getPassengers());
		reservation.setPriceCalculation(getStrategy());
		reservation.setPrice(getTotalPriceFR());
		reservation.setFinalPrice(getFinalPriceDiscounted());
		
		reservation.setFlights(getFlights());
		Long lastId = reservationDao.getIdFromLastReservation();
		reservation.setReservationId("FMID-" + (lastId.intValue() + 1));
		reservationDao.save(getReservation());
		justBooked = true;
		
		updateReservedSeats();
		
		for(int i = 0; i < temporaryReservationSeatsList.size(); i++)
			temporaryReservationSeatsDao.delete(temporaryReservationSeatsList.get(i));
		
		return "summary?faces-redirect=true";
	}
	
	//@PreDestroy
	@Transactional
	public void cleanTemporaryReservations() {
		if(temporaryReservationSeatsList != null && !temporaryReservationSeatsList.isEmpty()){
			System.out.println("@PreDestroy is called... " + temporaryReservationSeatsList.get(0));
			for(int i = 0; i < getFlights().size(); i++)
				temporaryReservationSeatsDao.delete(temporaryReservationSeatsList.get(i));
			
			temporaryReservationSeatsDao.cleanExpiredTemporaryReservation(maxMinutesTemporaryReservation);
		}
	}
	
}

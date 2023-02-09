package it.unifi.ing.swam.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import it.unifi.ing.swam.dao.AirportDao;
import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.model.Airport;
import it.unifi.ing.swam.model.Flight;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.util.Util;

@Model
@SessionScoped
@Named
public class InsertFlightController implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	@Inject
	private FlightDao flightDao;
	
	@Inject
	private AirportDao airportDao;

	private Flight flight;
	
	private List<Airport> airports;
	private List<Flight> flights;
	
	private Airport sourceAirportNew;
	private Airport destinationAirportNew;
	
	private List<Integer> listRepeats;
	private Integer repeats;
	
	//existing or new
	private String sourceAirportType;
	private String destinationAirportType;
	
	private Long sourceAirportId;
	private Long destinationAirportId;
	
	private String error;
	
	private Date fromDate;
	
	private boolean justInsertFlight;
	private boolean justDeleteFlight;
	
	public String returnInsertFlightPage(){
		return "insert-flights?faces-redirect=true";
	}
	
	public void reset(){
		flight = null;
		repeats = new Integer(1);
		sourceAirportId = airports == null ? null : airports.get(0).getId();
		destinationAirportId = sourceAirportId;
	}
	
	public Flight getFlight() {
		if(flight == null){
			flight = ModelFactory.flight();
			flight.setReservedSeats(0);
			
			Calendar cal = Calendar.getInstance();
		    cal.setTime(new Date());
		    cal.add(Calendar.HOUR_OF_DAY, -1);
		    
			flight.setDepartureDate( cal.getTime() );
		}
		return flight;
	}
	
	public void setFlight(Flight flight){
		this.flight = flight;
	}
	
	public List<Airport> getAirports(){
		airports = airportDao.getAllAirports();
		return airports;
	}
	
	public List<Flight> getFlights(){
		if(flights == null){
			flights = new ArrayList<Flight>();
		}
		return flights;
	}
	
	public String getSourceAirportType() {
		if(sourceAirportType == null)
			sourceAirportType = "existing";
		return sourceAirportType;
	}
	public void setSourceAirportType(String sourceAirportType) {
		this.sourceAirportType = sourceAirportType;
	}
	public String getDestinationAirportType() {
		if(destinationAirportType == null)
			destinationAirportType = "existing";
		return destinationAirportType;
	}
	public void setDestinationAirportType(String destinationAirportType) {
		this.destinationAirportType = destinationAirportType;
	}
	public Integer getRepeats() {
		return repeats;
	}
	public void setRepeats(Integer repeats) {
		this.repeats = repeats;
	}
	public List<Integer> getListRepeats() {
		if(listRepeats == null){
			listRepeats = new ArrayList<Integer>();
			for(int i = 0; i < 31; i++) {
				listRepeats.add(i+1);
			}
		}
			
		return listRepeats;
	}
	public void setListRepeats(List<Integer> listRepeats) {
		this.listRepeats = listRepeats;
	}
	public Airport getSourceAirportNew() {
		if(sourceAirportNew == null){
			sourceAirportNew = ModelFactory.airport();
		}
		return sourceAirportNew;
	}
	public void setSourceAirportNew(Airport sourceAirportNew) {
		this.sourceAirportNew = sourceAirportNew;
	}
	public Airport getDestinationAirportNew() {
		if(destinationAirportNew == null){
			destinationAirportNew = ModelFactory.airport();
		}
		return destinationAirportNew;
	}
	public void setDestinationAirportNew(Airport destinationAirportNew) {
		this.destinationAirportNew = destinationAirportNew;
	}
	public Long getSourceAirportId() {
		return sourceAirportId;
	}
	public void setSourceAirportId(Long sourceAirportId) {
		this.sourceAirportId = sourceAirportId;
	}
	public Long getDestinationAirportId() {
		return destinationAirportId;
	}
	public void setDestinationAirportId(Long destinationAirportId) {
		this.destinationAirportId = destinationAirportId;
	}
	public String getError() {
		if(error == null)
			error = "";
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public Date getFromDate() {
		if(fromDate == null)
			fromDate = new Date();
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public String searchFlights(){
		error = "";
		if(sourceAirportId == 0 && destinationAirportId == 0){
			flights = flightDao.getAllFlights(fromDate);
			if(flights == null || flights.isEmpty())
				error = "No available flights since " + Util.getOnlyDate(fromDate);
		}
		else if(sourceAirportId == 0){
			Airport d = airportDao.findById(destinationAirportId);
			flights = flightDao.getFlightsFromDestination(d, fromDate);
			if(flights == null || flights.isEmpty())
				error = "No available flights to " + d.getName() + " since " + Util.getOnlyDate(fromDate);
		}
		else if(destinationAirportId == 0){
			Airport s = airportDao.findById(sourceAirportId);
			flights = flightDao.getFlightsFromSource(s, fromDate);
			if(flights == null || flights.isEmpty())
				error = "No available flights from "+ s.getName() + " since " + Util.getOnlyDate(fromDate);
		}
		else{
			Airport s = airportDao.findById(sourceAirportId);
			Airport d = airportDao.findById(destinationAirportId);
			flights = flightDao.getFlightsFromDate(s,d, fromDate);
			if(flights == null || flights.isEmpty())
				error = "No available flights from "+ s.getName() + " to " + d.getName() + " since " + Util.getOnlyDate(fromDate);
		}
		return "show-flights?faces-redirect=true";
	}
	
	@Transactional
	public String saveFlight(){
		error = "";
		if(sourceAirportType.equals("new")){
			if(airportDao.getAllAirportsNames().contains(sourceAirportNew.getName())){
				error = sourceAirportNew.getName() + " airport already exists";
				return "insert-flight";
			}
			airportDao.save(sourceAirportNew);
			flight.setSourceAirport(sourceAirportNew);
		}
		else{
			Airport air = airportDao.findById(sourceAirportId);
			flight.setSourceAirport(air);
		}
		
		if(destinationAirportType.equals("new")){
			if(airportDao.getAllAirportsNames().contains(destinationAirportNew.getName())){
				error = destinationAirportNew.getName() + " airport already exists";
				return "insert-flight";
			}
			airportDao.save(destinationAirportNew);
			flight.setDestinationAirport(destinationAirportNew);
		}
		else{
			Airport air = airportDao.findById(destinationAirportId);
			flight.setDestinationAirport(air);
		}
		
		flight.setDepartureTime(Util.subOneHour(flight.getDepartureTime()));
		flightDao.save(getFlight());
		setJustInsertFlight(true);
		Date date = flight.getDepartureDate();
		for (int i = 0; i < repeats.intValue() - 1; i++) {
			date = Util.incrementDay(date);
			Flight fl = ModelFactory.flight();
			fl.copyFlight(flight);
			fl.setDepartureDate(date);
			flightDao.save(fl);
		}
		
		return "insert-flight-success?faces-redirect=true";
	}
	
	@Transactional
	public String deleteFlight(String idStr){
		Long id = Long.valueOf(idStr);
		flightDao.delete(flightDao.findById(id));
		flights = null;
		setJustDeleteFlight(true);
		return "delete-flight-success?faces-redirect=true";
	}

	public boolean isJustInsertFlight() {
		return justInsertFlight;
	}

	public void setJustInsertFlight(boolean justInsertFlight) {
		this.justInsertFlight = justInsertFlight;
	}
	
	public String backToInsertFlight() {
		error = "";
		return "insert-flights?faces-redirect=true";
	}
	
	public String showFlights() {
		error = "";
		return "show-flights?faces-redirect=true";
	}

	public boolean isJustDeleteFlight() {
		return justDeleteFlight;
	}

	public void setJustDeleteFlight(boolean justDeleteFlight) {
		this.justDeleteFlight = justDeleteFlight;
	}
}

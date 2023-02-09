package it.unifi.ing.swam.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import it.unifi.ing.swam.bean.producer.HttpParam;
import it.unifi.ing.swam.dao.AirportDao;
import it.unifi.ing.swam.dao.FlightDao;
import it.unifi.ing.swam.dao.TemporaryReservationSeatsDao;
import it.unifi.ing.swam.model.Flight;

@Model
@ConversationScoped
@Named
public class SearchFlightsController implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	private final int maxPassengersPerBooking = 15;
	private final int maxMinutesConversation = 5;
	private final int maxMinutesTemporaryReservation = 10;
	
	@Inject
	private FlightDao flightDao;
	
	@Inject
	private AirportDao airportDao;
	
	@Inject
	private TemporaryReservationSeatsDao temporaryReservationSeatsDao;
	
	@Inject
	private Conversation conversation;
	
	private Long flightOut;
	private Long flightBack;
	private List<Flight> flightsOut;
	private List<Flight> flightsBack;
	
	private List<String> sources;
	private List<String> destinations;
	private List<String> enabledSources;
	private List<String> enabledDestinations;
	private List<Date> enabledDate;
	private List<Date> enabledDateBack;
	private String enabledDateS;
	private String enabledDateBackS;
	
	private Date departureDate;
	private Date backDate;
	
	private String oneWay;
	private String error;
	private String errorOut;
	private String errorBack;
	
	private int nPassengers;
	
	@Inject @HttpParam("nocid")
	private String nocid;
	
	
	private String source;
	private String destination;
	
    public void start() {
    	if(conversation.isTransient()){
	    	conversation.begin();
	    	conversation.setTimeout(1000 * 60 * maxMinutesConversation);
    	}
    }
    
    public void stop() {
    	if(!conversation.isTransient())
    		conversation.end();
    }
    
    public String stopConversationAndReturnHome(){
    	stop();
    	error = "";
    	return "index?faces-redirect=true";
    }
    
    public void redirectToHome(String s) throws IOException {
    	stop();
    	error = s;
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/index.xhtml");
    }
	
	public int getMaxPassengersPerBooking() {
		return maxPassengersPerBooking;
	}
	
	public boolean enabledSource(String airport) {
		return getEnabledSources().contains(airport);
	}
	
	public boolean enabledDestination(String airport) {
		List<String> result = getEnabledDestinations();
		
		if(result == null)
			return false;
		else
			return result.contains(airport);
	}

	public List<String> getSources() {
		if(sources == null) {
			sources = airportDao.getAllAirportsNames();
			enabledSources = flightDao.getSourceAirports();
		}
		
		return sources;
	}
	
	public List<String> getDestinations() {
		if(destinations == null) {
			destinations = airportDao.getAllAirportsNames();
			enabledDestinations = flightDao.getDestinationAirportsFromSource(source);
		}
		
		return destinations;
	}
	
	public void setSource(String airport) {
		source = airport;
	}
	
	public String getSource() {
		if(source == null)
			if(enabledSources != null && !enabledSources.isEmpty())
				source = enabledSources.get(0);
		return source;
	}
	
	public void setDestination(String airport) {
		destination = airport;
	}
	
	public String getDestination() {
		return destination;
	}

	public List<String> getEnabledSources() {
		if(enabledSources == null)
			enabledSources = airportDao.getAllAirportsNames();
		
		return enabledSources;
	}

	public void setEnabledSources(List<String> enabledSources) {
		this.enabledSources = enabledSources;
	}

	public List<String> getEnabledDestinations() {
		if(enabledDestinations == null){
			if(getSource() == null)
				enabledDestinations = airportDao.getAllAirportsNames();
			else
				enabledDestinations = flightDao.getDestinationAirportsFromSource(source);
		}
		
		return enabledDestinations;
	}

	public void setEnabledDestinations(List<String> enabledDestinations) {
		this.enabledDestinations = enabledDestinations;
	}

	public List<Date> getEnabledDate() {
		if(getSource() != null && getDestination() != null)
			enabledDate = flightDao.getAvailableDateFromSourceToDestination(source, destination);
		
		return enabledDate;
	}

	public void setEnabledDate(List<Date> enabledDate) {
		this.enabledDate = enabledDate;
	}
	
	public List<Date> getEnabledDateBack() {
		if(getSource() != null && getDestination() != null) 
			enabledDateBack = flightDao.getAvailableDateFromSourceToDestination(destination, source);
		
		return enabledDateBack;
	}

	public void setEnabledDateBack(List<Date> enabledDateBack) {
		this.enabledDateBack = enabledDateBack;
	}
	
	public String getEnabledDateS() {
		if(getEnabledDate() != null)
			enabledDateS = getEnabledDate().toString();
		
		return enabledDateS;
	}

	public void setEnabledDateS(String enabledDateS) {
		this.enabledDateS = enabledDateS;
	}

	public String getEnabledDateBackS() {
		if(getEnabledDateBack() != null)
			enabledDateBackS = getEnabledDateBack().toString();
		
		return enabledDateBackS;
	}

	public void setEnabledDateBackS(String enabledDateBackS) {
		this.enabledDateBackS = enabledDateBackS;
	}
	
	public Long getFlightOut() {
		if(this.flightOut == null)
			this.flightOut = 0L;
		
		return flightOut;
	}

	public void setFlightOut(Long flightOut) {
		this.flightOut = flightOut;
	}

	public Long getFlightBack() {
		if(this.flightBack == null)
			this.flightBack = 0L;
		
		return flightBack;
	}

	public void setFlightBack(Long flightBack) {
		this.flightBack = flightBack;
	}

	public int getnPassengers() {
		if(nPassengers == 0)
			nPassengers = 1;
		
		return nPassengers;
	}

	public void setnPassengers(int nPassengers) {
		this.nPassengers = nPassengers;
	}

	public Date getDepartureDate() {
		if(departureDate == null)
			departureDate = new Date();
		
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public Date getBackDate() {
		if(backDate == null){
			backDate = new Date();
			if(departureDate != null)
				backDate.setTime(departureDate.getTime());
		}
		
		return backDate;
	}

	public void setBackDate(Date backDate) {
		this.backDate = backDate;
	}
	
	public String getOneWay() {
		if(oneWay == null)
			oneWay = "oneway";
		
		return oneWay;
	}

	public void setOneWay(String oneWay) {
		this.oneWay = oneWay;
	}
	
	public void setError(String err) {
		error = err;
	}
	
	public String getNocid() {
		return nocid;
	}

	public void setNocid(String nocid) {
		this.nocid = nocid;
	}

	public String getError() {
		if(error == null){
			if(nocid != null && !nocid.equals(""))
				error = "ATTENTION: session expired! Retry to insert data!";
			else
				error = "";
		}
		
		return error;
	}
	
	public String getErrorOut() {
		if(errorOut == null)
			errorOut = "";
		
		return errorOut;
	}

	public void setErrorOut(String errorOut) {
		this.errorOut = errorOut;
	}

	public String getErrorBack() {
		if(errorBack == null)
			errorBack = "";
		
		return errorBack;
	}

	public void setErrorBack(String errorBack) {
		this.errorBack = errorBack;
	}

	public List<Flight> getFlightsOut() {
		if(flightsOut == null) {
			flightsOut = flightDao.getFlights(source, destination, departureDate);

			if(flightsOut == null){
				errorOut = "No flights available for the selected date";
				flightsOut = new ArrayList<Flight>();
			}
		}
		
		return flightsOut;
	}
	
	public List<Flight> getFlightsBack() {
		if(oneWay.equals("return") && flightsBack == null) {
			flightsBack = flightDao.getFlights(destination, source, backDate);
			
			if(flightsBack == null){
				errorBack = "No flights available for the selected date";
				flightsBack = new ArrayList<Flight>();
			}
		}
		
		return flightsBack;
	}
	
	@Transactional
	public boolean isAvailable(Flight flight) {
		temporaryReservationSeatsDao.cleanExpiredTemporaryReservation(maxMinutesTemporaryReservation);
		int realSeats = flight.getTotalSeats() - flight.getReservedSeats();
		int temp = temporaryReservationSeatsDao.getTemporaryReservedSeats(flight);
		int availableSeats = realSeats - temp;
		
		return (availableSeats >= nPassengers);
	}
	
	@Transactional
	public int availableSeats(Flight flight) {
		int realSeats = flight.getTotalSeats() - flight.getReservedSeats();
		int temp = temporaryReservationSeatsDao.getTemporaryReservedSeats(flight);
		int availableSeats = realSeats - temp;
		
		return availableSeats;
	}
	public String isAvailableStr(Flight flight) {
		if(isAvailable(flight))
			return "";
		
		return "disabled";
	}
	
	public boolean existResult() {
		boolean forward = false;
		boolean backward = false;
		if(flightsOut != null && !flightsOut.isEmpty())
			forward = true;
		if(flightsBack != null && !flightsBack.isEmpty())
			backward = true;
		
		return (forward || backward);
	}
	
	public String searchFlight() { 
		start();
		error = "";
		
		return "flights-result?faces-redirect=true";
	}
	
	public String confirmFlights() {
		return "confirm?faces-redirect=true";
	}
	
}

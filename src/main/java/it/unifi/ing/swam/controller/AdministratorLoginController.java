package it.unifi.ing.swam.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import it.unifi.ing.swam.bean.AdministratorSessionBean;
import it.unifi.ing.swam.dao.AdministratorDao;
import it.unifi.ing.swam.model.Administrator;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.util.Util;

@Model
public class AdministratorLoginController {
	
	@Inject
	private AdministratorSessionBean administratorSession;
	
	@Inject
	private AdministratorDao administratorDao;

	private Administrator administratorData;
	
	private String error;
	
	public AdministratorLoginController() {
		administratorData = ModelFactory.administrator();
	}
	
	public String login() {
		administratorData.setPassword(Util.digest(administratorData.getPassword()));
		Administrator loggedAdministrator = administratorDao.login(getAdministratorData());
		
		if( loggedAdministrator == null ) {
			setError("Login failed! Retry");
			return "administrator-login";
		}
		
		administratorData = loggedAdministrator;
		administratorSession.setAdministratorId(loggedAdministrator.getId());
		return "insert-flights?faces-redirect=true";
	}
	
	public String logout() {
		administratorSession.setAdministratorId(null);
		return "index?faces-redirect=true";
	}
	
	public void unsetLogin(){
		administratorSession.setAdministratorId(null);
	}
	
	public Administrator getAdministratorData() {
		if(administratorData == null && administratorSession.isLoggedIn()){
			administratorData = administratorDao.findById(administratorSession.getAdministratorId());
		}
		return administratorData;
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

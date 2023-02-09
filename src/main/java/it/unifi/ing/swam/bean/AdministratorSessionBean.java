package it.unifi.ing.swam.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import it.unifi.ing.swam.dao.AdministratorDao;
import it.unifi.ing.swam.model.Administrator;

@SessionScoped
@Named
public class AdministratorSessionBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long administratorId;
	
	private Administrator administrator;
	
	@Inject
	private AdministratorDao administratorDao;
	
	public Long getAdministratorId() {
		return administratorId;
	}
	public void setAdministratorId(Long administratorId) {
		this.administratorId = administratorId;
		administrator = (administratorId == null) ? null : administratorDao.findById(administratorId);
	}
	
	public boolean isLoggedIn() {
		return administratorId != null;
	}
	
	public void checkNotLoggedin() throws IOException {
	    if (!isLoggedIn()) {
	        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	        ec.redirect(ec.getRequestContextPath() + "/administration-login.xhtml");
	    }
	}
	
	public void checkLoggedin() throws IOException {
	    if (isLoggedIn()) {
	        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	        ec.redirect(ec.getRequestContextPath() + "/insert-flights.xhtml");
	    }
	}
	
	public Administrator getAdministrator() {
		return administrator;
	}
	public void setAdministrator(Administrator administrator) {
		this.administrator = administrator;
	}
	
}

package it.unifi.ing.swam.bean.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;

import it.unifi.ing.swam.dao.AdministratorDao;
import it.unifi.ing.swam.model.Administrator;
import it.unifi.ing.swam.model.ModelFactory;
import it.unifi.ing.swam.util.Util;

@Singleton
@Startup
public class StartupBean {
	
	@Inject
	private AdministratorDao administratorDao;
	
	@PostConstruct
	@Transactional
	public void init() throws Exception {
		
		//Default administrator creation
		if(!administratorDao.existsAdministrator())
			administratorDao.save(administrator("admin", "password"));
	}
	
	private Administrator administrator(String username, String password) {
		Administrator admin = ModelFactory.administrator();
		admin.setUsername(username);
		admin.setPassword(Util.digest(password));
		
		return admin;
	}
}
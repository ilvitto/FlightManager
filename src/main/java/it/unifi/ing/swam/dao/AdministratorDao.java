package it.unifi.ing.swam.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.unifi.ing.swam.model.Administrator;

public class AdministratorDao implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(Administrator administrator) {
		if(administrator.getId() != null)
			this.entityManager.merge(administrator);
		else
			this.entityManager.persist(administrator);
	}
	
	public void delete(Administrator administrator) {
		this.entityManager.remove(
				this.entityManager.contains(administrator) ? administrator : this.entityManager.merge(administrator));
	}
	
	public Administrator findById(Long administratorId) {
		return this.entityManager.find(Administrator.class, administratorId);
	}
	
	public boolean existsAdministrator() {
		List<Administrator> result = this.entityManager
				.createQuery("SELECT a "
							+ "FROM Administrator a ", Administrator.class)
				.getResultList();
		
		if(result.isEmpty())
			return false;
		
		return true;
	}
	
	public Administrator login(Administrator administrator) {
		List<Administrator> result = this.entityManager
				.createQuery("SELECT a "
							+ "FROM Administrator a "
							+ "WHERE a.username = :username AND a.password = :password",
							Administrator.class)
				.setParameter("username", administrator.getUsername())
				.setParameter("password", administrator.getPassword())
				.setMaxResults(1)
				.getResultList();
		
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
}

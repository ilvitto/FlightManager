package it.unifi.ing.swam.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="administrator")
public class Administrator extends BaseEntity {
	
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	public Administrator() {
		super();
	}
	
	public Administrator(String uuid) {
		super(uuid);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

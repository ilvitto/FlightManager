package it.unifi.ing.swam.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="airport")
public class Airport extends BaseEntity {
	
	private String name;
	
	private int ZIP;
	
	private int GMT;
	
	public Airport() {
		super();
	}
	
	public Airport(String uuid) {
		super(uuid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getZIP() {
		return ZIP;
	}

	public void setZIP(int ZIP) {
		this.ZIP = ZIP;
	}

	public int getGMT() {
		return GMT;
	}

	public void setGMT(int GMT) {
		this.GMT = GMT;
	}
}

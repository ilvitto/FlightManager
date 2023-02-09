package it.unifi.ing.swam.model.strategy;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import it.unifi.ing.swam.model.Reservation;

@Entity(name = "strategy")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Strategy_Type", discriminatorType=DiscriminatorType.STRING)
public abstract class Strategy {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String uuid;
	
	private Date date;

	protected Strategy() {
		setDate(new Date());
	}

	public Strategy(String uuid) {
		if(uuid == null) {
			throw new IllegalArgumentException("uuid cannot be null");
		}
		this.uuid = uuid;
	}

	public Long getId() {
		return id;
	}
	
	public String getUuid() {
		return uuid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public abstract float getFinalDiscount(Reservation reservation);
	
	public abstract String getDescription();
}

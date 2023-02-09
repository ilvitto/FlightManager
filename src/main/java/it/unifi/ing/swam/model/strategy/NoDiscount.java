package it.unifi.ing.swam.model.strategy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import it.unifi.ing.swam.model.Reservation;

@Entity
@DiscriminatorValue("NoDiscount")
public class NoDiscount extends Strategy {

	public NoDiscount() {
		super();
	}
	
	public NoDiscount(String uuid) {
		super(uuid);
	}
	
	@Override
	public float getFinalDiscount(Reservation reservation) {
		return 0;
	}

	@Override
	public String getDescription() {
		return "No discount applied";
	}

}

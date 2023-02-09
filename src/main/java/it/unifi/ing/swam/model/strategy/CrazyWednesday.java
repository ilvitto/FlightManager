package it.unifi.ing.swam.model.strategy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import it.unifi.ing.swam.model.Reservation;
import it.unifi.ing.swam.util.Util;

@Entity
@DiscriminatorValue("CrazyWednesday")
public class CrazyWednesday extends Strategy {

	public CrazyWednesday() {
		super();
	}
	
	public CrazyWednesday(String uuid) {
		super(uuid);
	}
	
	@Override
	public float getFinalDiscount(Reservation reservation) {
		return Util.round(reservation.getPrice() * 10 / 100, 2);
	}

	@Override
	public String getDescription() {
		return "Discount applied for Crazy Wednesday (10%)";
	}

}

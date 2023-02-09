package it.unifi.ing.swam.model.strategy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import it.unifi.ing.swam.model.Reservation;
import it.unifi.ing.swam.util.Util;

@Entity
@DiscriminatorValue("BigGroup")
public class BigGroup extends Strategy {

	public BigGroup() {
		super();
	}
	
	public BigGroup(String uuid) {
		super(uuid);
	}
	
	@Override
	public float getFinalDiscount(Reservation reservation) {
		int nPassengers = reservation.getPassengers().size();
		int c = Math.floorDiv(nPassengers, 5);
		return Util.round(reservation.getPrice() * c * 5 / 100, 2);
	}

	@Override
	public String getDescription() {
		return "Discount applied for big groups (5% each 5 people)";
	}

}

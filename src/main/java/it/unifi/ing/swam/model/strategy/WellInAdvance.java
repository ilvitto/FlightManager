package it.unifi.ing.swam.model.strategy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import it.unifi.ing.swam.model.Reservation;
import it.unifi.ing.swam.util.Util;

@Entity
@DiscriminatorValue("WellInAdvance")
public class WellInAdvance extends Strategy {

	public WellInAdvance() {
		super();
	}
	
	public WellInAdvance(String uuid) {
		super(uuid);
	}
	
	@Override
	public float getFinalDiscount(Reservation reservation) {
		return Util.round(reservation.getPrice() * 25 / 100, 2);
	}

	@Override
	public String getDescription() {
		return "Discount applied for large advance booking (> 3 months)";
	}

}

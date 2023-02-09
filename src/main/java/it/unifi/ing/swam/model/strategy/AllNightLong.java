package it.unifi.ing.swam.model.strategy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import it.unifi.ing.swam.model.Reservation;
import it.unifi.ing.swam.util.Util;

@Entity
@DiscriminatorValue("AllNightLong")
public class AllNightLong extends Strategy {

	public AllNightLong() {
		super();
	}
	
	public AllNightLong(String uuid) {
		super(uuid);
	}
	
	@Override
	public float getFinalDiscount(Reservation reservation) {
		return Util.round(reservation.getPrice() * 8 / 100, 2);
	}

	@Override
	public String getDescription() {
		return "Discount applied for night booking (from 0:00 to 6:00)";
	}

}

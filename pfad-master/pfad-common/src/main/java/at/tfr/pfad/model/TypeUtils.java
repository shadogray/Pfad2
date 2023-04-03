package at.tfr.pfad.model;

import at.tfr.pfad.ActivityType;
import at.tfr.pfad.PaymentType;

public class TypeUtils {

	public static PaymentType toPayType(ActivityType actType) {
		switch (actType) {
		case Membership:
			return PaymentType.Membership;
		case Camp:
			return PaymentType.Camp;
		default:
			return PaymentType.Donation;
		}
	}
	
}

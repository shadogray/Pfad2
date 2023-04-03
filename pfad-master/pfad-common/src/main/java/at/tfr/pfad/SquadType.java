/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad;

public enum SquadType {
	BIBE(5,7), WIWO(7,10), GUSP(10,13), CAEX(13,16), RARO(16,20);
	
	int min, max;
	SquadType(final int min, final int max) {
		this.min = min;
		this.max = max;
	}
	
	public int getMin() { return min; }
	public int getMax() { return max; }

	public String getKey(Sex sex) {
		switch (this) {
		case BIBE:
			return Sex.W.equals(sex) ? "BI" : "BI"; // no sex distinction??
		case WIWO:
			return Sex.W.equals(sex) ? "WI" : "WÖ";
		case GUSP:
			return Sex.W.equals(sex) ? "GU" : "SP";
		case CAEX:
			return Sex.W.equals(sex) ? "CA" : "EX";
		case RARO:
			return Sex.W.equals(sex) ? "RA" : "RO";
		}
		return "";
	}
}
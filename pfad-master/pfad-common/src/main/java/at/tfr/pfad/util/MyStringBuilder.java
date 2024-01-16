package at.tfr.pfad.util;

import org.apache.commons.text.TextStringBuilder;

public class MyStringBuilder extends TextStringBuilder {

	public MyStringBuilder(StringBuilder other) {
		super(other.capacity());
		append(other.toString());
	}
	
	public MyStringBuilder() {
		super();
	}

	public MyStringBuilder(int initialCapacity) {
		super(initialCapacity);
	}

	public MyStringBuilder(String str) {
		super(str);
	}
}

package at.tfr.pfad.util;

import org.apache.commons.lang3.text.StrBuilder;

public class MyStrBuilder extends StrBuilder {

	public MyStrBuilder(StrBuilder other) {
		super(other.capacity());
		append(other.toCharArray());
	}
	
	public MyStrBuilder() {
		super();
	}

	public MyStrBuilder(int initialCapacity) {
		super(initialCapacity);
	}

	public MyStrBuilder(String str) {
		super(str);
	}

	public char[] getBuffer() {
		return buffer;
	}
}

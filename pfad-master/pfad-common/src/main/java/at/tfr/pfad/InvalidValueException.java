package at.tfr.pfad;

import javax.ejb.ApplicationException;

@ApplicationException
public class InvalidValueException extends PfadException {

	public InvalidValueException() {
		super();
	}

	public InvalidValueException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidValueException(String message) {
		super(message);
	}

	public InvalidValueException(Throwable cause) {
		super(cause);
	}

}

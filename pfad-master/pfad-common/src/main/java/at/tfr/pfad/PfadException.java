package at.tfr.pfad;

import javax.ejb.ApplicationException;

@ApplicationException
public class PfadException extends Exception {

	public PfadException() {
		super();
	}

	public PfadException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PfadException(String message, Throwable cause) {
		super(message, cause);
	}

	public PfadException(String message) {
		super(message);
	}

	public PfadException(Throwable cause) {
		super(cause);
	}

}

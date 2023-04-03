package at.tfr.pfad.util;

public class ValidationResult {

	private boolean valid;
	private String message;

	public ValidationResult() {
	}

	public ValidationResult(boolean valid, String message) {
		super();
		this.valid = valid;
		this.message = message;
	}
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Valid[is=" + valid + ", message=" + message + "]";
	}
}


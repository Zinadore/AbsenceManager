package us.absencemanager.exceptions;

@SuppressWarnings("serial")
public class NoDataFoundException extends Exception {

	public NoDataFoundException() {
		super();
	}

	public NoDataFoundException(String message) {
		super(message);
	}

}

package de.unituebingen.medizin.eye.ciloct.exception;

/**
 * @author strasser
 */
public class FitException extends AbstractCilOCTException {
	public FitException(final String message) {
		super(message);
	}

	public FitException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FitException(final Throwable cause) {
		super(cause);
	}
}
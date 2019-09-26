package de.unituebingen.medizin.eye.ciloct.exception;

/**
 * @author strasser
 */
public class GradientException extends AbstractCilOCTException {
	public GradientException(final String message) {
		super(message);
	}

	public GradientException(final Throwable cause) {
		super(cause);
	}

	public GradientException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
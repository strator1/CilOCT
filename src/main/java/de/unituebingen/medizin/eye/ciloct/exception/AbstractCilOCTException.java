package de.unituebingen.medizin.eye.ciloct.exception;

/**
 * @author strasser
 */
public abstract class AbstractCilOCTException extends RuntimeException {
	public AbstractCilOCTException(final String message) {
		super(message);
	}

	public AbstractCilOCTException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AbstractCilOCTException(final Throwable cause) {
		super(cause);
	}
}
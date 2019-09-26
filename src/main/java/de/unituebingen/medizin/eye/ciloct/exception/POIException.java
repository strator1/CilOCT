package de.unituebingen.medizin.eye.ciloct.exception;

/**
 *
 * @author strasser
 */
public class POIException extends AbstractCilOCTException {
	public POIException(final String message) {
		super(message);
	}

	public POIException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public POIException(final Throwable cause) {
		super(cause);
	}
}
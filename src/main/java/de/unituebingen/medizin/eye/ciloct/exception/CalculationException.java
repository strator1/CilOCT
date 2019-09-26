package de.unituebingen.medizin.eye.ciloct.exception;

/**
 *
 * @author strasser
 */
public class CalculationException extends AbstractCilOCTException {
	public CalculationException(final String message) {
		super(message);
	}

	public CalculationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CalculationException(final Throwable cause) {
		super(cause);
	}	
}
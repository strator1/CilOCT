package de.unituebingen.medizin.eye.ciloct.vo;

/**
 * @author strasser
 */
public interface IBounds {
	public double getX1();
	public double getX2();
	public default boolean isValidPoint(final double x) {
		return x >= getX1() && x < getX2();
	}
}
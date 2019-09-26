package de.unituebingen.medizin.eye.ciloct.refract;

import de.unituebingen.medizin.eye.ciloct.vo.IBounds;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author strasser
 * @param <T>
 */
public interface IInterface<T extends IBounds> extends IBounds {

	public double derivativeValue(final double x);

	public T getBorder();

	public String getName();

	public double getRefractiveIndex();

	public Vector2D perpendicular(final double x);

	public void updateBorder(final T border);

	public double value(final double x);	
}
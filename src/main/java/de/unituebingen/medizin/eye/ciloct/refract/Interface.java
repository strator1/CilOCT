package de.unituebingen.medizin.eye.ciloct.refract;

import de.unituebingen.medizin.eye.ciloct.vo.IBoundedFunction;
import java.util.Objects;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author strasser
 */
public class Interface implements IInterface<IBoundedFunction> {
	private final String _name;
	private final double _refractiveIndex;
	private IBoundedFunction _border;

	public Interface(final String name, final IBoundedFunction border, final double refractiveIndex) {
		_name = name;
		_border = border;
		_refractiveIndex = refractiveIndex;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public double getRefractiveIndex() {
		return _refractiveIndex;
	}

	@Override
	public IBoundedFunction getBorder() {
		return _border;
	}

	@Override
	public double value(final double x) {
		return _border.value(x);
	}

	@Override
	public double derivativeValue(final double x) {
		return _border.derivative().value(x);
	}

	@Override
	public Vector2D perpendicular(final double x) {
		final double y = -1d / derivativeValue(x);
		return (y < 0) ? new Vector2D(-1d, -y) : new Vector2D(1d, y);
	}

	@Override
	public double getX1() {
		return _border.getX1();
	}

	@Override
	public double getX2() {
		return _border.getX2();
	}

	@Override
	public void updateBorder(final IBoundedFunction border) {
		_border = border;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this._name);
		hash = 67 * hash + (int) (Double.doubleToLongBits(this._refractiveIndex) ^ (Double.doubleToLongBits(this._refractiveIndex) >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Interface other = (Interface) obj;
		if (!Objects.equals(this._name, other._name)) {
			return false;
		}
		return Double.doubleToLongBits(this._refractiveIndex) == Double.doubleToLongBits(other._refractiveIndex);
	}
}
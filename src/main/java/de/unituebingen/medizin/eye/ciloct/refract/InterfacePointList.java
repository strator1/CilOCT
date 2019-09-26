package de.unituebingen.medizin.eye.ciloct.refract;

import de.unituebingen.medizin.eye.ciloct.util.MathUtil;
import de.unituebingen.medizin.eye.ciloct.vo.PointList;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author strasser
 */
public class InterfacePointList implements IInterface<PointList> {
	private final String _name;
	private final double _refractiveIndex;
	private PointList _points;

	public InterfacePointList(final String name, final PointList border, final double refractiveIndex) {
		_name = name;
		_points = border;
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
	public PointList getBorder() {
		return _points;
	}

	@Override
	public double value(final double x) {
		return _points
			.stream()
			.filter(p -> Math.abs(p.getX() - x) < 0.1)
			.mapToDouble(p -> p.getY())
			.average()
			.orElseGet(() -> {
				throw new UnsupportedOperationException("Failed to get value");
			}
		);
	}

	@Override
	public double derivativeValue(final double x) {
		return
			_points
				.stream()
				.filter(p -> Math.abs(p.getX() - x) < 10)
				.collect(collectingAndThen(toList(), MathUtil::fitPolynomialSpline))
			.derivative()
				.value(x);
	}

	@Override
	public Vector2D perpendicular(final double x) {
		final double y = -1d / derivativeValue(x);
		return (y < 0) ? new Vector2D(-1d, -y) : new Vector2D(1d, y);
	}

	@Override
	public double getX1() {
		return _points.getX1();
	}

	@Override
	public double getX2() {
		return _points.getX2();
	}

	@Override
	public void updateBorder(final PointList border) {
		_points = border;
	}

	@Override
	public boolean isValidPoint(final double x) {
		return _points.stream().anyMatch(p -> Math.abs(p.getX() - x) < 0.1);
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
		final InterfacePointList other = (InterfacePointList) obj;
		if (!Objects.equals(this._name, other._name)) {
			return false;
		}
		return Double.doubleToLongBits(this._refractiveIndex) == Double.doubleToLongBits(other._refractiveIndex);
	}
}
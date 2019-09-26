package de.unituebingen.medizin.eye.ciloct.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author strasser
 */
public class ProfilePoint {
	private final String _type;
	private String _name;
	private boolean _corrected;
	private final Double _x;
	private final Point _p1;
	private final Point _p2;
	private final Double _width;
	private final Double _k1;
	private final Double _k2;
	private final Double _arclength;

	public ProfilePoint(final Double x, final Point p1, final Double arclength, final Double k1, final Point p2, final Double k2, final Double width, final String type) {
		_x = x;
		_p1 = p1;
		_arclength = arclength;
		_k1 = k1;
		_p2 = p2;
		_k2 = k2;
		_width = width;
		_type = type;
	}

	public ProfilePoint(final Double x, final Point p1, final Double arclength, final Double k1, final String type) {
		this(x, p1, arclength, k1, null, null, null, type);
	}

	public ProfilePoint(final Double x, final String type) {
		this(x, null, null, null, type);
	}

	public String getType() {
		return _type;
	}

	public String getName() {
		return _name;
	}

	public boolean isCorrected() {
		return _corrected;
	}

	public double getOriginalX() {
		return _x;
	}

	public Point getP1() {
		return _p1;
	}

	public Point getP2() {
		return _p2;
	}

	public Double getWidth() {
		return _width;
	}

	public Double getArcLength() {
		return _arclength;
	}

	public Double getK1() {
		return _k1;
	}

	public Double getK2() {
		return _k2;
	}

	void setName(final String name) {
		_name = name;
	}

	void setCorrected(final boolean corrected) {
		_corrected = corrected;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getOriginalX())
			.append(getName())
			.append(getType())
			.append(isCorrected())
			.toHashCode()
		;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProfilePoint == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		final ProfilePoint point = (ProfilePoint) obj;
		return new EqualsBuilder()
			.append(getType(), point.getType())
			.append(getOriginalX(), point.getOriginalX())
			.append(getName(), point.getName())
			.append(isCorrected(), point.isCorrected())
			.isEquals()
		;
	}

	@Override
	public String toString() {
		return String.format(
			"%s\t%b\t%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.5f\t%.2f\t%.2f\t%.5f\t%.2f",
			getName(),
			isCorrected(),
			getType(),
			getOriginalX(),
			getP1() != null ? getP1().getX() : null,
			getP1() != null ? getP1().getY() : null,
			getP1() != null ? getArcLength(): null,
			getP1() != null ? getK1() : null,
			getP2() != null ? getP2().getX() : null,
			getP2() != null ? getP2().getY() : null,
			getP2() != null ? getK2() : null,
			getP1() != null && getP2() != null ? getWidth() : null
		).replaceAll("null", "").replaceAll("nu", "");
	}
}
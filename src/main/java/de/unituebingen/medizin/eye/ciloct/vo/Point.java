package de.unituebingen.medizin.eye.ciloct.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * @author strasser
 */
public class Point extends WeightedObservedPoint implements Clusterable {
	public Point(final double x,final double y) {
		super(1.0, x, y);
	}

	public Point(final double weight, final double x,final double y) {
		super(weight, x, y);
	}

	public Point(final Vector2D vector) {
		this(vector.getX(), vector.getY());
	}

	public int getRoundedX() {
		return (int) Math.round(getX());
	}

	public int getRoundedY() {
		return (int) Math.round(getY());
	}

	public Vector2D toVector() {
		return new Vector2D(getX(), getY());
	}

	@Override
	public double[] getPoint() {
		return new double[] {getX(), getY()};
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Point == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		final Point point = (Point) obj;
		return new EqualsBuilder()
			.append(getX(), point.getX())
			.append(getY(), point.getY())
			.isEquals()
		;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getX()).append(getY()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("x", getX()).append("y", getY()).toString();
	}


	public static int compareX(final WeightedObservedPoint p1, final WeightedObservedPoint p2) {
		return Double.compare(p1.getX(), p2.getX());
	}

	public static int compareY(final WeightedObservedPoint p1, final WeightedObservedPoint p2) {
		return Double.compare(p1.getY(), p2.getY());
	}
}
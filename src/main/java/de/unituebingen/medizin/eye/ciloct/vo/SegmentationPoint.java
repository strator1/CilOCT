package de.unituebingen.medizin.eye.ciloct.vo;

import de.unituebingen.medizin.eye.ciloct.GroundTruthPalette;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author strasser
 */
public class SegmentationPoint extends Point {
	private final GroundTruthPalette _clazz;
	private final double _probability;

	public SegmentationPoint(final double x, final double y, final GroundTruthPalette clazz, final double probability) {
		super(x, y);

		_clazz = clazz;
		_probability = probability;
	}

	public double getProbability() {
		return _probability;
	}

	public GroundTruthPalette getClazz() {
		return _clazz;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("x", getX()).append("y", getY()).append("Class", getClazz()).append("Probability %", _probability * 100d).toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Point == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		final SegmentationPoint point = (SegmentationPoint) obj;
		return new EqualsBuilder()
			.append(getX(), point.getX())
			.append(getY(), point.getY())
			.append(getProbability(), point.getProbability())
			.append(getClazz(), point.getClazz())
			.isEquals()
		;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getX()).append(getY()).append(getProbability()).append(getClazz()).toHashCode();
	}
}
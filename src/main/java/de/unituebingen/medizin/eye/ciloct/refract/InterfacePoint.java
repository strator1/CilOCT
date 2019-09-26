package de.unituebingen.medizin.eye.ciloct.refract;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author strasser
 */
class InterfacePoint extends Point {
	private final IInterface<?> _interface;

	InterfacePoint(final double x, final double y, final IInterface<?> iface) {
		super(x, y);
		_interface = iface;
	}

	InterfacePoint(final Vector2D corrected, final IInterface<?> iface) {
		super(corrected);
		_interface = iface;
	}

	IInterface<?> getInterface() {
		return _interface;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof InterfacePoint == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		final InterfacePoint point = (InterfacePoint) obj;
		return new EqualsBuilder()
			.appendSuper(super.equals(obj))
			.append(getInterface(), point.getInterface())
			.append(getY(), point.getY())
			.isEquals()
		;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(getInterface()).toHashCode();
	}
}
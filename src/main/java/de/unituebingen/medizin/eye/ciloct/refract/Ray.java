package de.unituebingen.medizin.eye.ciloct.refract;

import de.unituebingen.medizin.eye.ciloct.draw.IDrawable;
import de.unituebingen.medizin.eye.ciloct.draw.LineDrawable;
import de.unituebingen.medizin.eye.ciloct.vo.Line;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;
import javafx.scene.paint.Color;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author strasser
 */
class Ray {
	private final List<InterfacePoint> _points = new ArrayList<>(5);

	Ray() {
	}

	void addPoint(final double x, final IInterface<?> iface) {
		_points.add(new InterfacePoint(x, iface.value(x), iface));
	}

	InterfacePoint getPoint(final int i) {
		return _points.get(i);
	}

	List<InterfacePoint> getPoints() {
		return Collections.unmodifiableList(_points);
	}

	int count() {
		return _points.size();
	}

	void refract(final int start) {
		if (start < _points.size()-1) {
			final IInterface<?> from = _points.get(start-1).getInterface();
			final IInterface<?> to = _points.get(start).getInterface();
			final Vector2D startPoint = _points.get(start-1).toVector();
			final Vector2D incidentPoint = _points.get(start).toVector();

			if (to.isValidPoint(incidentPoint.getX())) {
				final Vector2D imagePoint = _points.get(start+1).toVector();
				final Vector2D perpendicular = to.perpendicular(incidentPoint.getX());

				final double theta_i = Vector2D.angle(incidentPoint.subtract(startPoint), perpendicular);
				final double theta_r = Math.asin((Math.sin(theta_i) * from.getRefractiveIndex()) / to.getRefractiveIndex());
				final double rotationAngle = theta_r - theta_i;
				final double cosAngle = Math.cos(rotationAngle);
				final double sinAngle = Math.sin(rotationAngle);

				final Vector2D p = imagePoint.subtract(incidentPoint);
				final Vector2D corrected = 
					new Vector2D(p.getX() * cosAngle - p.getY() * sinAngle, p.getY() * cosAngle - p.getX() * sinAngle)
						.scalarMultiply(1d/to.getRefractiveIndex())
						.add(incidentPoint)
				;

				_points.set(start+1, new InterfacePoint(corrected, _points.get(start+1).getInterface()));

				final Vector2D shift = imagePoint.subtract(corrected);
				IntStream
					.range(start+2, count())
					.forEach(i -> _points.set(i, new InterfacePoint(_points.get(i).toVector().subtract(shift), _points.get(i).getInterface())))
				;
			} else {
				_points.retainAll(_points.subList(0, start));
			}
		}
	}

	void debug(final List<IDrawable> layer, final int start, final IInterface<?> iface) {
		final Vector2D startPoint = _points.get(start-1).toVector();
		final Vector2D incidentPoint = _points.get(start).toVector();
		if (iface.isValidPoint(incidentPoint.getX())) {
			final Vector2D correctedPoint = _points.get(start+1).toVector();
			final Vector2D perpendicular = iface.perpendicular(incidentPoint.getX());

			layer.add(new LineDrawable(new Line(startPoint, incidentPoint), Color.YELLOW));
			layer.add(new LineDrawable(new Line(incidentPoint, incidentPoint.add(perpendicular.normalize().scalarMultiply(10))), Color.GREENYELLOW));
			layer.add(new LineDrawable(new Line(incidentPoint, incidentPoint.add(perpendicular.normalize().scalarMultiply(-10))), Color.GREENYELLOW));
			layer.add(new LineDrawable(new Line(incidentPoint, correctedPoint), Color.RED));
		}
	}
}
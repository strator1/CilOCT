package de.unituebingen.medizin.eye.ciloct.vo;

/**
 * @author strasser
 */
public class Angle {
	private final double _angle;
	private final double _startAngle;
	private final Point _point;

	public Angle(final double angle, final double startAngle, final Point p) {
		_angle = angle;
		_startAngle = startAngle;
		_point = p;
	}

	public Point getCenter() {
		return _point;
	}

	public double getStartAngle() {
		return _startAngle;
	}

	public double getAngle() {
		return _angle;
	}
}
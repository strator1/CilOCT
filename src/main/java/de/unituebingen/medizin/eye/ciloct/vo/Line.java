package de.unituebingen.medizin.eye.ciloct.vo;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author strasser
 */
public class Line implements Comparable<Line> {
	private final Point _start;
	private final Point _end;

	public Line(final double x1, final double y1, final double x2, final double y2) {
		this(new Point(x1, y1), new Point(x2, y2));
	}

	public Line(final Vector2D start, final Vector2D end) {
		this(start.getX(), start.getY(), end.getX(), end.getY());
	}

	public Line(final Point start, final Point end) {
		_start = start;
		_end = end;
	}

	public Point getStart() {
		return _start;
	}

	public Point getEnd() {
		return _end;
	}

	public double getLength() {
		final double x1 = _start.getX();
		final double x2 = _end.getX();
		final double y1 = _start.getY();
		final double y2 = _end.getY();
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	@Override
	public int compareTo(final Line line) {
		return Double.compare(getLength(), line.getLength());
	}
}
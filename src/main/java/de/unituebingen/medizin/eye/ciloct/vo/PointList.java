package de.unituebingen.medizin.eye.ciloct.vo;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * @author strasser
 */
public class PointList extends Vector<Point> implements IBounds {
	public PointList() {		
	}

	public PointList(final Collection<? extends Point> c) {
		super(c);
	}

	public PointList(final IBoundedFunction function, final double resolution) {
		for (double x = function.getX1(); x < function.getX2(); x+=resolution) {
			add(new Point(x, function.value(x)));
		}
	}

	@Override
	public double getX1() {
		//return Collections.min(this, Point::compareX).getX();
		final Point a = get(0);
		final Point b = get(size()-1);
		return (Point.compareX(a, b) <= 0 ? a : b).getX();
	}

	@Override
	public double getX2() {
		//return Collections.max(this, Point::compareX).getX();
		final Point a = get(0);
		final Point b = get(size()-1);
		return (Point.compareX(a, b) > 0 ? a : b).getX();
	}

	public double getMinX() {
		return Collections.min(this, Point::compareX).getX();
	}

	public double getMaxX() {
		return Collections.max(this, Point::compareX).getX();
	}

	public double getMinY() {
		return Collections.min(this, Point::compareY).getY();
	}

	public double getMaxY() {
		return Collections.max(this, Point::compareY).getY();
	}
}
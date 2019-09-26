package de.unituebingen.medizin.eye.ciloct.util;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.function.Consumer;

/**
 * @author strasser
 */
public class PointAverager implements Consumer<Point> {
	private double _sumX = 0d;
	private double _sumY = 0d;
	private int _count;

	@Override
	public void accept(final Point p) {
		_sumX += p.getX();
		_sumY += p.getY();
		_count++;
	}

	public Point average() {
		return new Point(_sumX / _count, _sumY / _count);
	}

	public void combine(final PointAverager a) {
		_sumX += a._sumX;
		_sumY += a._sumY;
		_count += a._count;
	}	
}
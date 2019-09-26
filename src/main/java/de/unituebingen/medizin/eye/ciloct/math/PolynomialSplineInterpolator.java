package de.unituebingen.medizin.eye.ciloct.math;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.List;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import static java.lang.Math.max;

/**
 * @author strasser
 */
public final class PolynomialSplineInterpolator {
	private static final double BANDWIDTH = 0.05;

	private PolynomialSplineInterpolator() {
	}

	public static PolynomialSplineFunction fit(final List<? extends Point> points) {
		final double[] x = new double[points.size()];
		final double[] y = new double[points.size()];

		for (int i = 0; i < points.size(); i++) {
			final Point p = points.get(i);
			x[i] = p.getX();
			y[i] = p.getY();
		}

		return new LoessInterpolator(max(2d/points.size(), BANDWIDTH), 0).interpolate(x, y);
	}
}
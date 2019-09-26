package de.unituebingen.medizin.eye.ciloct.util;

import de.unituebingen.medizin.eye.ciloct.exception.FitException;
import de.unituebingen.medizin.eye.ciloct.math.DifferenceFunction;
import de.unituebingen.medizin.eye.ciloct.math.LinearInterpolator;
import de.unituebingen.medizin.eye.ciloct.math.PolynomialFunction;
import de.unituebingen.medizin.eye.ciloct.math.PolynomialSplineInterpolator;
import de.unituebingen.medizin.eye.ciloct.vo.BoundedPolynomialFunction;
import de.unituebingen.medizin.eye.ciloct.vo.BoundedPolynomialSplineFunction;
import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.abs;
import java.util.ArrayList;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author strasser
 */
public final class MathUtil {
	public static enum AngleOpening {
		LEFT,
		UP
	}

	private MathUtil() {
	}

	public static double calcAngle(final PolynomialFunction f1, final PolynomialFunction f2, final Point point, final AngleOpening opening) {
		Vector2D v1 = new Vector2D(1, f1.derivative().value(point.getX()));
		Vector2D v2 = new Vector2D(1, f2.derivative().value(point.getX()));

//		if (opening == AngleOpening.UP) {
//			if (v1.getY() > 0) {
//				v1 = v1.scalarMultiply(-1);
//			}
//			if (v2.getY() > 0) {
//				v2 = v2.scalarMultiply(-1);
//			}
//		} else if (opening == AngleOpening.LEFT) {
//			if (v1.getX() > 0) {
//				v1 = v1.scalarMultiply(-1);
//			}
//			if (v2.getX() > 0) {
//				v2 = v2.scalarMultiply(-1);
//			}
//		}

		return toDegrees(Vector2D.angle(v1, v2));
	}

	public static double calcAngle(final PolynomialFunction f, final Point point) {
//		final double m = f.derivative().value(point.getX());
//		final Vector2D v = new Vector2D(1, m);
//		final Vector2D u = new Vector2D(1, 0);
//		System.out.printf("%.2f° : %.2f°\n", toDegrees(Vector2D.angle(u, v)), toDegrees(abs(atan(f.derivative().value(point.getX())))));
//		return toDegrees(Vector2D.angle(u, v));
		return toDegrees(abs(atan(f.derivative().value(point.getX()))));
	}

	public static double[] intersect(final DifferentiableUnivariateFunction f1, final DifferentiableUnivariateFunction f2, final double min, final double max) {
		final List<Double> zeros = new ArrayList<>(1);
		final BrentSolver solver = new BrentSolver();
		final DifferenceFunction function = new DifferenceFunction(f1, f2);

		double prevX = min+0.001;
		try {
			UnivariateSolverUtils.verifySequence(min, prevX, max);
		} catch (NumberIsTooLargeException e) {
			return new double[0];
		}
		while(prevX < max) {
			final double x;
			try {
			x = solver.solve(100, function, min, max, prevX);
			} catch (NoBracketingException e) {
				break;
			}
			if (Long.compare(Math.round(x*100000000d), Math.round(prevX*100000000d)) == 0) {
				break;
			} else {
				zeros.add(x);
				prevX = x;
			}
		}

		return zeros.stream().mapToDouble(d -> d).toArray();
	}

	public static BoundedPolynomialFunction fitTail(final int degree, final UnivariateFunction function, final double start, final double end) {
		return fitTail(degree, function, start, (int) Math.round(end - start));
	}

	public static BoundedPolynomialFunction fitTail(final int degree, final UnivariateFunction function, final double start, final int length) {
		return new BoundedPolynomialFunction(
			PolynomialCurveFitter
				.create(degree)
				.fit(
					DoubleStream
						.iterate(start, x -> x + 1d)
						.limit(length)
						.filter(x -> {
							try {
								function.value(x);
								return true;
							} catch (OutOfRangeException e) {
								return false;
							}
						})
						.mapToObj(x -> new Point(x, function.value(x)))
						.collect(Collectors.toList())
				),
			start,
			start + length
		);
	}

	public static PolynomialFunction fitTail(final List<Point> points) {
		return fitTail(1, points);
	}

	public static PolynomialFunction fitTail(final int degree, final List<Point> points) {
		if (degree == 1) {
			return LinearInterpolator.fit(points);
		} else {
			return new PolynomialFunction(
				PolynomialCurveFitter
					.create(degree)
					.fit(
						points
							.stream()
							.sorted(Point::compareX)
							.map(p -> (WeightedObservedPoint) p)
							.collect(Collectors.toList())
					)
			);
		}
	}

	public static BoundedPolynomialSplineFunction fitPolynomialSpline(final List<Point> points) throws FitException {
		try {
			Util.findDuplicateX(points).forEach(x -> {
				final DescriptiveStatistics stats = new DescriptiveStatistics();
				points.stream().filter(p -> x.equals(p.getX())).forEach(p -> stats.addValue(p.getY()));
				points.removeIf(p -> x.equals(p.getX()));
				points.add(new Point(x, stats.getMean()));
			});

			Collections.sort(points, Point::compareX);
			final double minX = points.get(0).getX();
			final double maxX = points.get(points.size()-1).getX();

			return new BoundedPolynomialSplineFunction(PolynomialSplineInterpolator.fit(points), minX, maxX);
		} catch (NullArgumentException | NumberIsTooSmallException | DimensionMismatchException | NonMonotonicSequenceException | java.lang.IndexOutOfBoundsException e) {
			throw new FitException("Could not fit spline.", e);
		}
	}

	public static double arclength(final PolynomialSplineFunction f, final double start, final double end) {
		final UnivariateIntegrator integrator = new SimpsonIntegrator();
		final UnivariateFunction f1 = f.derivative();
		final UnivariateFunction lenFunction = (x) -> Math.sqrt(1d + Math.pow(f1.value(x), 2));
		return integrator.integrate(Integer.MAX_VALUE, lenFunction, start, end);
	}

	public static double distance(final Point a, final Point b) {
		final double dx = a.getX() - b.getX();
		final double dy = a.getY() - b.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
}
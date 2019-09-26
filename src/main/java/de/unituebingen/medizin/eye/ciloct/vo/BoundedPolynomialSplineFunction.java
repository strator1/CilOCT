package de.unituebingen.medizin.eye.ciloct.vo;

import de.unituebingen.medizin.eye.ciloct.util.MathUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

/**
 * @author strasser
 */
public class BoundedPolynomialSplineFunction extends PolynomialSplineFunction implements IBoundedFunction {
	private final UnivariateFunction LENGTH_FUNCTION = (x) -> Math.sqrt(1d + Math.pow(derivative().value(x), 2));

	private final double _x1;
	private final double _x2;

	private UnivariateFunction _deriviative;

	public BoundedPolynomialSplineFunction(final PolynomialSplineFunction function, final double x1, final double x2) throws NullArgumentException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException {
		super(function.getKnots(), function.getPolynomials());
		_x1 = x1;
		_x2 = x2;
	}

	@Override
	public double getX1() {
		return _x1;
	}

	@Override
	public double getX2() {
		return _x2;
	}

	public List<Point> intersect(final DifferentiableUnivariateFunction f) {
		final double[] zeros = MathUtil.intersect(this, f, _x1, _x2);
		return Arrays.stream(zeros).filter(this::isValidPoint).mapToObj(x -> new Point(x, value(x))).collect(Collectors.toList());
	}

	public double arclength() throws IllegalArgumentException {
		return arclength(_x1, _x2);
	}

	@Override
	public UnivariateFunction derivative() {
		if (_deriviative == null) {
			_deriviative = super.derivative();
		}
		return _deriviative;
	}

	public double arclength(final double start, final double end) throws IllegalArgumentException {
		if (isValidPoint(start) && isValidPoint(end)) {
			final UnivariateIntegrator integrator = new SimpsonIntegrator();
			return integrator.integrate(Integer.MAX_VALUE, LENGTH_FUNCTION, start, end);
		} else {
			throw new IllegalArgumentException(String.format("Start or end [%.2f - %.2f] not within function bounds [%.2f - %.2f].", start, end, _x1, _x2));
		}
	}

	public double integrate() throws IllegalArgumentException {
		return integrate(_x1, _x2);
	}

	public double integrate(final double start, final double end) throws IllegalArgumentException {
		if (isValidPoint(start) && isValidPoint(end)) {
			final UnivariateIntegrator integrator = new SimpsonIntegrator();
			return integrator.integrate(Integer.MAX_VALUE, this, start, end);
		} else {
			throw new IllegalArgumentException(String.format("Start or end [%.2f - %.2f] not within function bounds [%.2f - %.2f].", start, end, _x1, _x2));
		}
	}

	public double curvature(final double x) {
		final PolynomialSplineFunction f1 = polynomialSplineDerivative();
		final PolynomialSplineFunction f2 = f1.polynomialSplineDerivative();
		final double v = f1.value(x);
		return f2.value(x) / Math.pow(1d + v * v, 1.5);
	}
}
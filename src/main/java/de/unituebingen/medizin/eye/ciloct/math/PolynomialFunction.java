package de.unituebingen.medizin.eye.ciloct.math;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

/**
 * @author strasser
 */
public class PolynomialFunction extends org.apache.commons.math3.analysis.polynomials.PolynomialFunction {
	public PolynomialFunction(final double[] c) throws NullArgumentException, NoDataException {
		super(c);
	}

	public PolynomialFunction reciprocal() throws IllegalArgumentException {
		if (degree() != 1) {
			throw new IllegalArgumentException("Only polynomials of degree 1 allowed.");
		} else {
			final double a = getCoefficients()[1];
			final double b = getCoefficients()[0];
			return new PolynomialFunction(new double[] {-b/a, 1d/a});
		}
	}

	public List<Point> intersect(final PolynomialFunction f, final double x1, final double x2) {
		final org.apache.commons.math3.analysis.polynomials.PolynomialFunction diff = subtract(f);

		final List<Double> zeros = new ArrayList<>(1);
		final BisectionSolver solver = new BisectionSolver();

		double prevX = x1;
		while(prevX < x2) {
			final double x = solver.solve(100, diff, prevX, x2);
			if (Long.compare(Math.round(x*100000d), Math.round(prevX*100000d)) == 0) {
				break;
			} else {
				zeros.add(x);
				prevX = x;
			}
		}

		return zeros.stream().map(x -> new Point(x, value(x))).collect(Collectors.toList());
	}
}
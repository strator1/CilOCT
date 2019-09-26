package de.unituebingen.medizin.eye.ciloct.vo;

import de.unituebingen.medizin.eye.ciloct.math.PolynomialFunction;
import java.util.List;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

/**
 * @author strasser
 */
public class BoundedPolynomialFunction extends PolynomialFunction implements IBoundedFunction {
	private final double _x1;
	private final double _x2;

	public BoundedPolynomialFunction(final double[] coeffs, final double x1, final double x2) throws NullArgumentException, NoDataException {
		super(coeffs);

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

	public List<Point> intersect(final PolynomialFunction f) {
		return intersect(f, _x1, _x2);
	}
}
package de.unituebingen.medizin.eye.ciloct.math;

import de.unituebingen.medizin.eye.ciloct.vo.IBoundedFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * @author strasser
 */
public final class DifferenceFunction implements DifferentiableUnivariateFunction {
	private final DifferentiableUnivariateFunction _f1;
	private final DifferentiableUnivariateFunction _f2;

	public DifferenceFunction(final DifferentiableUnivariateFunction f1, final DifferentiableUnivariateFunction f2) {
		_f1 = f1;
		_f2 = f2;
	}

	@Override
	public UnivariateFunction derivative() {
		return x -> _f1.derivative().value(x) - _f2.derivative().value(x);
	}

	@Override
	public double value(double x) {
		return _f1.value(x) - _f2.value(x);
	}
}
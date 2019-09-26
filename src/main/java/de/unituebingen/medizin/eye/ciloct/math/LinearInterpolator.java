package de.unituebingen.medizin.eye.ciloct.math;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.List;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author strasser
 */
public final class LinearInterpolator {
	private LinearInterpolator() {	
	}

	public static PolynomialFunction fit(final List<Point> points) {
		final SimpleRegression sr = new SimpleRegression(true);
		points.forEach(p -> sr.addData(p.getX(), p.getY()));
		return new PolynomialFunction(sr.regress().getParameterEstimates());
//		return new PolynomialFunction(
//			PolynomialCurveFitter
//				.create(2)
//				.fit(points.stream().map(p -> new WeightedObservedPoint(1, p.getX(), p.getY())).collect(Collectors.toList()))
//		);
	}
}
package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.math.PolynomialFunction;
import de.unituebingen.medizin.eye.ciloct.vo.BoundedPolynomialFunction;
import de.unituebingen.medizin.eye.ciloct.vo.IBoundedFunction;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import java.util.stream.DoubleStream;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public final class FunctionDrawable extends AbstractDrawable {
	private static final int STEP = 10;
	private static final int RADIUS = 1;

	private final IBoundedFunction _f;

	public FunctionDrawable(final PolynomialFunction f, final Paint paint, final double x1, final double x2) {
		this(new BoundedPolynomialFunction(f.getCoefficients(), x1, x2), paint);
	}

	public FunctionDrawable(final IBoundedFunction f, final Paint paint) {
		super(paint);

		_f = f;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		final double start = floor(_f.getX1());
		final long limit = round(ceil(_f.getX2()) - start) * STEP;
		gc.setFill(getPaint());
		DoubleStream
			.iterate(start, x -> x + 1d/STEP)
			.limit(limit)
			.filter(x -> x > _f.getX1() && x < _f.getX2())
			.forEach(x -> gc.fillOval(x-RADIUS, _f.value(x)-RADIUS, RADIUS+RADIUS, RADIUS+RADIUS));
	}
}
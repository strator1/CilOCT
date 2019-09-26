package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Angle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;

/**
 * @author strasser
 */
public final class AngleDrawable extends AbstractDrawable {
	private final Angle _angle;
	private final int _radius;

	public AngleDrawable(final Angle angle, final int radius, final Paint paint) {
		super(paint);
		_radius = radius;
		_angle = angle;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		final double x = _angle.getCenter().getX();
		final double y = _angle.getCenter().getY();
		final double angle = _angle.getAngle();
		final double startAngle = _angle.getStartAngle();

		gc.setStroke(getPaint());
		gc.strokeArc(x-_radius, y-_radius, _radius+_radius, _radius+_radius, 180-startAngle-angle, angle, ArcType.OPEN);
		gc.strokeText(String.format("%.0f°", _angle.getAngle()), x+_radius+5, y);
	}
}
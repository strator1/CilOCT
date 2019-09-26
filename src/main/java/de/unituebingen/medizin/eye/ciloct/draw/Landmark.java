package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public class Landmark extends AbstractDrawable {
	private final static int RADIUS = 4;

	private final Point _point;

	public Landmark(final Point point, final Paint paint) {
		super(paint);

		_point = point;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(getPaint());
		gc.strokeOval(_point.getX()-RADIUS, _point.getY()-RADIUS, RADIUS+RADIUS, RADIUS+RADIUS);
	}
}
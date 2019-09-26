package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public class PointDrawable extends AbstractDrawable {
	private static final int RADIUS = 2;

	private final Point _point;

	public PointDrawable(final Point point, final Paint paint) {
		super(paint);
		_point = point;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setFill(getPaint());
		gc.fillOval(_point.getX()-RADIUS, _point.getY()-RADIUS, RADIUS+RADIUS, RADIUS+RADIUS);
	}	
}
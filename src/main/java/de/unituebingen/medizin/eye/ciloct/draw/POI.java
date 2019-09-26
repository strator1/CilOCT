package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public final class POI extends AbstractDrawable {
	private final static int LENGTH = 4;

	private final Point _point;

	public POI(final Point point, final String title, final Paint paint) {
		super(paint);
		_point = point;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(getPaint());
		gc.strokeLine(_point.getX()-LENGTH, _point.getY()-LENGTH, _point.getX()+LENGTH, _point.getY()+LENGTH);
		gc.strokeLine(_point.getX()+LENGTH, _point.getY()-LENGTH, _point.getX()-LENGTH, _point.getY()+LENGTH);
	}
}
package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public final class Label extends AbstractDrawable {
	private final Point _point;
	private final String _title;

	public Label(final Point point, final String title, final Paint paint) {
		super(paint);
		_point = point;
		_title = title;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(getPaint());
		gc.strokeText(_title, _point.getX(), _point.getY());
	}
}
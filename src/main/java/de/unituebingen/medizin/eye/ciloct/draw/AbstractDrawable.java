package de.unituebingen.medizin.eye.ciloct.draw;

import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public abstract class AbstractDrawable implements IDrawable {
	private Paint _paint;

	public AbstractDrawable(final Paint paint) {
		setPaint(paint);
	}

	@Override
	public final void setPaint(final Paint paint) {
		_paint = paint;
	}

	protected final Paint getPaint() {
		return _paint;
	}
}
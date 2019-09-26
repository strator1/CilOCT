package de.unituebingen.medizin.eye.ciloct.draw;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public interface IDrawable {
	public void draw(final GraphicsContext gc);
	public void setPaint(final Paint paint);
}
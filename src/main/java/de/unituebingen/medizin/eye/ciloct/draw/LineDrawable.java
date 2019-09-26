package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Line;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public class LineDrawable extends AbstractDrawable {
	private final Line _line;

	public LineDrawable(final Line line, final Paint paint) {
		super(paint);
		_line = line;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		final double x1 = _line.getStart().getX();
		final double y1 = _line.getStart().getY();
		final double x2 = _line.getEnd().getX();
		final double y2 = _line.getEnd().getY();

		gc.setStroke(getPaint());
		gc.strokeLine(x1, y1, x2, y2);
	}
}
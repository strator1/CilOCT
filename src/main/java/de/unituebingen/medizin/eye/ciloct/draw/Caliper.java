package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.vo.Line;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;

/**
 * @author strasser
 */
public class Caliper extends LineDrawable {
	private final Line _line;

	public Caliper(final Line line, final Paint paint) {
		super(line, paint);
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
		gc.strokeText(String.format("%.1f px", _line.getLength()), x2-10, y2 - 10);
		gc.save();

		final double m = -(x2 - x1) / (y2 - y1);
		final double alpha = Math.toDegrees(Math.atan(m));
		gc.rotate(alpha);

		final Rotate r = new Rotate(alpha, x1, y1);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		gc.strokeLine(x1-5, y1, x1+5, y1);

		final Rotate r1 = new Rotate(alpha, x2, y2);
		gc.setTransform(r1.getMxx(), r1.getMyx(), r1.getMxy(), r1.getMyy(), r1.getTx(), r1.getTy());
		gc.strokeLine(x2-5, y2, x2+5, y2);

		gc.restore();
	}
}
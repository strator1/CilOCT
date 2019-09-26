package de.unituebingen.medizin.eye.ciloct.draw;

import de.unituebingen.medizin.eye.ciloct.util.PointAverager;
import de.unituebingen.medizin.eye.ciloct.vo.Point;
import de.unituebingen.medizin.eye.ciloct.vo.PointList;
import java.util.Optional;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public final class PointListDrawable extends AbstractDrawable {
	private static final int RADIUS = 1;

	private final PointList _points;
	private final Optional<String> _label;

	public PointListDrawable(final PointList points, final Paint paint) {
		this(null, points, paint);
	}

	public PointListDrawable(final String label, final PointList points, final Paint paint) {
		super(paint);
		_points = points;
		_label = Optional.ofNullable(label);
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setFill(getPaint());
		_points.forEach(p -> gc.fillOval(p.getX()-RADIUS, p.getY()-RADIUS, RADIUS+RADIUS, RADIUS+RADIUS));

		_label.ifPresent(label -> {
			final Point p = _points.stream().collect(PointAverager::new, PointAverager::accept, PointAverager::combine).average();
			new Label(p, label, Color.RED).draw(gc);
		});
	}
}
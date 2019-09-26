package de.unituebingen.medizin.eye.ciloct.events;

import de.unituebingen.medizin.eye.ciloct.CilOCTController;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author strasser
 */
public abstract class RepaintEventHandler implements EventHandler<RepaintEvent> {
	@Override
	public final void handle(final RepaintEvent event) {
		final Canvas canvas = (Canvas) event.getTarget();
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		synchronized(CilOCTController.class) {
			repaint(gc);
		}
	}

	protected abstract void repaint(final GraphicsContext gc);
}
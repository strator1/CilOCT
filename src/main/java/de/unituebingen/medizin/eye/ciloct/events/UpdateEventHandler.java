package de.unituebingen.medizin.eye.ciloct.events;

import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;

/**
 *
 * @author strasser
 */
public abstract class UpdateEventHandler implements EventHandler<UpdateEvent> {
	@Override
	public final void handle(final UpdateEvent event) {
		final XYChart<Double, Double> chart = (XYChart<Double, Double>) event.getTarget();
		final XYChart.Series<Double, Double> series = chart.getData().get(0);
		series.getData().clear();

		update(series);
	}

	protected abstract void update(final XYChart.Series<Double, Double> series);	
}
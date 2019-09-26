package de.unituebingen.medizin.eye.ciloct.events;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;

/**
 * @author strasser
 */
public final class UpdateManager {
	private UpdateManager() {
	}

	public static void register(final Canvas canvas, final Observable ... observables) {
		Arrays.stream(observables).forEach(o -> o.addListener(o1 -> Platform.runLater(() -> canvas.fireEvent(new RepaintEvent(o1)))));
	}
}
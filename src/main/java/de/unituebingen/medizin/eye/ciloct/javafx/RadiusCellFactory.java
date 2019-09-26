package de.unituebingen.medizin.eye.ciloct.javafx;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

/**
 * @author strasser
 */
public class RadiusCellFactory implements Callback<ListView<Integer>, ListCell<Integer>> {
	@Override
	public ListCell<Integer> call(final ListView<Integer> param) {
		return new ListCell<Integer>() {
			{
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			}

			@Override
			protected void updateItem(final Integer radius, final boolean empty) {
				super.updateItem(radius, empty);
				if (radius != null) {
					setGraphic(new Circle(radius, Color.BLACK));
//					setText(String.format("%d px", radius));
				}
			}
		};
	}
}
package de.unituebingen.medizin.eye.ciloct.javafx;

import com.pixelduke.control.ribbon.QuickAccessBar;
import java.util.Collection;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * @author strasser
 */
public class QuickAccessBarSkin extends SkinBase<QuickAccessBar> {
    private final BorderPane _outerContainer;
    private final HBox _buttonContainer;
    private final HBox _rightButtons;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    public QuickAccessBarSkin(final QuickAccessBar control, final String title) {
        super(control);

        _buttonContainer = new HBox();
        _rightButtons = new HBox();
        _outerContainer = new BorderPane();
        _buttonContainer.getStyleClass().add("button-container");
        _outerContainer.getStyleClass().add("outer-container");

        _outerContainer.setLeft(_buttonContainer);
		_outerContainer.setCenter(new Text(title));
        _outerContainer.setRight(_rightButtons);
        getChildren().add(_outerContainer);
        updateAddedButtons(control.getButtons());
    }

    private void buttonsChanged(final ListChangeListener.Change<? extends Button> changed) {
        while(changed.next()) {
            if (changed.wasAdded()) {
                updateAddedButtons(changed.getAddedSubList());
            }
            if(changed.wasRemoved()) {
				changed.getRemoved().forEach(button -> _buttonContainer.getChildren().remove(button));
            }
        }
    }

    private void updateAddedButtons(final Collection<? extends Button> addedSubList) {
        final QuickAccessBar skinnable = getSkinnable();
		skinnable.getButtons().forEach(button -> _buttonContainer.getChildren().add(button));
		skinnable.getRightButtons().forEach(button -> _rightButtons.getChildren().add(button));
    }
}
package de.unituebingen.medizin.eye.ciloct;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

/**
 * @author strasser
 */
public class ProgressDialogController implements Initializable {
	private final StringProperty _status = new SimpleStringProperty();
	private final DoubleProperty _progress = new SimpleDoubleProperty();
	private final StringProperty _file = new SimpleStringProperty();
	private final IntegerProperty _successfull = new SimpleIntegerProperty(0);
	private final IntegerProperty _failed = new SimpleIntegerProperty(0);

	private Task<?> _task;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private TextField fileTextField;

	@FXML
	private TextField statusTextField;

	@FXML
	private TextField successfullTextField;

	@FXML
	private TextField failedTextField;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		statusTextField.textProperty().bind(_status);
		fileTextField.textProperty().bind(_file);
		progressBar.progressProperty().bind(_progress);
		successfullTextField.textProperty().bind(_successfull.asString());
		failedTextField.textProperty().bind(_failed.asString());
	}

	void setTask(final Task<?> task) {
		_task = task;
	}

	IntegerProperty failedProperty() {
		return _failed;
	}

	IntegerProperty successfullProperty() {
		return _successfull;
	}

	StringProperty statusProperty() {
		return _status;
	}

	DoubleProperty progressProperty() {
		return _progress;
	}

	StringProperty fileProperty() {
		return _file;
	}

	@FXML
	private void cancel(final ActionEvent e) {
		_task.cancel(true);
	}
}
package de.unituebingen.medizin.eye.ciloct.task;

import de.unituebingen.medizin.eye.ciloct.CilOCTController;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import org.apache.commons.lang3.StringUtils;

/**
 * @author strasser
 */
public abstract class ExceptionCatchingTask extends Task<List<Throwable>>{
	private final String _file;
	private final String _header;

	public ExceptionCatchingTask(final ProgressIndicator indicator, final String header, final String file) {
		_file = file;
		_header = header;

		indicator.visibleProperty().unbind();
		indicator.visibleProperty().bind(runningProperty());
	}

	protected Optional<? extends Throwable> call(final Runnable r) {
		try {
			r.run();
			return Optional.empty();
		} catch (Throwable e) {
			return Optional.of(e);
		}
	} 

	@Override
	protected void failed() {
		Logger.getLogger(ExceptionCatchingTask.class.getName()).log(Level.SEVERE, "Task failed");
	}

	@Override
	protected void done() {
		try {
			get()
				.stream()
				.peek(t -> Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, String.format("%s: %s", _file, t.getMessage()), t))
				.map(Throwable::getMessage)
				.collect(
					collectingAndThen(joining("\n"), message -> {
						if (StringUtils.isNotEmpty(message)) {
							Platform.runLater(() -> {
								final Alert alert = new Alert(Alert.AlertType.ERROR);
								alert.setHeaderText(_header);
								alert.setContentText(message);
								alert.showAndWait();
							});
						}
						return null;
					})
				);
		} catch (InterruptedException | ExecutionException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
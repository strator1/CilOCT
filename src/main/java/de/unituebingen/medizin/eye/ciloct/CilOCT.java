package de.unituebingen.medizin.eye.ciloct;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;
import static javafx.application.Application.launch;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.spi.IIORegistry;


public class CilOCT extends Application {
	private double _xOffset = 0d;
	private double _yOffest = 0d;

    @Override
    public void start(final Stage stage) throws Exception {
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setTitle("CilOCT");

		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CilOCT.fxml"));
        final Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("/styles/CilOCT.css");

		final CilOCTController controller = loader.getController();
		controller.quickAccessBar.setOnMousePressed(mouseEvent -> {
			_xOffset = stage.getX() - mouseEvent.getScreenX();
			_yOffest = stage.getY() - mouseEvent.getScreenY();
		});
		controller.quickAccessBar.setOnMouseDragged(mouseEvent -> {
			stage.setX(mouseEvent.getScreenX() + _xOffset);
			stage.setY(mouseEvent.getScreenY() + _yOffest);
		});

		stage.setOnCloseRequest(e -> {
			e.consume();
			controller.exit(new ActionEvent(e.getSource(), e.getTarget()));
		});
		scene.cursorProperty().bind(controller.cursorProperty());
        stage.setScene(scene);

//		ResizeHelper.addResizeListener(stage, controller.cursorProperty());

        stage.show();

		getParameters().getUnnamed().stream().findFirst().ifPresent(filename -> controller.loadImage(new File(filename), true));
    }

	@Override
	public void init() throws Exception {
		try {
			LogManager
				.getLogManager()
				.readConfiguration(
					getClass().getResourceAsStream("/logging.properties")
				)
			;
		} catch (IOException | SecurityException e) {
		}
		IIORegistry.getDefaultInstance().registerServiceProvider(new org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi());
	}

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
		launch(args);
    }
}
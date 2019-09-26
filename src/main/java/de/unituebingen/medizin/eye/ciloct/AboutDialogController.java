package de.unituebingen.medizin.eye.ciloct;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

/**
 * FXML Controller class
 *
 * @author strasser
 */
public class AboutDialogController implements Initializable {
	@FXML
	private WebView webview;

	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		webview.setContextMenuEnabled(false);
		final WebEngine engine = webview.getEngine();
		engine.setJavaScriptEnabled(false);

		engine.getLoadWorker().stateProperty().addListener((final ObservableValue<? extends State> observable, final State oldValue, final State newValue) -> {
			if (newValue == State.SUCCEEDED) {
				final Document doc = engine.getDocument();
				final NodeList nl = doc.getElementsByTagName("a");
				for (int i = 0; i < nl.getLength(); i++) {
					final Node node = nl.item(i);
					final EventTarget target = (EventTarget) node;

					target.addEventListener("click", evt -> {
						final EventTarget et = evt.getCurrentTarget();
						final HTMLAnchorElement anchorElement = (HTMLAnchorElement) et;
						final String href = anchorElement.getHref();
						
						if (href.startsWith("http:") || href.startsWith("https:")) {
							try {
								Desktop.getDesktop().browse(new URL(href).toURI());
							} catch (IOException | URISyntaxException ex) {
								Logger.getLogger(AboutDialogController.class.getName()).log(Level.SEVERE, null, ex);
							}
						} else if (href.startsWith("mailto:")) {
							try {
								Desktop.getDesktop().mail(new URL(href).toURI());
							} catch (IOException | URISyntaxException ex) {
								Logger.getLogger(AboutDialogController.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
						evt.preventDefault();
					}, false);
				}
			}
		});

		engine.load(getClass().getResource("/about.html").toExternalForm());
	}

	@FXML
	private void close(final ActionEvent event) {
		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
	}
}
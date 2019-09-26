package de.unituebingen.medizin.eye.ciloct;

import de.unituebingen.medizin.eye.ciloct.util.MatrixUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * FXML Controller class
 *
 * @author strasser
 */
public class HistogramDialogController implements Initializable {
	@FXML
	private BarChart<String, Double> barChart;

	@FXML
	private CategoryAxis categoryAxis;

	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
	}

	public void setMatrix(final RealMatrix matrix) {
		final double[] histogram = MatrixUtil.histogram(matrix, true);
		final BarChart.Series<String, Double> series = new BarChart.Series<>();
		for (int i = 0; i < histogram.length; i++) {
			series.getData().add(new BarChart.Data<>(Integer.toString(i), histogram[i]));
		}
		barChart.getData().add(series);
	}

	public void setThreshold(final double threshold) {
		final String t = Integer.toString((int) Math.round(threshold));
		
		final BarChart.Series<String, Double> series = new BarChart.Series<>();
		series.getData().add(new BarChart.Data<>(t, 1.1));
		barChart.getData().add(series);
	}
}
package de.unituebingen.medizin.eye.ciloct.serialization;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import de.unituebingen.medizin.eye.ciloct.Brush;
import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.List;

/**
 * @author strasser
 */
@JacksonXmlRootElement(localName = "Landmarks")
public class LandmarksVO {
    @JacksonXmlElementWrapper(localName = "outerCiliaryMuscleBorder")
    @JacksonXmlProperty(localName = "landmark1")
	private List<Point> outerCiliaryMuscleBorderLandmarks;

	@JacksonXmlElementWrapper(localName = "innerCiliaryMuscleBorder")
    @JacksonXmlProperty(localName = "landmark2")
	private List<Point> innerCiliaryMuscleBorderLandmarks;

	@JacksonXmlElementWrapper(localName = "verticalCiliaryMuscleBorder")
    @JacksonXmlProperty(localName = "landmark3")
	private List<Point> verticalCiliaryMuscleBorderLandmarks;

	private Point anteriorChamberStartPoint;

	private boolean newThresholdCalculation;

	private double thresholdFactor = 1.0;

	private double lensThresholdFactor = 1.3;

	@JacksonXmlElementWrapper(localName = "brushes")
    @JacksonXmlProperty(localName = "brush")
	private List<Brush> brushes;

	private Point lensStartPoint;

	public List<Point> getOuterCiliaryMuscleBorderLandmarks() {
		return outerCiliaryMuscleBorderLandmarks;
	}

	public void setOuterCiliaryMuscleBorderLandmarks(final List<Point> outerCiliaryMuscleBorderLandmarks) {
		this.outerCiliaryMuscleBorderLandmarks = outerCiliaryMuscleBorderLandmarks;
	}

	public List<Point> getInnerCiliaryMuscleBorderLandmarks() {
		return innerCiliaryMuscleBorderLandmarks;
	}

	public void setInnerCiliaryMuscleBorderLandmarks(final List<Point> innerCiliaryMuscleBorderLandmarks) {
		this.innerCiliaryMuscleBorderLandmarks = innerCiliaryMuscleBorderLandmarks;
	}

	public List<Point> getVerticalCiliaryMuscleBorderLandmarks() {
		return verticalCiliaryMuscleBorderLandmarks;
	}

	public void setVerticalCiliaryMuscleBorderLandmarks(final List<Point> verticalCiliaryMuscleBorderLandmarks) {
		this.verticalCiliaryMuscleBorderLandmarks = verticalCiliaryMuscleBorderLandmarks;
	}

	public Point getAnteriorChamberStartPoint() {
		return anteriorChamberStartPoint;
	}

	public void setAnteriorChamberStartPoint(final Point anteriorChamberStartPoint) {
		this.anteriorChamberStartPoint = anteriorChamberStartPoint;
	}

	public boolean isNewThresholdCalculation() {
		return newThresholdCalculation;
	}

	public void setNewThresholdCalculation(boolean newThresholdCalculation) {
		this.newThresholdCalculation = newThresholdCalculation;
	}

	public double getThresholdFactor() {
		return thresholdFactor;
	}

	public void setThresholdFactor(final double thresholdFactor) {
		this.thresholdFactor = thresholdFactor;
	}

	public double getLensThresholdFactor() {
		return lensThresholdFactor;
	}

	public void setLensThresholdFactor(final double lensThresholdFactor) {
		this.lensThresholdFactor = lensThresholdFactor;
	}

	public List<Brush> getBrushes() {
		return brushes;
	}

	public void setBrushes(final List<Brush> brushes) {
		this.brushes = brushes;
	}

	public Point getLensStartPoint() {
		return lensStartPoint;
	}

	public void setLensStartPoint(final Point lensStartPoint) {
		this.lensStartPoint = lensStartPoint;
	}
}
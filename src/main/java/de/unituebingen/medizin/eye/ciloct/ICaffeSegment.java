package de.unituebingen.medizin.eye.ciloct;

import de.unituebingen.medizin.eye.ciloct.exception.GradientException;
import de.unituebingen.medizin.eye.ciloct.vo.SegmentationPoint;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * @author strasser
 */
public interface ICaffeSegment {
	public static final GroundTruthPalette[] PALETTE = {
		GroundTruthPalette.INNER_SCLERAL_BORDER,
		GroundTruthPalette.UPPER_IRIS_BORDER,
		GroundTruthPalette.OUTER_CONJUNCTIVA_BORDER,
		GroundTruthPalette.OUTER_CILIARY_MUSCLE_BORDER,
		GroundTruthPalette.INNER_CILIARY_MUSCLE_BORDER,
		GroundTruthPalette.VERTICAL_CILIARY_MUSCLE_BORDER,
		GroundTruthPalette.CILIARY_MUSCLE_AREA,
		GroundTruthPalette.UPPER_LENS_BORDER,
		GroundTruthPalette.LOWER_LENS_BORDER,
		GroundTruthPalette.LENS_AREA,
		GroundTruthPalette.ANTERIOR_CHAMBER_AREA,
		GroundTruthPalette.AIR_AREA
	};

	public Map<GroundTruthPalette, List<SegmentationPoint>> segment(final BufferedImage image) throws GradientException;	
}
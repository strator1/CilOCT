package de.unituebingen.medizin.eye.ciloct.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unituebingen.medizin.eye.ciloct.Brush;
import de.unituebingen.medizin.eye.ciloct.vo.Point;

/**
 * @author strasser
 */
public final class LandmarkMapper {
	private LandmarkMapper() {
	}

	public static XmlMapper create() {
		final XmlMapper mapper = new XmlMapper();

		mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.addMixIn(Point.class, PointMixin.class);
		mapper.addMixIn(Brush.class, BrushMixin.class);
		mapper.setVisibilityChecker(
			mapper
				.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
		);

		return mapper;
	}
}
package de.unituebingen.medizin.eye.ciloct.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author strasser
 */
public abstract class PointMixin {
	@JsonCreator
	public PointMixin(@JsonProperty("x") double x, @JsonProperty("y") double y) {
		
	}
}
package de.unituebingen.medizin.eye.ciloct.serialization;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author strasser
 */
public abstract class BrushMixin {
	@JacksonXmlProperty(localName = "x")
	private int _x;

	@JacksonXmlProperty(localName = "y")
	private int _y;

	@JacksonXmlProperty(localName = "value")
	private int _value;

	@JacksonXmlProperty(localName = "radius")
	private int _radius;	
}
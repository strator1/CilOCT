package de.unituebingen.medizin.eye.ciloct;

import java.util.Arrays;

/**
 * @author strasser
 */
public enum GroundTruthPalette {
	BACKGROUND("background", 0x000000),
	INNER_SCLERAL_BORDER("inner scleral border", 0xffffff),
	UPPER_IRIS_BORDER("upper iris border", 0x00ffff),
	OUTER_CONJUNCTIVA_BORDER("outer conjunctiva border", 0xffff00),
	OUTER_CILIARY_MUSCLE_BORDER("outer ciliary muscle border", 0xff0000),
	INNER_CILIARY_MUSCLE_BORDER("inner ciliary muscle border", 0x00ff00),
	VERTICAL_CILIARY_MUSCLE_BORDER("vertical ciliary muscle border", 0x0000ff),
	CILIARY_MUSCLE_AREA("ciliary muscle area", 0xa0a0a0);

	private final static int ALPHA = 0xff000000;

	private final String _name;
	private final int _rgb;
	
	GroundTruthPalette(final String name, final int rgb) {
		_name = name;
		_rgb = rgb;
	}

	public int rgba() {
		return _rgb | ALPHA;
	}

	public int rgb() {
		return _rgb;
	}

	public static int[] toArray() {
		return Arrays.stream(values()).mapToInt(v -> v.rgb()).toArray();
	}

	public static String[] names() {
		return Arrays.stream(values()).map(v -> v._name).toArray(String[]::new);
	}
}
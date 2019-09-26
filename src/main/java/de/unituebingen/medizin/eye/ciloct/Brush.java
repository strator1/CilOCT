package de.unituebingen.medizin.eye.ciloct;

/**
 * @author strasser
 */
public final class Brush {
	private int _x;
	private int _y;
	private int _value;
	private int _radius;

	public Brush() {
	}

	public Brush(final int x, final int y, final int value, final int radius) {
		_x = x;
		_y = y;
		_value = value;
		_radius = radius;
	}

	public void setX(final int x) {
		_x = x;
	}

	public void setY(final int y) {
		_y = y;
	}

	public void setValue(final int value) {
		_value = value;
	}

	public void setRadius(final int radius) {
		_radius = radius;
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	public int getValue() {
		return _value;
	}

	public int getRadius() {
		return _radius;
	}
}
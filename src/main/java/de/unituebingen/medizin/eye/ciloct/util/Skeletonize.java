package de.unituebingen.medizin.eye.ciloct.util;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import de.unituebingen.medizin.eye.ciloct.vo.PointList;
import java.util.Arrays;

/**
 * @author strasser
 * https://nayefreza.wordpress.com/2013/05/11/zhang-suen-thinning-algorithm-java-implementation/
 */
public class Skeletonize {
	private static final int DEFAULT_SCALE = 1;

	private final boolean[][] _image;
	private final int _height;
	private final int _width;
	private final int _minX;
	private final int _minY;
	private final int _scale;

	private boolean[][] _shadow;

	public Skeletonize(final PointList points, final int scale) {
		_scale = scale;

		_minX = (int) Math.floor(points.getMinX() / _scale);
		_minY = (int) Math.floor(points.getMinY() / _scale);
		_height = (int) Math.ceil(points.getMaxY() / _scale) - _minY + 1;
		_width = (int) Math.ceil(points.getMaxX() / _scale) - _minX + 1;
		
		_image = new boolean[_height][_width];
		points.forEach(p -> _image[p.getRoundedY() / _scale - _minY][p.getRoundedX() / _scale - _minX] = true);

		clearShadow();
	}

	public Skeletonize(final PointList points) {
		this(points, DEFAULT_SCALE);
	}

    /**
     * @return the point list after thinning using zhang-suen thinning algo.
     */
    public PointList doZhangSuenThinning() {
        boolean hasChange;
		do {
            hasChange = false;

			for (int y = 1; y + 1 < _height; y++) {
                for (int x = 1; x + 1 < _width; x++) {
                    final int a = getA(y, x);
                    final int b = getB(y, x);
					
                    if (_image[y][x] && b >= 2 && b <= 6 && a == 1
                            && !(_image[y - 1][x] && _image[y][x + 1] && _image[y + 1][x])
                            && !(_image[y][x + 1] && _image[y + 1][x] && _image[y][x - 1])) {
						_shadow[y][x] = true;
                        hasChange = true;
                    }
                }
            }

			merge();
			clearShadow();

            for (int y = 1; y + 1 < _height; y++) {
                for (int x = 1; x + 1 < _width; x++) {
                    final int a = getA(y, x);
                    final int b = getB(y, x);
                    if (_image[y][x] && b >= 2 && b <= 6 && a == 1
                            && !(_image[y - 1][x] && _image[y][x + 1] && _image[y][x - 1])
                            && !(_image[y - 1][x] && _image[y + 1][x] && _image[y][x - 1])) {
						_shadow[y][x] = true;
                        hasChange = true;
                    }
                }
            }

			merge();
			clearShadow();
        } while (hasChange);

		final PointList result = new PointList();
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				if (_image[y][x]) {
					result.add(new Point((_minX + x) * _scale, (_minY + y) * _scale));
				}
			}
		}

		return result;
    }
 
    private int getA(final int y, final int x) {
		return
			(!_image[y-1][x] && _image[y-1][x+1] ? 1 : 0)
			+ (!_image[y-1][x+1] && _image[y][x+1] ? 1 : 0)
			+ (!_image[y][x +1] && _image[y+1][x+1] ? 1 : 0)
			+ (!_image[y+1][x+1] && _image[y+1][x] ? 1 : 0)
			+ (!_image[y+1][x] && _image[y+1][x-1] ? 1 : 0)
			+ (!_image[y+1][x-1] && _image[y][x-1] ? 1 : 0)
			+ (!_image[y][x-1] && _image[y-1][x-1] ? 1 : 0)
			+ (!_image[y-1][x-1] && _image[y-1][x] ? 1 : 0)
		;
    }
 
    private int getB(final int y, final int x) {
		return (int) Arrays
			.asList(
				_image[y-1][x-1], _image[y-1][x], _image[y-1][x+1],
				_image[y][x - 1], _image[y][x + 1],
				_image[y+1][x-1], _image[y+1][x], _image[y+1][x+1]
			)
			.stream()
			.filter(b -> b)
			.count()
		;
    }

	private void clearShadow() {
		_shadow = new boolean[_height][_width];
	}

	private void merge() {
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				_image[y][x] &= !_shadow[y][x];
			}
		}
	}
}
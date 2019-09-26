package de.unituebingen.medizin.eye.ciloct.matrix;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.AbstractRealMatrix;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;

/**
 * @author strasser
 */
public class ImageDataMatrix extends AbstractRealMatrix implements PixelReader {
	private final int _width;
	private final int _height;
	private final int[] _data;

	public ImageDataMatrix(final BufferedImage image) {
		_width = image.getWidth();
		_height = image.getHeight();
		_data = image.getRaster().getPixels(0, 0, _width, _height, new int[_width * _height]);
	}

	private ImageDataMatrix(final int rowDimension, final int columnDimension) {
		this(rowDimension, columnDimension, new int[columnDimension * rowDimension]);
	}

	private ImageDataMatrix(final int rowDimension, final int columnDimension, final int[] data) {
		_width = columnDimension;
		_height = rowDimension;
		_data = data;
	}

	@Override
	public int getRowDimension() {
		return _height;
	}

	@Override
	public int getColumnDimension() {
		return _width;
	}

	@Override
	public ImageDataMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
		return new ImageDataMatrix(rowDimension, columnDimension);
	}

	@Override
	public ImageDataMatrix copy() {
		final int[] data = new int[_data.length];
        System.arraycopy(_data, 0, data, 0, _data.length);

        return new ImageDataMatrix(_height, _width, data);
	}

	@Override
	public double getEntry(final int row, final int column) throws OutOfRangeException {
		return _data[row * _width + column];
	}

	public double getEntry(final Point point) {
		return getEntry(point.getRoundedY(), point.getRoundedX());
	}

	@Override
	public void setEntry(final int row, final int column, final double value) throws OutOfRangeException {
		setEntry(row, column, (int) Math.round(value));
	}

	public void setEntry(final int row, final int column, final int value) throws OutOfRangeException {
		_data[row * _width + column] = value;
	}

	@Override
	public PixelFormat getPixelFormat() {
		return PixelFormat.getIntArgbInstance();
	}

	@Override
	public int getArgb(final int x, final int y) {
		final int v = (int) getEntry(y, x);
		return (255 << 24 | v << 16 | v << 8 | v);
	}

	@Override
	public Color getColor(final int x, final int y) {
		return Color.gray(getEntry(y, x) / 255d);
	}

	@Override
	public <T extends Buffer> void getPixels(final int x, final int y, final int w, final int h, final WritablePixelFormat<T> pixelformat, final T buffer, final int scanlineStride) {
		walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
			@Override
			public void visit(final int row, final int column, final double value) {
				final int v = (int) value;
				pixelformat.setArgb(buffer, column, row, scanlineStride, (255 << 24 | v << 16 | v << 8 | v));
			}
		}, y, y+h-1, x, x+w-1);
	}

	@Override
	public void getPixels(final int x, final int y, final int w, final int h, WritablePixelFormat<ByteBuffer> pixelformat, final byte[] buffer, final int offset, final int scanlineStride) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
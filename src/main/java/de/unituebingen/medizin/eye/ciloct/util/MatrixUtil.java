package de.unituebingen.medizin.eye.ciloct.util;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * @author strasser
 */
public final class MatrixUtil {
	private MatrixUtil() {
	}

	public static RealMatrix medianFilter(final RealMatrix matrix) {
		final RealMatrix result = matrix.copy();

		result.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
			@Override
			public double visit(final int row, final int column, final double value) {
				return Arrays
						.stream(result.getSubMatrix(row-1, row+1, column-1, column+1).getData())
						.flatMapToDouble(Arrays::stream)
						.sorted()
						.toArray()[4]
				;
			}
		}, 1, result.getRowDimension()-2, 1, result.getColumnDimension()-2);

		return result;
	}

	public static RealMatrix horizontalGradient(final RealMatrix matrix) {
		final RealMatrix result = matrix.copy();

		final LoessInterpolator interpolator = new LoessInterpolator(0.01, 1);

		final double column[] = new double[matrix.getRowDimension()];
		Arrays.setAll(column, i -> i+1);

		IntStream.range(0, matrix.getColumnDimension()).forEach(x -> {
			final UnivariateFunction function = interpolator.interpolate(column, result.getColumn(x)).derivative();
			result.setColumn(x, Arrays.stream(column).map(y -> function.value(y)).toArray());
		});

		return result;
	}

	public static void scaleToRange(final RealMatrix matrix, final double lower, final double upper) {
		final double min = getMin(matrix);
		final double max = getMax(matrix);
		final double srcRange = max - min;
		final double targetRange = upper - lower;

		matrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
			@Override
			public double visit(final int row, final int column, final double value) {
				return targetRange * (value - min) / srcRange + lower;
			}
		});
	}

	public static double getMax(final RealMatrix matrix) {
		return matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
			private double _max = Double.NEGATIVE_INFINITY;

			@Override
			public void visit(final int row, final int column, final double value) {
				if (value > _max) {
					_max = value;
				}
			}

			@Override
			public double end() {
				return _max;
			}
		});
	}

	public static double getMin(final RealMatrix matrix) {
		return matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
			private double _min = Double.POSITIVE_INFINITY;

			@Override
			public void visit(final int row, final int column, final double value) {
				if (value < _min) {
					_min = value;
				}
			}

			@Override
			public double end() {
				return _min;
			}
		});
	}

	public static double[] histogram(final RealMatrix matrix, final boolean normalize) {
		final double[] histogram = new double[256];

		matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor(){

			@Override
			public void visit(final int row, final int column, final double value) {
				histogram[(int) Math.round(value)]++;
			}
		});

		if (normalize) {
			final double max = Arrays.stream(histogram).max().getAsDouble();
			for (int i = 0; i < histogram.length; i++) {
				histogram[i] /= max;
			}
		}

		return histogram;
	}

	public static <T extends RealMatrix>T flipVertical(final T matrix) {
		final T flipped = (T) matrix.createMatrix(matrix.getRowDimension(), matrix.getColumnDimension());

		for (int i = matrix.getColumnDimension()-1, j = 0; i >= 0; i--, j++) {
			flipped.setColumn(i, matrix.getColumn(j));
		}

		return flipped;
	}
}
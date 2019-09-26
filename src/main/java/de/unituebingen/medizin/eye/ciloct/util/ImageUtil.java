package de.unituebingen.medizin.eye.ciloct.util;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.FileImageInputStream;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealMatrix;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;

/**
 * @author strasser
 */
public final class ImageUtil {
	private ImageUtil() {
	}


	public static BufferedImage convertToGray(final BufferedImage image) {
		return new ColorConvertOp(image.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(image, new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY));
	}

	public static BufferedImage loadDicomImage16bitGrayscale(final File file) throws IOException {
		final DicomImageReader reader = (DicomImageReader) ImageIO.getImageReadersByFormatName("dicom").next();
		reader.setInput(new FileImageInputStream(file), true);

		final Raster raster = reader.readRaster(0, new ImageReadParam());
		final short[] ss = ((DataBufferUShort) raster.getDataBuffer()).getData();

		final ByteBuffer bb = ByteBuffer.allocate(ss.length * 2);
		for (int i = 0; i < ss.length; i++) {
			bb.putShort(ss[i]);
		}
		bb.rewind();

		for (int j = 0; j < ss.length; j++) {
			ss[j] = (short) ((((bb.get() & 0xFF) << 8) | (bb.get() & 0xFF)) + 32768);
		}

		final ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
		
		return new BufferedImage(colorModel, (WritableRaster) raster, colorModel.isAlphaPremultiplied(), null);
	}

	public static BufferedImage adjustVisanteDicomImage(final BufferedImage image) {
		final AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(-1, 1));
		at.concatenate(AffineTransform.getTranslateInstance(-image.getHeight()-image.getWidth(), image.getHeight()/4));
		at.concatenate(AffineTransform.getScaleInstance(2.0, 0.5));
		at.concatenate(AffineTransform.getQuadrantRotateInstance(1, image.getWidth()/2, image.getHeight()/2));
		at.concatenate(AffineTransform.getScaleInstance(1, 1.25));

		final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		return ato.filter(image, ato.createCompatibleDestImage(image, image.getColorModel()));
	}

	public static <T extends RealMatrix>T changeContrast(final T matrix, final double contrast) {
		final double factor = (259d * (contrast + 255d)) / (255d * (259d - contrast));

		matrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
			@Override
			public double visit(final int row, final int column, final double value) {
				final double newValue = factor * (value - 128d) + 128d;
				if (newValue < 0) {
					return 0;
				} else if (newValue > 255d) {
					return 255d;
				} else {
					return newValue;
				}
			}
		});

		return matrix;
	}

	/**
	 * CLAHE algorithm
	 * From: https://github.com/accord-net/java/blob/master/Catalano.Image/src/Catalano/Imaging/Filters/Clahe.java
	 * @param <T>
	 * @param matrix 
	 * @param blockRadius 
	 * @param slope 
	 * @param bins 
	 */
	public static <T extends RealMatrix> void clahe(final T matrix, final int blockRadius, final double slope, final int bins) {
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			int[][] result = new int[matrix.getRowDimension()][matrix.getColumnDimension()];

			int iMin = Math.max(0, i - blockRadius);
			int iMax = Math.min(matrix.getRowDimension(), i + blockRadius + 1);
			int h = iMax - iMin;

			int jMin = Math.max(0, -blockRadius);
			int jMax = Math.min(matrix.getColumnDimension() - 1, blockRadius);

			int[] hist = new int[bins + 1];
			int[] clippedHist = new int[bins + 1];

			for (int k = iMin; k < iMax; k++) {
				for (int l = jMin; l < jMax; l++) {
					++hist[roundPositive(matrix.getEntry(k, l) / 255.0d * bins)];
				}
			}

			for (int j = 0; j < matrix.getColumnDimension(); j++) {
				int v = roundPositive(matrix.getEntry(i, j) / 255.0d * bins);

				int xMin = Math.max(0, j - blockRadius);
				int xMax = j + blockRadius + 1;
				int w = Math.min(matrix.getColumnDimension(), xMax) - xMin;
				int n = h * w;

				int limit = (int) (slope * n / bins + 0.5f);

				/* remove left behind values from histogram */
				if (xMin > 0) {
					int xMin1 = xMin - 1;
					for (int yi = iMin; yi < iMax; ++yi) {
						--hist[roundPositive(matrix.getEntry(yi, xMin1) / 255.0f * bins)];
					}
				}

				/* add newly included values to histogram */
				if (xMax <= matrix.getColumnDimension()) {
					int xMax1 = xMax - 1;
					for (int yi = iMin; yi < iMax; ++yi) {
						++hist[roundPositive(matrix.getEntry(yi, xMax1) / 255.0f * bins)];
					}
				}

				System.arraycopy(hist, 0, clippedHist, 0, hist.length);
				int clippedEntries = 0, clippedEntriesBefore;

				do {
					clippedEntriesBefore = clippedEntries;
					clippedEntries = 0;
					for (int z = 0; z <= bins; ++z) {
						int d = clippedHist[z] - limit;
						if (d > 0) {
							clippedEntries += d;
							clippedHist[z] = limit;
						}
					}

					int d = clippedEntries / (bins + 1);
					int m = clippedEntries % (bins + 1);
					for (int z = 0; z <= bins; ++z) {
						clippedHist[z] += d;
					}

					if (m != 0) {
						int s = bins / m;
						for (int z = 0; z <= bins; z += s) {
							++clippedHist[z];
						}
					}
				} while (clippedEntries != clippedEntriesBefore);

				/* build cdf of clipped histogram */
				int hMin = bins;
				for (int z = 0; z < hMin; ++z) {
					if (clippedHist[z] != 0) {
						hMin = z;
					}
				}

				int cdf = 0;
				for (int z = hMin; z <= v; ++z) {
					cdf += clippedHist[z];
				}

				int cdfMax = cdf;
				for (int z = v + 1; z <= bins; ++z) {
					cdfMax += clippedHist[z];
				}

				int cdfMin = clippedHist[hMin];

				result[i][j] = roundPositive((cdf - cdfMin) / (float) (cdfMax - cdfMin) * 255.0f);
			}

			for (int a = 0; a < matrix.getColumnDimension(); a++) {
				matrix.setEntry(i, a, result[i][a]);
			}
		}
	}

    private static int roundPositive(final double a ) {
        return (int) ( a + 0.5f );
    }
}
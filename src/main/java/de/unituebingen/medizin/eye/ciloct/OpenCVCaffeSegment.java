package de.unituebingen.medizin.eye.ciloct;

import de.unituebingen.medizin.eye.ciloct.exception.GradientException;
import de.unituebingen.medizin.eye.ciloct.vo.SegmentationPoint;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

/**
 * @author strasser
 */
public class OpenCVCaffeSegment implements ICaffeSegment {
	static {
		Loader.load(opencv_java.class);
	}

	private static Net _net;

	public OpenCVCaffeSegment(final File prototxt, final File weights) throws GradientException {
		if (_net == null) {
			try {
				final MatOfByte proto = new MatOfByte(IOUtils.toByteArray(new FileInputStream(prototxt)));
				final MatOfByte model = new MatOfByte(IOUtils.toByteArray(new FileInputStream(weights)));
				_net = Dnn.readNetFromCaffe(proto, model);
				setUseGPU(false);
			} catch (IOException e) {
				throw new GradientException(e);
			}
		}
	}

	public final void setUseGPU(final boolean useGPU) {
		if (useGPU) {
			_net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
			_net.setPreferableTarget(Dnn.DNN_TARGET_OPENCL_FP16);			
		} else {
			_net.setPreferableBackend(Dnn.DNN_BACKEND_DEFAULT);
			_net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
		}
	}

	@Override
	public Map<GroundTruthPalette, List<SegmentationPoint>> segment(final BufferedImage image) throws GradientException {
		try {
			final Mat data = new Mat(new Size(image.getWidth(), image.getHeight()), CvType.CV_8UC1);
			data.put(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
			final Mat data2 = Dnn.blobFromImage(data, 1.0, new Size(640, 256), new Scalar(0), false, false);

			_net.setInput(data2);

			final Mat result = _net.forward();

			final List<Mat> matrices = new ArrayList<>();
			Dnn.imagesFromBlob(result, matrices);

			return argmax(matrices.get(0));
		} catch (Throwable t) {
			throw new GradientException(t);
		}
	}

	private Map<GroundTruthPalette, List<SegmentationPoint>> argmax(final Mat mat) {
		final Map<GroundTruthPalette, List<SegmentationPoint>> segmentation =
			Arrays
				.stream(PALETTE)
				.filter(p -> p != GroundTruthPalette.BACKGROUND)
				.collect(Collectors.toMap(Function.identity(), p -> new ArrayList<SegmentationPoint>()))
		;

		for (int x = 0; x < mat.cols(); x++) {
			for (int y = 0; y < mat.rows(); y++) {
				final double[] classProbabilities = mat.get(y, x);

				int clazz = 0;
				for (int cl = 1; cl < classProbabilities.length; cl++) {
					if (classProbabilities[cl] > classProbabilities[clazz]) {
						clazz = cl;
					}
				}

				if (clazz > 0) {
					segmentation.get(PALETTE[clazz-1]).add(new SegmentationPoint(x * 2, y * 2, PALETTE[clazz-1], classProbabilities[clazz]));
				}
			}
		}

		return segmentation;
	}
}
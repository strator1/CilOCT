package de.unituebingen.medizin.eye.ciloct.util;

/**
 * @author strasser
 */
public final class Otsu {
	private Otsu() {
	}

	public static int threshold(final double[] histogram, final int pixelCount) {
		int sum = 0;
		for (int i = 0; i < histogram.length; i++) {
			sum += i * histogram[i];
		}

		float sumB = 0;
		int wB = 0;

		float varMax = 0;
		int threshold = 0;

		for (int i = 0; i < histogram.length; i++) {
			wB += histogram[i]; // Weight Background
			if (wB == 0) {
				continue;
			}

			int wF = pixelCount - wB; // Weight Foreground
			if (wF == 0) {
				break;
			}

			sumB += (float) (i * histogram[i]);

			float mB = sumB / wB; // Mean Background
			float mF = (sum - sumB) / wF; // Mean Foreground

			// Calculate Between Class Variance
			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

			// Check if new maximum found
			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = i;
			}
		}

		return threshold;
	}	
}
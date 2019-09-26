package de.unituebingen.medizin.eye.ciloct.util;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * @author strasser
 */
public final class FloodSelect {
	private FloodSelect() {
	}

	private static final int[] DX = new int[] {0, 1, 0, -1};
	private static final int[] DY = new int[] {-1, 0, 1, 0};

	public static List<Point> select(final RealMatrix matrix, final int startX, final int startY, final BiPredicate<Integer, Integer> predicate) {
		if (!predicate.test(startX, startY)) {
			return Collections.emptyList();
		}

		final boolean[][] m = new boolean[matrix.getColumnDimension()][matrix.getRowDimension()];
		final List<Point> result = new ArrayList<>();

		final Deque<Point> queue = new LinkedList<>();
		for (Point p = new Point(startX, startY); p != null; p = queue.pollLast()) {
			final int x = p.getRoundedX();
			final int y = p.getRoundedY();

			if (x >= matrix.getColumnDimension() || y >= matrix.getRowDimension()) {
				continue;
			}

			m[x][y] = true;
			result.add(p);

			for (int i = 0; i < DX.length; i++) {
				final int nx = x + DX[i];
				final int ny = y + DY[i];

				if (nx > 0 && nx < m.length && ny > 0 && ny < m[0].length && !m[nx][ny] && predicate.test(nx, ny)) {
					queue.offerLast(new Point(nx, ny));
				} 
			}
		}

		return result;
	}	
}
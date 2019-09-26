package de.unituebingen.medizin.eye.ciloct.util;

import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author strasser
 */
public final class Util {
	private Util() {
	}

	public static <S, T> Optional<T> match(S key, final List<S> keys, final T ... values) {
		final int index = keys.indexOf(key);
		return (index != -1) ? Optional.of(values[index]) : Optional.empty();
	}

	public static Set<Double> findDuplicateX(final List<Point> points) {
		final Set<Double> setToReturn = new HashSet<>(); 
		final Set<Double> set1 = new HashSet<>();
		points.forEach(p -> {
			if (!set1.add(p.getX())) {
				setToReturn.add(p.getX());
			}
		});
		return setToReturn;
	}
}
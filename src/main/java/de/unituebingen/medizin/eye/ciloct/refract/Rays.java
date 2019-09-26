package de.unituebingen.medizin.eye.ciloct.refract;

import de.unituebingen.medizin.eye.ciloct.draw.IDrawable;
import de.unituebingen.medizin.eye.ciloct.util.MathUtil;
import de.unituebingen.medizin.eye.ciloct.vo.PointList;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toCollection;
import java.util.stream.IntStream;

/**
 * @author strasser
 */
public class Rays {
	private final List<Ray> _rays = new ArrayList<>(2000);
	private final List<IInterface<?>> _interfaces = new ArrayList<>(5);

	private final double _resolution;

	public Rays(final double resolution) {
		_resolution = resolution;
	}

	public void addInterface(final IInterface<?> iface) {
		_interfaces.add(iface);
	}

	public IInterface<?> getInterface(final int i) {
		return _interfaces.get(i);
	}

	public void createRays(final double start, final double end) {
		for (double x = start; x < end; x+=_resolution) {
			addRay(x);
		}
	}

	public void refract() {
		refract(1);
	}

	public List<IInterface<?>> getInterfaces() {
		return _interfaces;
	}

	public void debug(final List<IDrawable> layer, int start, final int spacing) {
		final int space = (int) Math.round(spacing / _resolution);
		IntStream
			.iterate(0, i -> i + space)
			.limit(_rays.size() / space)
			.mapToObj(i -> _rays.get(i))
			.filter(ray -> start < ray.count()-1)
			.forEach(ray -> ray.debug(layer, start, _interfaces.get(start)))
		;
	}

	private void addRay(final double x) {
		final Ray ray = new Ray();

		_interfaces
			.stream()
			.filter(iface -> iface.isValidPoint(x))
			.sorted((i1, i2) -> Double.compare(i1.value(x), i2.value(x)))
			.forEach(iface -> ray.addPoint(x, iface))
		;

		_rays.add(ray);
	}

	private void refract(final int start) {
		if (start < _interfaces.size()) {
			_rays
//				.parallelStream()
				.forEach(ray -> ray.refract(start))
			;

			_interfaces
				.stream()
				.skip(1)
				.forEach(iface -> {
						final PointList points =
						_rays
							.stream()
//							.parallel()
							.flatMap(ray -> ray.getPoints().stream())
							.filter(p -> iface.equals(p.getInterface()))
							.filter(p -> Double.isFinite(p.getX()) && Double.isFinite(p.getY()))
							.collect(toCollection(PointList::new))
						;
						if (iface instanceof InterfacePointList) {
							((InterfacePointList) iface).updateBorder(points);
						} else {
							if (points.size() > 0) {
								((Interface) iface).updateBorder(MathUtil.fitPolynomialSpline(points));
							}
						}
				});

			refract(start + 1);
		}
	}
}
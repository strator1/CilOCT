package de.unituebingen.medizin.eye.ciloct;

import de.unituebingen.medizin.eye.ciloct.vo.Angle;
import de.unituebingen.medizin.eye.ciloct.vo.Line;
import de.unituebingen.medizin.eye.ciloct.vo.Point;
import java.util.Objects;
import java.util.Optional;

/**
 * @author strasser
 */
public final class Result {
	private final String _filename;
	private final Optional<Line> _perpendicularAxis;
	private final Optional<Angle> _innerAngle;
	private final Optional<Angle> _ciliaryApexAngle;
	private final Optional<Double> _ciliaryMuscleArea;
	private final Optional<Point> _ciliaryApex;
	private final Optional<Point> _scleralSpur;
	private final Optional<Double> _ciliaryApexShift;

	private boolean _useAngles = false;

	Result(
			final String filename,
			final Optional<Line> perpendicularAxis,
			final Optional<Angle> ciliaryApexAngle,
			final Optional<Angle> innerAngle,
			final Optional<Double> ciliaryMuscleArea,
			final Optional<Point> ciliaryApex,
			final Optional<Point> scleralSpur,
			final Optional<Double> ciliaryApexShift) {
		_filename = filename;
		_ciliaryApexAngle = ciliaryApexAngle;
		_perpendicularAxis = perpendicularAxis;
		_innerAngle = innerAngle;
		_ciliaryMuscleArea = ciliaryMuscleArea;
		_ciliaryApex = ciliaryApex;
		_scleralSpur = scleralSpur;
		_ciliaryApexShift = ciliaryApexShift;
	}

	public boolean isUseAngles() {
		return _useAngles;
	}

	public void setUseAngle(final boolean useAngles) {
		_useAngles = useAngles;
	}

	public String getFilename() {
		return _filename;
	}

	public Optional<Line> getPerpendicularAxis() {
		return _perpendicularAxis;
	}

	public Optional<Angle> getInnerAngle() {
		return _innerAngle;
	}

	public Optional<Angle> getCiliaryApexAngle() {
		return _ciliaryApexAngle;
	}

	public Optional<Double> getCiliaryMuscleArea() {
		return _ciliaryMuscleArea;
	}

	public Optional<Point> getCiliaryApex() {
		return _ciliaryApex;
	}

	public Optional<Point> getScleralSpur() {
		return _scleralSpur;
	}

	public Optional<Double> getCiliaryMuscleShift() {
		return _ciliaryApexShift;
	}

	@Override
	public String toString() {
		if (_useAngles) {
			return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
				_filename,
				_perpendicularAxis.map(l -> (String.format("%.1f", l.getLength()))).orElse(""),
				_ciliaryApexAngle.map(angle -> (String.format("%.0f", angle.getAngle()))).orElse(""),
				_innerAngle.map(angle -> (String.format("%.0f", angle.getAngle()))).orElse(""),
				_ciliaryMuscleArea.map(area -> (String.format("%.1f", area))).orElse(""),
				_ciliaryApex.map(p -> (String.format("%.1f", p.getX()))).orElse(""),
				_ciliaryApex.map(p -> (String.format("%.1f", p.getY()))).orElse(""),
				_scleralSpur.map(p -> (String.format("%.1f", p.getX()))).orElse(""),
				_scleralSpur.map(p -> (String.format("%.1f", p.getY()))).orElse(""),
				_ciliaryApex.map(p -> _scleralSpur.map(p1 -> (String.format("%.1f", Math.sqrt(Math.pow(p.getX() - p1.getX(), 2) + Math.pow(p.getY() - p1.getY(), 2))))).orElse("")).orElse(""),
				_ciliaryApexShift.map(area -> (String.format("%.1f", area))).orElse("")
			);
		} else {
			return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
				_filename,
				_perpendicularAxis.map(l -> (String.format("%.1f", l.getLength()))).orElse(""),
				_ciliaryMuscleArea.map(area -> (String.format("%.1f", area))).orElse(""),
				_ciliaryApex.map(p -> (String.format("%.1f", p.getX()))).orElse(""),
				_ciliaryApex.map(p -> (String.format("%.1f", p.getY()))).orElse(""),
				_scleralSpur.map(p -> (String.format("%.1f", p.getX()))).orElse(""),
				_scleralSpur.map(p -> (String.format("%.1f", p.getY()))).orElse(""),
				_ciliaryApex.map(p -> _scleralSpur.map(p1 -> (String.format("%.1f", Math.sqrt(Math.pow(p.getX() - p1.getX(), 2) + Math.pow(p.getY() - p1.getY(), 2))))).orElse("")).orElse(""),
				_ciliaryApexShift.map(area -> (String.format("%.1f", area))).orElse("")
			);			
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Objects.hashCode(_filename);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Result other = (Result) obj;
		return Objects.equals(_filename, other._filename);
	}

	static String header(final boolean useAngles) {
		if (useAngles) {
			return "Filename\tPerpendicular axis\tCiliary muscle angle\tInner angle\tCiliary muscle area\tCiliary apex x0\tCiliary apex y0\tScleral spur x0\tScleral spur y0\tCA-SP-dist\tCiliary apex shift\n";
		} else {
			return "Filename\tPerpendicular axis\tCiliary muscle area\tCiliary apex x0\tCiliary apex y0\tScleral spur x0\tScleral spur y0\tCA-SP-dist\tCiliary apex shift\n";
		}
	}
}
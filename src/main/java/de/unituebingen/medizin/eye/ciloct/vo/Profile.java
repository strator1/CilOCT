package de.unituebingen.medizin.eye.ciloct.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author strasser
 */
public class Profile extends ArrayList<ProfilePoint> {
	private final String _name;
	private final boolean _corrected;

	public Profile(final String name, final boolean corrected, final List<ProfilePoint> points) {
		_name = name;
		_corrected = corrected;
		points
			.stream()
			.peek(p -> {
				p.setName(name);
				p.setCorrected(corrected);
			})
			.forEach(p -> add(p));
	}

	public String getName() {
		return _name;
	}

	public boolean isCorrected() {
		return _corrected;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(_name);
		hash = 79 * hash + (_corrected ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Profile other = (Profile) obj;
		if (!Objects.equals(_name, other._name)) {
			return false;
		}
		return this._corrected == other._corrected;
	}
}
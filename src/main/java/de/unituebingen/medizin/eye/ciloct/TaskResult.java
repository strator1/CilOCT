package de.unituebingen.medizin.eye.ciloct;

import de.unituebingen.medizin.eye.ciloct.vo.Profile;
import java.util.List;
import java.util.Map;

/**
 * @author strasser
 */
class TaskResult {
	private final long _total;
	private final List<Result> _results;
	private final List<Profile> _profiles;
	private final Map<String, List<Throwable>> _errors;

	TaskResult(final long total, final List<Result> results, final List<Profile> profiles, final Map<String, List<Throwable>> errors) {
		_total = total;
		_results = results;
		_profiles = profiles;
		_errors = errors;
	}

	long getTotal() {
		return _total;
	}

	int getFailed() {
		return _errors.size();
	}

	int getSuccessfull() {
		return _results.size();
	}

	List<Result> getResults() {
		return _results;
	}

	List<Profile> getProfiles() {
		return _profiles;
	}

	Map<String, List<Throwable>> getErrors() {
		return _errors;
	}
}
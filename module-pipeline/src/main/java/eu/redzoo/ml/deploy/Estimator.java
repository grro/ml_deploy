package eu.redzoo.ml.deploy;

import java.util.List;
import java.util.Map;

public interface Estimator<I, L> {

	public List<L> predict(List<Map<String, I>> records);
}
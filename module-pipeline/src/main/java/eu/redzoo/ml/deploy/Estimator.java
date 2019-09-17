package eu.redzoo.ml.deploy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Estimator<I, L> extends Serializable {

	Map<String, Object> fit(List<Map<String, I>> records, List<L> labels);

	public List<L> predict(List<Map<String, I>> records);
}
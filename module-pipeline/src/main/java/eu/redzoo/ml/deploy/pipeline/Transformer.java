package eu.redzoo.ml.deploy.pipeline;



import eu.redzoo.ml.deploy.Trainable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;


public interface Transformer<I, O, L> extends Trainable<I, L> {

	List<Map<String, O>> transform(List<Map<String, I>> records);

	default Pair<List<Map<String, O>>, List<L>> transform(List<Map<String, I>> records, List<L> labels) {
		return ImmutablePair.of(transform(records), labels);
	}

	default Map<String, Object> fit(List<Map<String, I>> records, List<L> labels) {
		return Map.of();
	}
}
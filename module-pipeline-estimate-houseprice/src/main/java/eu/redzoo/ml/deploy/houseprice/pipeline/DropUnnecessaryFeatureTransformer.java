package eu.redzoo.ml.deploy.houseprice.pipeline;


import eu.redzoo.ml.deploy.pipeline.Transformer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DropUnnecessaryFeatureTransformer implements Transformer<Double, Double, Double> {

	private final Set<String> featuresToRemove;

	public DropUnnecessaryFeatureTransformer(String... featuresToRemove) {
		this.featuresToRemove = Set.of(featuresToRemove);
	}

    @Override
	public List<Map<String, Double>> transform(List<Map<String, Double>> houses) {
		return houses.stream().map(this::transform).collect(Collectors.toList());
	}

	private Map<String, Double> transform(Map<String, Double> house) {
		return house.entrySet()
				    .stream()
				    .filter(entry -> !featuresToRemove.contains(entry.getKey()))
				    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
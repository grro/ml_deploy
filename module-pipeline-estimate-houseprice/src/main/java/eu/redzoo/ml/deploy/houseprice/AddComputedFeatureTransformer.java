package eu.redzoo.ml.deploy.houseprice;


import com.google.common.collect.Maps;
import eu.redzoo.ml.deploy.Transformer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AddComputedFeatureTransformer implements Transformer<Double, Double, Double> {

	public List<Map<String, Double>> transform(List<Map<String, Double>> houses) {
		return houses.stream().map(this::transform).collect(Collectors.toList());
	}

	private Map<String, Double> transform(Map<String, Double> features) {
		features = Maps.newHashMap(features);
		features.put("YearsSinceRemodAdd", features.get("YrSold") - features.get("YearRemodAdd"));
		return features;
	}
}
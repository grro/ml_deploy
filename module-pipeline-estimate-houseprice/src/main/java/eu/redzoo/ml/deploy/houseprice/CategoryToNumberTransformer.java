package eu.redzoo.ml.deploy.houseprice;


import com.google.common.collect.Maps;
import eu.redzoo.ml.deploy.Transformer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CategoryToNumberTransformer implements Transformer<Object, Double, Double> {

	private final CategoryToNumberResolver categoryToNumber = new CategoryToNumberResolver();

	@Override
	public Map<String, Object> fit(List<Map<String, Object>> houses, List<Double> prices) {
	    houses.forEach(house -> house.entrySet()
                                     .stream()
                                     .filter(feature -> feature.getValue() instanceof String)
                                     .forEach(categoryToNumber::add));
	    return Map.of();
	}

    @Override
	public List<Map<String, Double>> transform(List<Map<String, Object>> houses) {
		return houses.stream().map(this::transform).collect(Collectors.toList());
	}

	private Map<String, Double> transform(Map<String, Object> features) {
		return features.entrySet()
                       .stream()
                       .collect(Collectors.toMap(feature -> feature.getKey(),
                                                 feature -> (feature.getValue() instanceof String)
                                                               ? categoryToNumber.map(feature)
                                                               : (Double) feature.getValue()));
	}

	private static final class CategoryToNumberResolver implements Serializable {
		private final Map<String, Double> categoryToNumber = Maps.newHashMap();

		void add(Map.Entry<String, Object> feature) {
			// ..
		    var label = feature.getKey() + "_" + feature.getValue().toString();
			var numeric = categoryToNumber.get(label);
			if (numeric == null) {
				numeric = categoryToNumber.size() + 1.0;
				categoryToNumber.put(label, numeric);
			}
		}

		Double map(Map.Entry<String, Object> feature) {
            var label = feature.getKey() + "_" + feature.getValue().toString();
            return categoryToNumber.getOrDefault(label, -1.0);
		}
	}
}
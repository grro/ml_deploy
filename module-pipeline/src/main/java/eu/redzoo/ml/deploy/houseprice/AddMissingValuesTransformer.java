package eu.redzoo.ml.deploy.houseprice;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import eu.redzoo.ml.deploy.Transformer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AddMissingValuesTransformer implements Transformer<Object, Object, Double> {

    private final Map<String, Object> replacements = Maps.newHashMap();

    @Override
    public Map<String, Object> fit(List<Map<String, Object>> records, List<Double> prices) {
        // get all feature names
        var featureNames = records.stream().map(Map::keySet).reduce(Sets.newHashSet(), Sets::union);

        // add missing values
        for (String featureName : featureNames) {

            // A standard approach for numeric values is to replace the missing values with mean or median
            if (isNumeric(records, featureName)) {
                // compute mean
                List<Double> values = Lists.newArrayList();
                for (var record : records) {
                    var value = (Double) record.get(featureName);
                    if (value != null) {
                        values.add(value);
                    }
                }
                Collections.sort(values);
                var mean = values.get((int) (values.size() * 0.5));
                replacements.put(featureName, mean);

            // A standard approach for categorical values is to replace the missing entry with the most frequent one
            } else {
                // compute most frequent
                Map<String, Integer> categoryOccurrence = Maps.newHashMap();
                for (var record : records) {
                    var value = (String) record.get(featureName);
                    if (value != null) {
                        var occurrence = categoryOccurrence.getOrDefault(value, 0);
                        categoryOccurrence.put(value, occurrence + 1);
                    }
                }
                var mostFrequentCategory = "";
                var mostOccurency = 0;
                for (var category : categoryOccurrence.keySet()) {
                    if (categoryOccurrence.get(category) > mostOccurency) {
                        mostOccurency = categoryOccurrence.get(category);
                        mostFrequentCategory = category;
                    }
                }
                replacements.put(featureName, mostFrequentCategory);
            }
        }
        return Map.of();
    }

    @Override
    public List<Map<String, Object>> transform(List<Map<String, Object>> houses) {
        return houses.stream().map(this::transform).collect(Collectors.toList());
    }

    private Map<String, Object> transform(Map<String, Object> features) {
        var updatedFeatures = Maps.<String, Object>newHashMap(features);
        for (var pair : features.entrySet()) {
            if ((pair.getValue() == null) && (replacements.containsKey(pair.getKey()))) {
                updatedFeatures.put(pair.getKey(), replacements.get(pair.getKey()));
            }
        }
        return updatedFeatures;
    }

	private boolean isNumeric(List<Map<String, Object>> records, String featureName) {
		for (var record : records) {
			var value = record.get(featureName);
			if (value != null) {
				return value instanceof Number;
			}
		}
		return false;
	}
}
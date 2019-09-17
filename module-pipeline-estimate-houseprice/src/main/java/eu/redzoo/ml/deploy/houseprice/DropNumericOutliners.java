package eu.redzoo.ml.deploy.houseprice;


import com.google.common.collect.Lists;
import eu.redzoo.ml.deploy.Transformer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DropNumericOutliners implements Transformer<Object, Object, Double> {

    private final String featureName;
    private final double deviation;
    private double median;


    DropNumericOutliners(String featureName, double deviation) {
        this.featureName = featureName;
        this.deviation = deviation;
    }

	@Override
	public Map<String, Object> fit(List<Map<String, Object>> houses, List<Double> prices) {
        this.median = median(houses);
        return Map.of();
	}

    private Double median(List<Map<String, Object>> houses) {
        List<Double> values = Lists.newArrayList() ;
        for (var house : houses) {
            for (var feature : house.entrySet()) {
                if (feature.getKey().equals(featureName) && (feature.getValue() != null)) {
                    values.add((Double) feature.getValue());
                }
            }
        }
        Collections.sort(values);
        return values.get(values.size() / 2);
    }

    @Override
    public List<Map<String, Object>> transform(List<Map<String, Object>> records) {
        return records;
    }

    @Override
	public Pair<List<Map<String, Object>>, List<Double>> transform(List<Map<String, Object>> houses, List<Double> labels) {
        List<Map<String, Object>> filteredHouses = Lists.newArrayList();
        List<Double> filteredLabels = Lists.newArrayList();
        for (int i = 0; i < houses.size(); i++) {
            var house = houses.get(i);
            var label = labels.get(i);
            if (!isOutliner(house)) {
                filteredHouses.add(house);
                filteredLabels.add(label);
            }
        }

        return ImmutablePair.of(filteredHouses, filteredLabels);
	}

	private boolean isOutliner(Map<String, Object> house) {
        for (var feature : house.entrySet()) {
            if (feature.getKey().equals(featureName)) {
                var value = (Double) feature.getValue();
                return value > (deviation * median);
            }
        }
        return false;
    }
}
package eu.redzoo.ml.deploy.houseprice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.redzoo.ml.deploy.Estimator;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


public class HousePriceModel implements Estimator<Double, Double> {

	private Classifier targetFunction = new LinearRegression();

	public Map<String, Object> fit(List<Map<String, Double>> houses, List<Double> prices) {
		try {
			// prepare train, test dataset
			var dataset = toWekaDataSet(houses, prices);
			Random rand = new Random(64545323);
			Instances randData = new Instances(dataset);
			randData.randomize(rand);
			Instances trainData = randData.trainCV(10, 0, rand);
			Instances testData = randData.testCV(10, 0);

			// train the model
			targetFunction.buildClassifier(trainData);

			// evaluate the quality
			Evaluation eval = new Evaluation(randData);
			eval.evaluateModel(targetFunction, testData);
			return Map.of("CorrelationCoefficient", eval.correlationCoefficient(),
					      "RelativeAbsoluteError", eval.relativeAbsoluteError());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Double> predict(List<Map<String, Double>> houses) {
		return toWekaDataSet(houses).stream()
		 		  				    .map(this::predict)
				                    .collect(Collectors.toList());
	}

	private Double predict(Instance house) {
		try {
			return targetFunction.classifyInstance(house);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Instances toWekaDataSet(List<Map<String, Double>> houses) {
		return toWekaDataSet(houses, null);
	}

	private Instances toWekaDataSet(List<Map<String, Double>> houses, List<Double> labels) {
		var price = new Attribute("Price");
		var houseFeatures = houses.get(0).keySet()
				                         .stream()
										 .collect(Collectors.toMap(feature -> feature, Attribute::new));
		var attributes = Lists.newArrayList(houseFeatures.values());
		attributes.add(price);

		var dataset = new Instances("dataSet", attributes, houses.size());
		dataset.setClassIndex(dataset.numAttributes() - 1);

		for (int i = 0; i < houses.size(); i++) {
			var house = houses.get(i);
			var instance = new DenseInstance(attributes.size());
			for (String featureName : house.keySet()) {
				var value = (Double) house.get(featureName);
				instance.setValue(houseFeatures.get(featureName), value);
			}
			if (labels != null) {
				instance.setValue(price, labels.get(i));
			}
			dataset.add(instance);
		}

		return dataset;
	}

	@Override
	public String toString() {
		return targetFunction.toString();
	}
}
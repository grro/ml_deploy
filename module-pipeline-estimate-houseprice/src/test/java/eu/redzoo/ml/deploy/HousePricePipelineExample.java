package eu.redzoo.ml.deploy;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.redzoo.ml.deploy.houseprice.pipeline.HousePricePipelineBuilder;
import eu.redzoo.ml.deploy.pipeline.Pipeline;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;


public class HousePricePipelineExample {

	public static void main(String[] args) throws Exception  {
		var recordsInputStream = HousePriceModelExample.class.getClassLoader().getResourceAsStream("houses.json");
		var labelsInputStream = HousePriceModelExample.class.getClassLoader().getResourceAsStream("prices.json");
		List<Map<String, Object>> houses = List.of(new ObjectMapper().readValue(recordsInputStream, Map[].class));
		List<Double> prices = List.of(new ObjectMapper().readValue(labelsInputStream, Double[].class));

		var predictPipeline = new HousePricePipelineBuilder().newPipeline();
		predictPipeline.fit(houses, prices);
		System.out.println(predictPipeline);
		predictPipeline.save(new File("test.ser"));

		predictPipeline = Pipeline.load(new File("test.ser"));
		var predictions = predictPipeline.predict(houses.subList(0, 5));
		for (int i = 0; i < predictions.size(); i++) {
			System.out.println("predicted: " + predictions.get(i));
			System.out.println("real:      " + prices.get(i));
		}
	}
}
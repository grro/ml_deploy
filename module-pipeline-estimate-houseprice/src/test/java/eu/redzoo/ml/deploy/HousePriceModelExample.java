package eu.redzoo.ml.deploy;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.redzoo.ml.deploy.houseprice.AddMissingValuesTransformer;
import eu.redzoo.ml.deploy.houseprice.CategoryToNumberTransformer;
import eu.redzoo.ml.deploy.houseprice.HousePriceModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class HousePriceModelExample {

	public static void main(String[] args) throws IOException {
		var recordsInputStream = HousePriceModelExample.class.getClassLoader().getResourceAsStream("records.json");
		var labelsInputStream = HousePriceModelExample.class.getClassLoader().getResourceAsStream("labels.json");
		List<Map<String, Object>> houses = List.of(new ObjectMapper().readValue(recordsInputStream, Map[].class));
		List<Double> prices = List.of(new ObjectMapper().readValue(labelsInputStream, Double[].class));

		var transformedHouses = new CategoryToNumberTransformer().transform(new AddMissingValuesTransformer().transform(houses));

		// create and train model
		var model = new HousePriceModel();
		model.fit(transformedHouses, prices);
	}
}
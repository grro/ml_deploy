package eu.redzoo.ml.deploy.houseprice;


import eu.redzoo.ml.deploy.Pipeline;
import eu.redzoo.ml.deploy.PipelineBuilder;

import java.io.IOException;

public class HousePricePipelineBuilder extends PipelineBuilder<Object, Double, Double> {

	@Override
	public Pipeline<Object, Double, Double> newPipeline() {
		return Pipeline.add(new DropNumericOutliners("LotArea", 10))
				.add(new AddMissingValuesTransformer())
				.add(new CategoryToNumberTransformer())
				.add(new AddComputedFeatureTransformer())
				.add(new DropUnnecessaryFeatureTransformer("YrSold", "YearRemodAdd"))
				.add(new HousePriceModel());
	}

	public static void main(String[] args) throws IOException {
		new HousePricePipelineBuilder().train(args);
	}
}
package eu.redzoo.ml.deploy.houseprice.pipeline;


import eu.redzoo.ml.deploy.pipeline.Pipeline;
import eu.redzoo.ml.deploy.pipeline.PipelineBuilder;

import java.io.IOException;

public class HousePricePipelineBuilder extends PipelineBuilder<Object, Double> {

	@Override
	public Pipeline<Object, Double> newPipeline() {
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
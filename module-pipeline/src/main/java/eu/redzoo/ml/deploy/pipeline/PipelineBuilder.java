package eu.redzoo.ml.deploy.pipeline;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public abstract class PipelineBuilder<I, O, L> {

	public Pipeline<I, O, L> train(String[] args) throws IOException {
		var recordsFilename = args.length > 0 ? args[0] .trim(): "records.json";
		var labelsFilename = args.length > 1 ? args[1].trim() : "labels.json";
		var trainedFilename = args.length > 2 ? args[2].trim() : "trainedstate.ser";

		List<Map<String, Object>> records = List.of(new ObjectMapper().readValue(new File(recordsFilename), Map[].class));
		List<Double> labels = List.of(new ObjectMapper().readValue(new File(labelsFilename), Double[].class));

		Pipeline pipeline = newPipeline();
		var metrics = pipeline.fit(records, labels);
		pipeline.save(new File(trainedFilename));
		System.out.println(trainedFilename + " trained (" + Joiner.on(",").withKeyValueSeparator("=").join(metrics) + ")");
		return pipeline;
	}

	public abstract Pipeline<I, O, L> newPipeline();
}
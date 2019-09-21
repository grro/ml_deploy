package eu.redzoo.ml.deploy.pipeline;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public abstract class PipelineBuilder<I, L> {

	public void train(String[] args) throws IOException {
		var recordsName = args.length > 0 ? args[0] .trim(): "records.json";
		var labelsName = args.length > 1 ? args[1].trim() : "labels.json";
		var trainedName = args.length > 2 ? args[2].trim() : "trainedstate.ser";

		var records = List.of(new ObjectMapper().readValue(new File(recordsName), Map[].class));
		var labels = List.of(new ObjectMapper().readValue(new File(labelsName), Double[].class));

		Pipeline pipeline = newPipeline();
		var metrics = pipeline.fit(records, labels);
		pipeline.save(new File(trainedName));
		System.out.println(trainedName + " trained (" + Joiner.on(",").withKeyValueSeparator("=").join(metrics) + ")");
	}

	public abstract Pipeline<I, L> newPipeline();
}
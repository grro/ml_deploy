package eu.redzoo.ml.rest;

import com.google.common.collect.Lists;
import eu.redzoo.ml.deploy.Pipeline;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class EstimatorResource {

	private final Pipeline pipeline;

	EstimatorResource(@Value("${pipelineFilename}") String filename) throws IOException  {
		this.pipeline = Pipeline.load(new File(filename));
	}

	@RequestMapping(value = "/prediction", method = RequestMethod.POST)
	public Object predict(@RequestBody HashMap<String, Object> record) {
		List<Object> predictions = batchPredict(Lists.newArrayList(record));
		return predictions.get(0);
	}

	@RequestMapping(value = "/predictions", method = RequestMethod.POST)
	public List<Object> batchPredict(@RequestBody ArrayList<HashMap<String, Object>> records) {
		List<Object> predictions = pipeline.predict(records);
		return predictions;
	}

	@RequestMapping(value = "/model", method = RequestMethod.GET)
	public List<Map<String, Object>> modelMetrics() {
		return pipeline.getTrainMetrics();
	}
}
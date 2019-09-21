package eu.redzoo.ml.rest;

import com.google.common.collect.Lists;
import eu.redzoo.ml.deploy.Estimator;
import eu.redzoo.ml.deploy.pipeline.Pipeline;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SpringBootApplication
@RestController
public class RestfulEstimator {

	private final Estimator estimator;

	RestfulEstimator(@Value("${filename}") String filename) throws IOException  {
		this.estimator = Pipeline.load(new ClassPathResource(filename).getInputStream());
		System.out.println("file " + filename + " laoded");
	}

	@RequestMapping(value = "/prediction", method = RequestMethod.POST)
	public Object predict(@RequestBody HashMap<String, Object> record) {
		return batchPredict(Lists.newArrayList(record)).get(0);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/predictions", method = RequestMethod.POST)
	public List<Object> batchPredict(@RequestBody ArrayList<HashMap<String, Object>> records) {
		return estimator.predict(records);
	}

	public static void main(String[] args) {
		SpringApplication.run(RestfulEstimator.class, args);
	}
}

package eu.redzoo.ml.rest;

import com.google.common.collect.Lists;
import eu.redzoo.ml.deploy.Estimator;
import eu.redzoo.ml.deploy.Pipeline;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SpringBootApplication
@RestController
public class EstimatorResource {

	private final Estimator estimator;

	EstimatorResource() throws IOException  {
		InputStream is = new ClassPathResource("trained.ser").getInputStream();
		this.estimator = Pipeline.load(is);
	}

	@RequestMapping(value = "/prediction", method = RequestMethod.POST)
	public Object predict(@RequestBody HashMap<String, Object> record) {
		return batchPredict(Lists.newArrayList(record)).get(0);
	}

	@RequestMapping(value = "/predictions", method = RequestMethod.POST)
	public List<Object> batchPredict(@RequestBody ArrayList<HashMap<String, Object>> records) {
		return estimator.predict(records);
	}

	public static void main(String[] args) {
		SpringApplication.run(EstimatorResource.class, args);
	}
}

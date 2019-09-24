package eu.redzoo.ml.rest;

import com.google.common.collect.Lists;
import eu.redzoo.ml.deploy.Estimator;
import eu.redzoo.ml.deploy.pipeline.Pipeline;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SpringBootApplication
@RestController
public class RestfulEstimator {

	private final Estimator estimator;

	RestfulEstimator() throws IOException  {
		this.estimator = Pipeline.load(new ClassPathResource("pipeline.ser").getInputStream());
	}

	@RequestMapping(value = "/prediction", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object predict(@RequestBody HashMap<String, Object> record) {
		return batchPredict(Lists.newArrayList(record)).get(0);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/predictions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Object> batchPredict(@RequestBody ArrayList<HashMap<String, Object>> records) {
		return estimator.predict(records);
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String info() {
		return estimator.toString();
	}

	public static void main(String[] args) {
		// e.g. java -jar server-pipeline-estimate-houseprice-1.0.3-1568611516.jar
		SpringApplication.run(RestfulEstimator.class, args);
	}
}

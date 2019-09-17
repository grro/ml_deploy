package eu.redzoo.ml.deploy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Pipeline<I, O, L> implements Estimator<I, L> {

	private final Transformer<I, O, L> transformer;
	private final Estimator<O, L> model;
	private final List<Map<String, Object>> trainMetrics = Lists.newArrayList();

	private Pipeline(Transformer<I, O, L> transformer, Estimator<O, L> model) {
		this.transformer = transformer;
		this.model = model;
	}

	public Map<String, Object> fit(List<Map<String, I>> records, List<L> labels) {
		var immutableRecords = records.stream().map(Collections::unmodifiableMap).collect(Collectors.toList());

		// first fit the transformer
		var metricsTrain = transformer.fit(immutableRecords, labels);

		// than the model
		var transformed_records_and_labels = transformer.transform(immutableRecords, labels);
		var metricsModel = model.fit(transformed_records_and_labels.getLeft(), transformed_records_and_labels.getRight());

		var metrics = Maps.<String, Object>newHashMap();
		metrics.put("trainDate", LocalDate.now().toString());
		metrics.put("numExamples", transformed_records_and_labels.getKey().size());
		metrics.putAll(metricsModel);
		metrics.putAll(metricsTrain);
		return metrics;
	}

	public  List<L> predict(List<Map<String, I>> records) {
		return model.predict(transformer.transform(records));
	}

	public List<Map<String, Object>> getTrainMetrics() {
		return Lists.newArrayList(trainMetrics);
	}

	public void save(File file) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(transformer);
			oos.writeObject(model);
		}
	}

	public static <I, O, L> Pipeline<I, O, L> load(File file) throws IOException {
	    return load(new FileInputStream(file));
	}

	@SuppressWarnings("unchecked")
	public static <I, O, L> Pipeline<I, O, L> load(InputStream is) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            Transformer<I, O, L> transformer = (Transformer<I, O, L> ) ois.readObject();
            Estimator<O, L> model = (Estimator<O, L>) ois.readObject();
            return new Pipeline<>(transformer, model);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }

	public static <I, O, L> Pipeline.Builder<I, O, L> add(Transformer<I, O, L> transformer) {
		return new Pipeline.Builder<I, O, L>(transformer);
	}

	public static class Builder<I, O, L> {

		private final Transformer<I, O, L> transformer;

		Builder(Transformer<I, O, L> transformer) {
			this.transformer = transformer;
		}

		public <T> Pipeline.Builder<I, T, L> add(Transformer<O, T, L> nextTransformer) {
			return new Pipeline.Builder<>(new TransformerChain<>(transformer, nextTransformer));
		}

		public Pipeline<I, O, L> add(Estimator<O, L> model) {
			return new Pipeline<I, O, L>(transformer, model);
		}
	}

	private static class TransformerChain<I, T, O, L> implements Transformer<I, O, L> {
		private final Transformer<I, T, L> trans1;
		private final Transformer<T, O, L> trans2;

		TransformerChain(Transformer<I, T, L> trans1, Transformer<T, O, L> trans2) {
			this.trans1 = trans1;
			this.trans2 = trans2;
		}

		@Override
		public List<Map<String, O>> transform(List<Map<String, I>> records) {
			return trans2.transform(trans1.transform(records));
		}

		@Override
		public Pair<List<Map<String, O>>, List<L>> transform(List<Map<String, I>> records, List<L> labels) {
			var transformed_record_and_labels = trans1.transform(records, labels);
			return trans2.transform(transformed_record_and_labels.getLeft(), transformed_record_and_labels.getRight());
		}

		@Override
		public Map<String, Object> fit(List<Map<String, I>> records, List<L> labels) {
			var metrics1 = trans1.fit(records, labels);
			var metrics2 = trans2.fit(trans1.transform(records), labels);
			var metrics = Maps.newHashMap(metrics1);
			metrics.putAll(metrics2);
			return metrics;
		}
	}
}
package eu.redzoo.ml.deploy.pipeline;

import eu.redzoo.ml.deploy.Estimator;
import eu.redzoo.ml.deploy.Trainable;


public interface TrainableEstimator<I, L> extends Estimator<I, L>, Trainable<I, L> {

}
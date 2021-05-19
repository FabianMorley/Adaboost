package camera.Adaboost;

import camera.Haar.HaarFeature;
import camera.Haar.RunNewHaar;

import java.util.ArrayList;
import java.util.List;

public class Adaboost {
    int n_classifiers;
    List<DecisionStump> classifiers;

    public Adaboost(int n_classifiers){
        this.n_classifiers = n_classifiers;
        this.classifiers = new ArrayList<>();
    }

    /**
     * Training of the Adaboost classifier
     * @param training_data
     * @param features
     */
    public void fit(List<DataPoint> training_data, List<HaarFeature> features){
        double samples_n = training_data.size();
        double features_n = features.size();

        // Initialise weights
        for(DataPoint datapoint : training_data){
            datapoint.weight = 1/samples_n;
        }

        for(int i = 0; i < n_classifiers; i++){
            // Iterate over all features and thresholds
            DecisionStump classifier = new DecisionStump();

            double min_error = Double.MAX_VALUE;
            for (HaarFeature feature : features){

                // Get all potential thresholds with unique values
                List<Integer> thresholds = new ArrayList<>();

                for(DataPoint datapoint : training_data){
                    int feature_integral = RunNewHaar.featureSum(datapoint.image_mat,feature.x,feature.y,feature);
                    if (!thresholds.contains(feature_integral)){
                        thresholds.add(feature_integral);
                    }
                }

                for(int threshold : thresholds){
                    int polarity = 1;
                    int[] predictions = new int[(int) samples_n];
                    for(int j = 0; j < samples_n; j++){
                        if(RunNewHaar.featureSum(training_data.get(j).image_mat,feature.x,feature.y,feature) < threshold){
                            predictions[j] = -1;
                        }else{
                            predictions[j] = 1;
                        }
                    }

                    double error = 0; // Error is the sum of missclassified weights
                    for(int j = 0; j < samples_n; j++){
                        if (predictions[j] != training_data.get(j).label){
                            error += training_data.get(j).weight;
                        }
                    }
                    if(error > 0.5){
                        error = 1-error;
                        polarity = -1;
                    }

                    if(error < min_error){
                        min_error = error;
                        classifier.polarity = polarity;
                        classifier.threshold = threshold;
                        classifier.feature = feature;
                    }
                }

            }

            // Calculate performance
            double epsilon = 0.00000000001; // So as to not divide by 0
            classifier.alpha = 0.5 * Math.log((1-min_error)/(min_error+epsilon));

            int[] predictions = classifier.predict(training_data);
            double weight_sum = 0;
            for (int j = 0; j < samples_n; j++){
                double weight = training_data.get(j).weight * Math.exp(-classifier.alpha * training_data.get(j).label * predictions[j]);
                training_data.get(j).weight = weight;
                weight_sum += weight;
            }
            // Normalise weights
            for (DataPoint datapoint : training_data){
                datapoint.weight /= weight_sum;
            }

            this.classifiers.add(classifier);
        }

    }

    public int predict(DataPoint datapoint){
        int[] classifier_predictions = new int[n_classifiers];
        for (int i = 0; i < n_classifiers; i++) {
            classifier_predictions[i] = classifiers.get(i).predict(datapoint);
        }
        int prediction_sum = 0;
        for(int prediction : classifier_predictions){
            prediction_sum += prediction;
        }
        return Integer.signum(prediction_sum);
    }
}

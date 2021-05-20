package camera.Adaboost;

import camera.Haar.HaarFeature;
import camera.Haar.RunNewHaar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Adaboost {
    public int n_classifiers;
    public List<DecisionStump> classifiers;

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
            System.out.println("Creating weak classifier " + (i+1));
            final long sTime1 = System.currentTimeMillis();

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

            // Save strong classifier to file
            int polarity_s = classifier.polarity;
            int threshold_s = classifier.threshold;
            double alpha_s = classifier.alpha;

            HaarFeature feature = classifier.feature;
            int feature_type = feature.feature_type;
            int x_scalar = feature.x_scalar;
            int y_scalar = feature.y_scalar;
            int x_pos = feature.x;
            int y_pos = feature.y;

            try {
                AdaboostRun.fw.write("polarity:"+polarity_s+",threshold:"+threshold_s+",alpha:"+alpha_s+",feature_type:"+feature_type+",x_scalar:"+x_scalar+",y_scalar:"+y_scalar+",x_pos:"+x_pos+",y_pos:"+y_pos+"\n");
                AdaboostRun.fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final long eTime1 = System.currentTimeMillis();
            System.out.println("Finished creating weak classifier " + (i+1) + " in " + (eTime1 - sTime1)/1000 + "s");
        }

    }

    public int predict(DataPoint datapoint){
        int[] classifier_predictions = new int[n_classifiers];
        for (int i = 0; i < n_classifiers; i++) {
            classifier_predictions[i] = classifiers.get(i).predict(datapoint);
        }
        int prediction_sum = 0;
        for (int j = 0; j < n_classifiers; j++){
            prediction_sum += classifier_predictions[j];
        }
        return Integer.signum(prediction_sum);
    }
}

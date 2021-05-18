package camera.Adaboost;

import camera.Haar.FixImage;
import camera.Haar.HaarFeature;
import camera.Haar.RunNewHaar;
import camera.Processing.IntegralImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        // STEPS
        // 1. Organise all data into a dataset
        System.out.println("Creating dataset");
        File neg_dir = new File("src/camera/Adaboost/data/neg");
        File pos_dir = new File("src/camera/Adaboost/data/pos");

        File[] neg_directoryListing = neg_dir.listFiles();
        File[] pos_directoryListing = pos_dir.listFiles();

        List<DataPoint> dataset = new ArrayList<>();

        if (neg_directoryListing != null) {
            for (File child : neg_directoryListing) {
                // Do something with child
                dataset.add(new DataPoint(child.getPath(), -1));
            }
        }
        if (pos_directoryListing != null){
            for (File child : pos_directoryListing){
                dataset.add(new DataPoint(child.getPath(), 1));
            }
        }

        System.out.println("Cleaning dataset");
        int imageSize = 24;
        FixImage fixImage = new FixImage();
        for (DataPoint point : dataset){
            fixImage.makeGrayScale(point.imagePath);
            int[][] img_mat = IntegralImage.asMatrix(fixImage.resizeImage(imageSize,imageSize));
            point.image_mat = IntegralImage.integralImage(img_mat); // This is the integral image as a matrix of current image
        }

        // 2. Split into training and test data

        System.out.println("Split dataset");
        Collections.shuffle(dataset); // Shuffle dataset randomly
        double data_split = 0.1; // Split into training and test (0.1 = 10% training, 90% test)
        int split_index = (int) (dataset.size()*data_split);

        List<DataPoint> training = new ArrayList<>();
        List<DataPoint> test = new ArrayList<>();

        for(int i = 0; i < dataset.size(); i++){
            if(i >= split_index){
                test.add(dataset.get(i));
            }else{
                training.add(dataset.get(i));
            }
        }

        // 3. Get all of the possible Haar features
        System.out.println("Create Haar features");
        RunNewHaar haar_runner = new RunNewHaar();
        Hashtable<HaarFeature, int[]> haar_features = haar_runner.runner();

        // ADABOOST
        // 1. Assign equal weights to each observation (1/n)

        for (DataPoint datapoint : training) {
            // Initialising weights for boosting round t = 1
            datapoint.weight = 1.0/training.size();
        }


        // 2. For t = 1 to T do: (where T is number of rounds aka. how many weak classifiers to cascade)
        int boosting_rounds = 1;
        for (int t = 0; t < boosting_rounds; t++) {
            // ITERATE THROUGH EVERY FEATURE AND APPLY BELOW
            for (Map.Entry<HaarFeature, int[]> entry : haar_features.entrySet()){
                for(int x = 0; x < entry.getValue()[0]; x++){
                    for(int y = 0; y < entry.getValue()[1]; y++){
                        final long startTime = System.currentTimeMillis();

                        // Get all potential thresholds from unique values
                        List<Integer> thresholds = new ArrayList<>();
                        for(DataPoint datapoint : training){
                            int feature_integral = haar_runner.featureSum(datapoint.image_mat, x, y, entry.getKey());
                            if (!thresholds.contains(feature_integral)){
                                thresholds.add(feature_integral);
                            }
                        }

                        //TODO implement optimisation of threshold to minimise objective function (error function)
                        //for(int threshold : thresholds)
                        Classifier classifier = new Classifier(entry.getKey(), 0);

                        List<Classifier> classifiers = new ArrayList<>();
                        int true_positive = 0;
                        int true_negative = 0;
                        int false_positive = 0;
                        int false_negative = 0;

                        for (DataPoint datapoint : training) {
                            classifier.feature_integral = haar_runner.featureSum(datapoint.image_mat, x, y, entry.getKey());
                            classifier.classify(datapoint);

                            classifiers.add(classifier);
                        }

                        //      Compute error of weak classifier: Sum of (Datapoint weight * loss function) where loss function = 0 if correct classification and 1 if not
                        double error = 0;
                        double current_total_weight = 0;
                        for (DataPoint datapoint : training) {
                            int loss = 1;
                            if (classifier.classifications.get(datapoint) == datapoint.label) {
                                loss = 0;
                                if(datapoint.label == 1){
                                    true_positive +=1;
                                }else{
                                    true_negative +=1;
                                }
                            }else{
                                if(datapoint.label == 1){
                                    false_negative +=1;
                                }else{
                                    false_positive +=1;
                                }
                            }
                            current_total_weight += datapoint.weight;
                            error += datapoint.weight * loss;
                        }
                        error /= current_total_weight;
//                        System.out.println("Error: " + error);

                        // Classification metrics
//                        double recall = (double) true_positive / (true_positive+false_negative);
//                        double precision = (double) true_positive / (true_positive+false_positive);
//                        double accuracy = (double) (true_positive + true_negative)/(true_positive+true_negative+false_positive+false_negative);
//                        double f1score = (double) (2*true_positive)/(2*true_positive+false_positive+false_negative);
//
//                        System.out.println("Recall: " + recall + "\tPrecision: " + precision + "\tAccuracy: " + accuracy + "\tF1 Score: " + f1score);

                        //      choose coefficient alpha_t of classifier denoted by alpha_t = (0.5 * ln( (1 - error_t)/error_t )
                        double alpha = 0.5 * Math.log((1 - error) / error);
                        //System.out.println("Alpha: " + alpha);

                        //      update weights from step 1 for each datapoint:
                        //          weight for next round = weight * exp(-alpha_t * label of that datapoint * classification of h_t for that datapoint)
                        double next_total_weight = 0;
                        for (DataPoint dataPoint : training) {
                            double d_weight = dataPoint.weight * Math.exp(-alpha * dataPoint.label * classifier.classifications.get(dataPoint));
                            next_total_weight += d_weight;
                            dataPoint.weight = d_weight;
                        }

                        //      normalise weights: (weight for next round / sum of all weights for next round)
                        for (DataPoint dataPoint : training) {
                            dataPoint.weight /= next_total_weight;
                        }

                        final long endTime = System.currentTimeMillis();
                        System.out.println("Execution time for feature: " + (endTime - startTime) + "ms");
                    }
                }
            }
        }
        // 3. Output the final classifier H(x)


    }
}

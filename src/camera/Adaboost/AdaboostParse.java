package camera.Adaboost;

import camera.Haar.FixImage;
import camera.Haar.HaarFeature;
import camera.Processing.IntegralImage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdaboostParse {
    public static void main(String[] args) {
        // Steps to get strong classifier working

        // Parse all weak classifiers into list from text file
        List<DecisionStump> weak_clfs = parse("strong_classifier200_1.txt");

        // Download image from camera screenshot and save its path

        // Create a datapoint out of image(s) to test
        DataPoint test1 = new DataPoint("obama.jpg");

        // Clean image
        int imageSize = 24;
        FixImage fixImage = new FixImage();
        fixImage.makeGrayScale(test1.imagePath);
        int[][] img_mat = IntegralImage.asMatrix(fixImage.resizeImage(imageSize, imageSize));
        test1.image_mat = IntegralImage.integralImage(img_mat);

        // Make prediction
        int prediction = predict(test1, weak_clfs);

        // Output result of prediction
        if(prediction == 1){
            System.out.println("Face detected");
        }else{
            System.out.println("No face detected");
        }

    }

    public static List<DecisionStump> parse(String filepath){
        BufferedReader br;
        List<String> lines = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line = br.readLine();
            while(line != null){
                lines.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<DecisionStump> weak_clfs = new ArrayList<>();
        for(String line : lines){
            String[] line_split = line.split(",");

            int polarity = Integer.parseInt(line_split[0].substring(9));
            int threshold = Integer.parseInt(line_split[1].substring(10));
            double alpha = Double.parseDouble(line_split[2].substring(6));

            int feature_type = Integer.parseInt(line_split[3].substring(13));
            int x_scalar = Integer.parseInt(line_split[4].substring(9));
            int y_scalar = Integer.parseInt(line_split[5].substring(9));
            int x_pos = Integer.parseInt(line_split[6].substring(6));
            int y_pos = Integer.parseInt(line_split[7].substring(6));

            HaarFeature feature = new HaarFeature(feature_type,x_scalar,y_scalar,x_pos,y_pos);
            DecisionStump weak_clf = new DecisionStump(polarity, feature, threshold, alpha);
            weak_clfs.add(weak_clf);
        }
        return weak_clfs;
    }

    public static int predict(DataPoint datapoint, List<DecisionStump> classifiers){
        int n_classifiers = classifiers.size();
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

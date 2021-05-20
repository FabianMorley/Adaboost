package camera.Adaboost;

import camera.Haar.FixImage;
import camera.Haar.HaarFeature;
import camera.Haar.RunNewHaar;
import camera.Processing.IntegralImage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdaboostRun {
    public static FileWriter fw;

    public static void main(String[] args) throws IOException {
        // Initialise strong classifier storage
        fw = new FileWriter("strong_classifier.txt");

        // Load dataset
        List<DataPoint> dataset = load("src/camera/Adaboost/data/neg","src/camera/Adaboost/data/pos");

        // Clean dataset
        clean(dataset, 24);

        // Split into training and test data
        System.out.println("Splitting dataset");
        Collections.shuffle(dataset); // Shuffle dataset randomly

        double data_split = 0.2;
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
        System.out.println("Creating Haar features");
        RunNewHaar haar_runner = new RunNewHaar();
        List<HaarFeature> haar_features = haar_runner.runner();

        // ADABOOST
        System.out.println("Adaboost training");
        final long sTime1 = System.currentTimeMillis();

        Adaboost classifier = new Adaboost(1000);
        classifier.fit(training, haar_features);

        final long eTime1 = System.currentTimeMillis();
        System.out.println("Adaboost trained - " + (eTime1 - sTime1) + "ms");

        System.out.println("Adaboost testing");
        final long sTime2 = System.currentTimeMillis();

        int[] clf_predictions = new int[test.size()];
        int[] test_labels = new int[test.size()];

        for(int i = 0; i < test.size(); i++){
           clf_predictions[i] = classifier.predict(test.get(i));
           test_labels[i] = test.get(i).label;
        }

        final long eTime2 = System.currentTimeMillis();
        System.out.println("Adaboost tested - " + (eTime2 - sTime2) + "ms");

        // Generate metrics for strong classifier H(x)
        metrics(clf_predictions,test_labels);

//        // Save strong classifier
//
//        for(DecisionStump weak_clf : classifier.classifiers){
//            int polarity = weak_clf.polarity;
//            int threshold = weak_clf.threshold;
//            double alpha = weak_clf.alpha;
//
//            HaarFeature feature = weak_clf.feature;
//            int feature_type = feature.feature_type;
//            int x_scalar = feature.x_scalar;
//            int y_scalar = feature.y_scalar;
//            int x_pos = feature.x;
//            int y_pos = feature.y;
//
//            fw.write("polarity:"+polarity+",threshold:"+threshold+",alpha:"+alpha+",feature_type:"+feature_type+",x_scalar:"+x_scalar+",y_scalar:"+y_scalar+",x_pos:"+x_pos+",y_pos:"+y_pos+"\n");
//        }
        fw.close();

        //TODO: txt file to DecisionStump chain
        // & single prediction
    }

    public static List<DataPoint> load(String neg_path, String pos_path){
        System.out.println("Creating dataset");
        final long sTime1 = System.currentTimeMillis();

        File neg_dir = new File(neg_path);
        File pos_dir = new File(pos_path);

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

        final long eTime1 = System.currentTimeMillis();
        System.out.println("Created dataset - " + (eTime1 - sTime1) + "ms");
        return dataset;
    }

    public static void clean(List<DataPoint> dataset, int imageSize){
        System.out.println("Cleaning dataset");
        final long sTime2 = System.currentTimeMillis();

        FixImage fixImage = new FixImage();
        for (DataPoint point : dataset){
            fixImage.makeSquare(point.imagePath);
            fixImage.makeGrayScale();
            int[][] img_mat = IntegralImage.asMatrix(fixImage.resizeImage(imageSize,imageSize));
            point.image_mat = IntegralImage.integralImage(img_mat); // This is the integral image as a matrix of current image
        }

        final long eTime2 = System.currentTimeMillis();
        System.out.println("Cleaned dataset - " + (eTime2 - sTime2)/1000 + "s");
    }

    public static void metrics(int[] predicted, int[] labels){
        double tp = 0;
        double tn = 0;
        double fp = 0;
        double fn = 0;

        for(int i = 0; i < labels.length; i++){
            if(labels[i] == predicted[i]){
                if(labels[i] == 1){
                    tp++;
                }else{
                    tn++;
                }
            }else{
                if(labels[i] == 1){
                    fn++;
                }else{
                    fp++;
                }
            }
        }

        double recall = tp / (tp+fn);
        double precision = tp / (tp+fp);
        double accuracy = (tp + tn)/(tp+tn+fp+fn);
        double f1score = (2*tp)/(2*tp+fp+fn);

        System.out.println("Recall: " + recall);
        System.out.println("Precision: " + precision);
        System.out.println("Accuracy: " + accuracy);
        System.out.println("F1 Score: " + f1score);
    }

}

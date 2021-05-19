package camera.Adaboost.old;

import camera.Adaboost.DataPoint;
import camera.Haar.HaarFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Classifier {
    public HaarFeature feature;
    public double threshold;
    public int feature_integral;
    public Map<DataPoint, Integer> classifications = new HashMap<DataPoint, Integer>();

    public Classifier(HaarFeature feature, double threshold){
        this.feature = feature;
        this.threshold = threshold;
    }

    public void classify(DataPoint dataPoint){
        if (feature_integral > threshold){
            classifications.put(dataPoint, 1);
        }else{
            classifications.put(dataPoint, -1);
        }
//            if(feature.integralSum>threshold){
//                datapoint.setClassifiedAs(1);
//            }
//            else{
//                datapoint.setClassifiedAs(-1);
//            }
    }
}

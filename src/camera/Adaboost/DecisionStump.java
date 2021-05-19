package camera.Adaboost;

import camera.Haar.HaarFeature;
import camera.Haar.RunNewHaar;

import java.util.List;

public class DecisionStump {
    public int polarity;
    public HaarFeature feature;
    public int threshold;
    public double alpha;

    public DecisionStump(){
        this.polarity = 1;
    }

    public int[] predict(List<DataPoint> datapoints){
        int x = feature.x;
        int y = feature.y;

        int samples = datapoints.size();

        int[] predictions = new int[samples];

        for(int i = 0; i < samples; i++){
            if (polarity == 1){
                if (RunNewHaar.featureSum(datapoints.get(i).image_mat,x,y,feature) < threshold){ //TODO Make sure we calculate integral before predicting
                    predictions[i] = -1;
                }else{
                    predictions[i] = 1;
                }
            }else{
                if (RunNewHaar.featureSum(datapoints.get(i).image_mat,x,y,feature) > threshold){
                    predictions[i] = -1;
                }else{
                    predictions[i] = 1;
                }
            }
        }
        return predictions;
    }

    public int predict(DataPoint datapoint){
        int x = feature.x;
        int y = feature.y;

        int samples = 1;

        int prediction = 0;

        if (polarity == 1){
            if (RunNewHaar.featureSum(datapoint.image_mat,x,y,feature) < threshold){ //TODO Make sure we calculate integral before predicting
                prediction = -1;
            }else{
                prediction = 1;
            }
        }else{
            if (RunNewHaar.featureSum(datapoint.image_mat,x,y,feature) > threshold){
                prediction = -1;
            }else{
                prediction = 1;
            }
        }
        return prediction;
    }
}

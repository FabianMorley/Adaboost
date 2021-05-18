package camera.Haar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HaarFeature {
    public int[][] matrix;
    public int scalar;
    public int feature_type; // 1,2,3,4,5
    public int[] coords; // Maximum coordinates

    public int x;
    public int y;
    public double threshold;
    public double threshold_error;

    public double tp;
    public double tn;
    public double fp;
    public double fn;

    public HaarFeature(int[][] matrix, int feature_type, int[] coords, int scalar){
        this.matrix = matrix;
        this.feature_type = feature_type;
        this.coords = coords;
        this.scalar = scalar;
    }

    public void toFile(FileWriter fw) throws IOException {
        fw.write("{type:" + feature_type + ",scalar:" + scalar + ",x:" + x + ",y:" + y + ",threshold:" + threshold + ",error:" + threshold_error + "," + metrics() + "}\n");
    }

    public String metrics(){
        // Classification metrics
        double recall = tp / (tp+fn);
        double precision = tp / (tp+fp);
        double accuracy = (tp + tn)/(tp+tn+fp+fn);
        double f1score = (2*tp)/(2*tp+fp+fn);

        return "recall:"+recall+",precision:"+precision+",accuracy:"+accuracy+",f1score:"+f1score;
    }
}

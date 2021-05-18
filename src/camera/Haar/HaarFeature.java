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

    public HaarFeature(int[][] matrix, int feature_type, int[] coords, int scalar){
        this.matrix = matrix;
        this.feature_type = feature_type;
        this.coords = coords;
        this.scalar = scalar;
    }

    public void toFile(FileWriter fw) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{type:").append(feature_type).append(",scalar:").append(scalar).append(",x:").append(x).append(",y:").append(y).append(",threshold:").append(threshold).append(",error:").append(threshold_error).append("\n");
        fw.write(sb.toString());
    }
}

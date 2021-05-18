package camera.Haar;

public class HaarFeature {
    public int[][] matrix;
    public int feature_type; // 1,2,3,4,5
    public int[] coords;

    public HaarFeature(int[][] matrix, int feature_type, int[] coords){
        this.matrix = matrix;
        this.feature_type = feature_type;
        this.coords = coords;
    }
}

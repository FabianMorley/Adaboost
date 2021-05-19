package camera.Haar;

public class HaarFeature {
    public int[][] matrix;
    public int x_scalar;
    public int y_scalar;
    public int feature_type; // 1,2,3,4,5
    public int[] coords; // Maximum coordinates

    public int x;
    public int y;

    public HaarFeature(int[][] matrix, int feature_type, int[] coords, int x_scalar, int y_scalar){
        this.matrix = matrix;
        this.feature_type = feature_type;
        this.coords = coords;
        this.x_scalar = x_scalar;
        this.y_scalar = y_scalar;
        //TEMPORARY FOR TESTING
        this.x = coords[0];
        this.y = coords[1];
    }
}

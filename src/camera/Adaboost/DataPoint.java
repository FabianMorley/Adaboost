package camera.Adaboost;

import java.awt.image.BufferedImage;

public class DataPoint {
    public String imagePath;
    public int label;
    public double weight;
    public int[][] image_mat;

    DataPoint(String imagePath, int label){
        this.imagePath = imagePath;
        this.label = label;
    }

    /**
     * Unknown label
     * @param imagePath
     */
    DataPoint(String imagePath){
        this.imagePath = imagePath;
    }
}

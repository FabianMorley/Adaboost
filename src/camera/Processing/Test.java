package camera.Processing;

import java.util.Arrays;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Test {
    public static void main(String[] args) throws IOException {
//        int[][] testImage = {{1,2,2,4,1},{3,4,1,5,2},{2,3,3,2,4},{4,1,5,4,6},{6,3,2,1,3}};
//        int[][] integralImage = IntegralImage.integralImage(testImage);

    }

    static void imageToMatrix(File image){

    }

    static void matrixToImage(int[][] matrix) throws IOException {
        BufferedImage image = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_RGB);;
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[0].length; j++) {
                int a = matrix[i][j];
                Color newColor = new Color(a,a,a);
                image.setRGB(j,i,newColor.getRGB());
            }
        }
        File output = new File("GrayScale.jpg");
        ImageIO.write(image, "jpg", output);
    }
}

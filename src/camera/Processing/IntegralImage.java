package camera.Processing;

import java.awt.image.BufferedImage;

public class IntegralImage {

    /**
     * Creates a Summed-area table also know as an Integral Image from an input matrix of integer values representing pixel
     * colour value in grayscale.
     * @param imageMatrix original image to generate Integral Image from
     * @return integral image matrix
     */
    public static int[][] integralImage(int[][] imageMatrix){
        int xdim = imageMatrix.length;
        int ydim = imageMatrix[0].length;

        int[][] integralImageMatrix = new int[xdim][ydim];

        // Initialise values for I(0,y) & I(x,0)
        integralImageMatrix[0][0] = imageMatrix[0][0];

        for(int x = 1; x < xdim; x++){
            integralImageMatrix[x][0] = integralImageMatrix[x-1][0] + imageMatrix[x][0];
        }
        for(int y = 1; y < ydim; y++){
            integralImageMatrix[0][y] = integralImageMatrix[0][y-1] + imageMatrix[0][y];
        }

        for(int i = 1; i < xdim; i++){
            for(int j = 1; j < ydim; j++){
                // I(x,y) = i(x,y) + I(x,y-1) + I(x-1,y) - I(x-1,y-1)
                integralImageMatrix[i][j] = imageMatrix[i][j] + integralImageMatrix[i][j-1] + integralImageMatrix[i-1][j] - integralImageMatrix[i-1][j-1];
            }
        }

        return integralImageMatrix;
    }

    public static BufferedImage asBufferedImage(int[][] matrix, int width, int height){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int pixel = matrix[i][j];
                image.setRGB(i, j, pixel);
            }
        }
        return image;
    }

    public static int[][] asMatrix(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] pixels = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = image.getRGB(i, j) & 0xFF;
            }
        }

        return pixels;
    }
}

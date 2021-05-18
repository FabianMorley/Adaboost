package camera.Haar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

import camera.Processing.IntegralImage;

public class RunNewHaar {

    public BufferedImage image = null;
    public String originalPath;
    public String fixedPath= "src/camera/images/resized.jpg";
    public Object[] haarFeatures;
    public boolean correctImageFormat = false;
    public int imageSize = 24;
    public double featureThreshold = 0.80; // Could be used for the recognizing of sums
    /**
     * Initializes the Haar method.
     */
    public RunNewHaar(){

    }

    /**
     * This is the main runner which can call image resize method
     */
    public Hashtable<HaarFeature, int[]> runner() {

//        // If statement to reformat the image
//        if (!correctImageFormat) {
//            FixImage run1 = new FixImage(originalPath);
//            run1.makeGrayScale(); // This is to transform an image from rgb to greyscale
//            run1.resizeImage(imageSize, imageSize);
//        }

        // Serves as an iterator to see how many times we can increase size of features
        int iterations = (int) imageSize/4; // divide by 4 ensures no feature will outsize the image
        System.out.println("Iterations for size of Haar features: " + (iterations));
        //iterations = 1; // Just used to avoid computational overload

        Hashtable<HaarFeature, int[]> my_dict = new Hashtable<HaarFeature, int[]>();

        // Per iteration the size of the features increase
        for(int round = 1; round<iterations; round++) { // Represents the increase in size for each feature

            MakeFeatures run2 = new MakeFeatures(round);
            haarFeatures = run2.createHaarPixel(round); // Now we have the four de Haar types

            int[][] type1 = (int[][]) haarFeatures[0]; // Black, white horizontal
            int[][] type2 = rotateFeature(type1,1); // Black, white vertical
            int[][] type3 = (int[][]) haarFeatures[1]; // White black white horizontal
            int[][] type4 = rotateFeature(type3, 1); // White black white vertical
            int[][] type5 = (int[][]) haarFeatures[2]; // Black and white diagonals


            // Can outcomment these
//            printArray(type1);
//            printArray(type2);
//            printArray(type3);
//            printArray(type4);
//            printArray(type5);

            // This coordinate system represents from 0 up until that height and length, so for example
            // type 1 would go from coordinate = (0,0) to coordinate b = (coor1[0], coor1[1]) and everything in between
            //System.out.println(image.getWidth() + " " + image.getHeight());
            int[] coor1 = {imageSize-type1.length+1, imageSize-type1[0].length+1};
            int[] coor2 = {imageSize-type2.length+1, imageSize-type2[0].length+1};
            int[] coor3 = {imageSize-type3.length+1, imageSize-type3[0].length+1};
            int[] coor4 = {imageSize-type4.length+1, imageSize-type4[0].length+1};
            int[] coor5 = {imageSize-type5.length+1, imageSize-type5[0].length+1};

            HaarFeature f1 = new HaarFeature(type1, 1, coor1, round);
            HaarFeature f2 = new HaarFeature(type2, 2, coor2, round);
            HaarFeature f3 = new HaarFeature(type3, 3, coor3, round);
            HaarFeature f4 = new HaarFeature(type4, 4, coor4, round);
            HaarFeature f5 = new HaarFeature(type5, 5, coor5, round);

            my_dict.put(f1, coor1);
            my_dict.put(f2, coor2);
            my_dict.put(f3, coor3);
            my_dict.put(f4, coor4);
            my_dict.put(f5, coor5);

            //  Must be a way to use the 2d array of sums, possible save/recognize the features and locations based on threshold
            // Continue on Viola Jones algorithm
            // Option could be to have a HashMap where the key is the feature (and its size) and it maps to the coordinate location..
        }

        System.out.println("Size of dictionary: " + my_dict.size());
        return my_dict;
    }

    /**
     * Calculates the integral sum for the current image for a given feature
     */
    public int featureSum(int[][] int_img_mat, int x, int y, HaarFeature feature){
        //readIm(); // This reads the resized image so we can use its pixels later

        // INTEGRAL CALCULATION
        // To find the sum of an area: C + A - B - D, where A-D are the corners of a rectangle starting top left in
        // a clockwise direction

        int height = feature.matrix.length;
        int width = feature.matrix[0].length;
        //System.out.println("Haar Feature: " + width + " x " + height);

        try {
            switch (feature.feature_type) {
                case 1:
                    // 1 = [1,0]
                    // FOR SCALE 1 CASE
                    if(width == 2 && height == 1){
                        return int_img_mat[x][y] - int_img_mat[x][y+1];
                    }
                    int bA = int_img_mat[x][y];
                    int bB = int_img_mat[x][y + (width / 2) - 1];
                    int bC = int_img_mat[x + height - 1][y + (width / 2) - 1];
                    int bD = int_img_mat[x + height - 1][y];
                    int black = bC + bA - bB - bD; // Do I divide this by number of pixels in this area..?

                    int wA = int_img_mat[x][y + (width / 2)];
                    int wB = int_img_mat[x][y + width - 1];
                    int wC = int_img_mat[x + height - 1][y + width - 1];
                    int wD = int_img_mat[x + height - 1][y];
                    int white = wC + wA - wB - wD;

                    return (black - white) / (width * height);
                case 2:
                    // 2 = [1]
                    //     [0]
                    // FOR SCALE 1 CASE
                    if(width == 1 && height == 2){
                        return int_img_mat[x][y]-int_img_mat[x+1][y];
                    }
                    int b2A = int_img_mat[x][y];
                    int b2B = int_img_mat[x][y + width - 1];
                    int b2C = int_img_mat[x + (height / 2) - 1][y + width - 1];
                    int b2D = int_img_mat[x + (height / 2) - 1][y];
                    int black2 = b2C + b2A - b2B - b2D;

                    int w2A = int_img_mat[x + (height / 2)][y];
                    int w2B = int_img_mat[x + (height / 2)][y + width - 1];
                    int w2C = int_img_mat[x + height - 1][y + width - 1];
                    int w2D = int_img_mat[x + height - 1][y];
                    int white2 = w2C + w2A - w2B - w2D;

                    return (black2 - white2) / (width * height);
                case 3:
                    // 3 = [1,0,1]
                    // FOR SCALE 1 CASE
                    if(width == 3 && height == 1){
                        return (int_img_mat[x][y]+int_img_mat[x][y+2])-int_img_mat[x][y+1];
                    }
                    int b3Al = int_img_mat[x][y];
                    int b3Bl = int_img_mat[x][y + (width / 3) - 1];
                    int b3Cl = int_img_mat[x + height - 1][y + (width / 3) - 1];
                    int b3Dl = int_img_mat[x + height - 1][y];
                    int black3l = b3Cl + b3Al - b3Bl - b3Dl;

                    int w3A = int_img_mat[x][y + (width / 3)];
                    int w3B = int_img_mat[x][y + 2 * (width / 3) - 1];
                    int w3C = int_img_mat[x + height - 1][y + 2 * (width / 3) - 1];
                    int w3D = int_img_mat[x + height - 1][y + (width / 3)];
                    int white3 = w3C + w3A - w3B - w3D;

                    int b3Ar = int_img_mat[x][y + 2 * (width / 3)];
                    int b3Br = int_img_mat[x][y + width - 1];
                    int b3Cr = int_img_mat[x + height - 1][y + width - 1];
                    int b3Dr = int_img_mat[x + height - 1][y + 2 * (width / 3)];
                    int black3r = b3Cr + b3Ar - b3Br - b3Dr;

                    return ((black3l + black3r) - white3) / (width * height);
                case 4:
                    // 4 = [1]
                    //     [0]
                    //     [1]
                    // FOR SCALE 1 CASE
                    if(width == 1 && height == 3){
                        return (int_img_mat[x][y]+int_img_mat[x+2][y])-int_img_mat[x+1][y];
                    }
                    int b4At = int_img_mat[x][y];
                    int b4Bt = int_img_mat[x][y + width - 1];
                    int b4Ct = int_img_mat[x + (height / 3) - 1][y + width - 1];
                    int b4Dt = int_img_mat[x + (height / 3) - 1][y];
                    int black4t = b4Ct + b4At - b4Bt - b4Dt;

                    int w4A = int_img_mat[x + (height / 3)][y];
                    int w4B = int_img_mat[x + (height / 3)][y + width - 1];
                    int w4C = int_img_mat[x + 2 * (height / 3) - 1][y + width - 1];
                    int w4D = int_img_mat[x + 2 * (height / 3) - 1][y];
                    int white4 = w4C + w4A - w4B - w4D;

                    int b4Ab = int_img_mat[x + 2 * (height / 3)][y];
                    int b4Bb = int_img_mat[x + 2 * (height / 3)][y + width - 1];
                    int b4Cb = int_img_mat[x + height - 1][y + width - 1];
                    int b4Db = int_img_mat[x + height - 1][y];
                    int black4b = b4Cb + b4Ab - b4Bb - b4Db;

                    return ((black4t + black4b) - white4) / (width * height);
                case 5:
                    // 5 = [1,0]
                    //     [0,1]
                    // FOR SCALE 1 CASE
                    if(width == 2 && height == 2){
                        return (int_img_mat[x][y]+int_img_mat[x+1][y+1])-(int_img_mat[x][y+1]+int_img_mat[x+1][y]);
                    }
                    int b5At = int_img_mat[x][y];
                    int b5Bt = int_img_mat[x][y + (width / 2) - 1];
                    int b5Ct = int_img_mat[x + (height / 2) - 1][y + (width / 2) - 1];
                    int b5Dt = int_img_mat[x + (height / 2) - 1][y];
                    int black5t = b5Ct + b5At - b5Bt - b5Dt;

                    int w5At = int_img_mat[x][y + (width / 2)];
                    int w5Bt = int_img_mat[x][y + width - 1];
                    int w5Ct = int_img_mat[x + (height / 2) - 1][y + width - 1];
                    int w5Dt = int_img_mat[x + (height / 2) - 1][y + (width / 2)];
                    int white5t = w5Ct + w5At - w5Bt - w5Dt;

                    int w5Ab = int_img_mat[x + (height / 2)][y];
                    int w5Bb = int_img_mat[x + (height / 2)][y + (width / 2) - 1];
                    int w5Cb = int_img_mat[x + height - 1][y + (width / 2) - 1];
                    int w5Db = int_img_mat[x + height - 1][y];
                    int white5b = w5Cb + w5Ab - w5Bb - w5Db;

                    int b5Ab = int_img_mat[x + (height / 2)][y + (width / 2)];
                    int b5Bb = int_img_mat[x + (height / 2)][y + width - 1];
                    int b5Cb = int_img_mat[x + height - 1][y + width - 1];
                    int b5Db = int_img_mat[x + height - 1][y + (width / 2)];
                    int black5b = b5Cb + b5Ab - b5Bb - b5Db;

                    return ((black5t + black5b) - (white5t + white5b)) / (width * height);
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Index Out of Bounds for feature type: " +feature.feature_type+ " dimensions: (" +width + ", " +height+ ") for Position: (" + x + ", " + y + ")");
        }
        return 0;
    }

    /**
     * Serves to rotate the feature a certain amount of times
     * @param rotateIt
     * @param angle
     * @return
     */
    public int[][] rotateFeature(int[][] rotateIt, int angle){

        int[][] rotatedOne = null;

        for(int k=0; k<angle; k++){

            int height = rotateIt.length;
            int width = rotateIt[0].length;
            rotatedOne = new int[width][height];

            for(int i = 0 ; i < width; i++){
                for(int j = 0 ; j < height; j++){
                    //rotatedOne[i][j] = rotateIt[height-j-1][i];
                    rotatedOne[i][j] = rotateIt[j][i];

                }
            }
            rotateIt = rotatedOne;
        }

        return rotatedOne;
    }


    /**
     *
     */
    public void readIm(){
        //System.out.println("Reading image");
        try {
            File input = new File(fixedPath);
            image = ImageIO.read(input);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param array
     */
    public void printArray(int[][] array) {
        for(int i=0; i< array.length; i++){
            System.out.println(Arrays.toString(array[i]));
        }
        System.out.println();
    }


}

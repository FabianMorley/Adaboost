package camera.Haar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class runHaar {

    public BufferedImage image = null;
    public String originalPath= "src/images/ok.jpg";
    public String fixedPath= "src/images/resized.jpg";
    public Object[] haarFeatures;
    public boolean correctImageFormat = true;
    public int imageSize = 40;
    public double featureThreshold = 0.90; // Could be used for the recognizing of sums

    /**
     * Initializes the Haar method.
     */
    public runHaar(){

    }

    /**
     * This is the main runner which can call image resize method
     */
    public void runner() {

        // If statement to reformat the image
        if (!correctImageFormat) {
            FixImage run1 = new FixImage();
            //run1.makeGrayScale(); // This is to transform an image from rgb to greyscale
            run1.resizeImage(imageSize, imageSize);
        }

        readIm(); // This reads the resized image so we can use its pixels later

        // Serves as an iterator to see how many times we can increase size of features
        int iterations = (int) Math.floor(Math.sqrt(imageSize/2));
        System.out.println("Iterations for size of Haar features: " + iterations);
        //iterations = 1; // Just used to avoid computational overload

        // Per iteration the size of the features increase
        for(int round = 1; round<iterations+1;round++) { // Represents the increase in size for each feature

            MakeFeatures run2 = new MakeFeatures(round,round);
            haarFeatures = run2.createHaarPixel(round,round); // Now we have the four de Haar types

            int[][] type1 = (int[][]) haarFeatures[0]; // Black, white horizontal
            int[][] type2 = rotateFeature(type1,1); // Black, white vertical
            int[][] type3 = (int[][]) haarFeatures[1]; // White black white horizontal
            int[][] type4 = rotateFeature(type3, 1); // White black white vertical
            int[][] type5 = (int[][]) haarFeatures[2]; // Black and white diagonals

            // Can outcomment these
            printArray(type1);
            printArray(type2);
            printArray(type3);
            printArray(type4);
            printArray(type5);

            /*
            When checking each of these features, the reverse will also indirectly analyzed
            A high value close to 1 signifies that this feature is present there for example (black, white) pixel
            While a value close to -1 signfies the reverse feature is present, (white, black) for example

            In the following section I will start giving Haar sums for each type of feature
             */

            // Can check sums by print, and will see the values get close to 1 or -1 when there is a difference
            double[][] sum1 = checkHaar(type1);
            double[][] sum2 = checkHaar(type2);
            double[][] sum3 = checkHaar(type3);
            double[][] sum4 = checkHaar(type4);
            double[][] sum5 = checkHaar(type5);

            // TODO - Must be a way to use the 2d array of sums, possible save/recognize the features and locations based on threshold
            // Continue on Viola Jones algorithm
            // Option could be to have a HashMap where the key is the feature (and its size) and it maps to the coordinate location..

        }
    }


    /**
     * Given a feature I will run it through every pixel of the image and calculate matchhood
     * @param feature
     * @return
     */
    public double[][] checkHaar(int[][] feature){

        // TODO - this section will compare the Haar pixel to those in the image
        // The way i will aproach
        int FHeight = feature.length;
        int FWidth = feature[0].length;

        double[][] sums = new double[image.getHeight()][image.getWidth()];

        for(int i = 0; i<image.getHeight()-FHeight+1; i++){
            for (int j = 0; j<image.getWidth()-FWidth+1; j++){

                double[][] checkThis = new double[FHeight][FWidth];
                for(int m = 0; m<feature.length; m++){
                    for (int n = 0; n<feature[0].length; n++) {

                        int pixelValue =  (image.getRGB(j+n,i+m)) & 0xff;
                        //System.out.println("Location: (" + (j+n) +", " + (i+m) +")");
                        //System.out.println("Pixel: " + pixelValue);
                        double normValue = 1-(double)pixelValue/255;
                        //System.out.println("Norm: " + normValue);

                        checkThis[m][n] = normValue;

                    }
                }

                sums[i][j] = deltaCalculation(feature, checkThis);
            }
        }

        return sums;

    }

    /**
     *
     * @param feature
     * @param section
     * @return
     */
    public double deltaCalculation(int[][] feature, double[][] section){
        // This section will calculate the delta difference between Haar and the black and white one
        // Could implement Integral image

        // Using simple Haar formula to compare black and white pixels
        double blackSum = 0;
        int blackCount = 0;
        double whiteSum = 0;
        int whiteCount = 0;

        for(int i = 0; i< feature.length; i++){
            for (int j = 0; j<feature[0].length; j++) {
                if(feature[i][j] == 1){
                    blackCount++;
                    blackSum = blackSum + section[i][j];
                }
                else{
                    whiteCount++;
                    whiteSum = whiteSum + section[i][j];
                }
            }
        }

        //System.out.println("Black Sum " + blackSum);
        //System.out.println("White Sum " + whiteSum);

        double totalSum = blackSum/blackCount - whiteSum/whiteCount;

        return totalSum;
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


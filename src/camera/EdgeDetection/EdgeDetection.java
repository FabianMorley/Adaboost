package camera.EdgeDetection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EdgeDetection {

    public File Input1;
    public File Output1;
    public BufferedImage imgReturn;

    public EdgeDetection() {
        turnImageIntoBW();
        readImage();
    }


    /**
     * Could use the BW method in Haar
     */
    public void turnImageIntoBW(){

        System.out.println("Transforming image");
        try {
            File input = new File("java/camera/images/ok.jpg");
            BufferedImage image = ImageIO.read(input);

            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D graphic = result.createGraphics();
            graphic.drawImage(image, 0, 0, Color.WHITE, null);

            for (int i = 0; i < result.getHeight(); i++) {
                for (int j = 0; j < result.getWidth(); j++) {
                    Color c = new Color(result.getRGB(j, i));
                    int red = (int) (c.getRed() * 0.299);
                    int green = (int) (c.getGreen() * 0.587);
                    int blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(
                            red + green + blue,
                            red + green + blue,
                            red + green + blue);
                    result.setRGB(j, i, newColor.getRGB());
                }
            }

            File output = new File("java/camera/images/grey1.jpg");
            ImageIO.write(result, "jpg", output);

        }  catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void readImage(){

        System.out.println("Read BW image");
        BufferedImage img = null;
        BufferedImage gry = null;
        int height = 0;
        int width = 0;

        try {
            img = ImageIO.read(new File("java/camera/images/ok1.jpg"));
            gry = ImageIO.read(new File("java/camera/images/grey1.jpg"));
            height = gry.getHeight();
            width = gry.getWidth();

        } catch (IOException e) {
            System.out.println("Problem 1");
        }

        //System.out.println("Pixel value: " + img.getRGB(3,5));
        //System.out.println(img);


        assert img != null;
        System.out.println("Hereee: " + height);


        int[][] kx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}; // This will be the kernel for horizonatal edge detection called x
        int[][] ky = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};  //This will be the kernel for vertical edge detection called y

        int[][] main = new int[height][width];
        BufferedImage result = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_RGB);


        System.out.println("Value pixel:  "+ gry.getRGB(0,0));


        for (int i=1; i<(width-2); i++){
            for (int j=1; j<(height-2); j++){

                int[][] mat1 = new int[3][3];
                int[][] mat2 = new int[3][3];

                for(int m = -1; m<mat1.length-1; m++){
                    for(int n = -1; n<mat1[0].length-1;n++){
                        mat1[m+1][n+1] = gry.getRGB(i+m,j+n) * kx[m+1][n+1];
                        mat2[m+1][n+1] = gry.getRGB(i+m,j+n) * ky[m+1][n+1];

                    }
                }

                int sum1=0;
                int sum2 = 0;
                for (int ii=0; ii<mat1.length;ii++) {
                    for (int jj = 0; jj < mat1[ii].length; jj++) {
                        sum1 += mat1[ii][jj];
                        sum2 += mat2[ii][jj];
                    }
                }

                double modsum = Math.sqrt(Math.pow(sum1,2) + Math.pow(sum2, 2));

                //main[i-1][j-1] = (int) modsum - 2 * gry.getRGB(i,j); // Not needed atm

                System.out.println("Final Pixel Val: " + ((int) modsum - 2* gry.getRGB(i,j)));
                result.setRGB(i,j, (int) modsum - 2* gry.getRGB(i,j));
            }
        }


        try {
            File output = new File("java/camera/images/edge.jpg");
            ImageIO.write(result, "jpg", output);
        } catch (IOException e) {
            System.out.println("Failed :))");
        }

        //imgReturn = img;

    }

    public BufferedImage returnImg(){
        return imgReturn;
    }
}

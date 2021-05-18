package camera.Haar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FixImage {

    /**
     *
     */

    public FixImage(){

    }

    /**
     *
     */
    public BufferedImage makeGrayScale(String pathName){
        //System.out.println("Transforming image");
        try {
            File input = new File(pathName);
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

            File output = new File("src/camera/images/grey.jpg");
            ImageIO.write(result, "jpg", output);
            return result;

        }  catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param targetWidth
     * @param targetHeight
     */
    public BufferedImage resizeImage(int targetWidth, int targetHeight) {
        BufferedImage originalImage = null;

        // Read the previous image
        try {
            File input = new File("src/camera/images/grey.jpg");
            originalImage = ImageIO.read(input);

        } catch (IOException e) {
            e.printStackTrace();
        }


        // Resize that image
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        try {
            File output2 = new File("src/camera/images/resized.jpg");
            ImageIO.write(resizedImage, "jpg", output2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resizedImage;
    }

    /**
     *
     * @param image
     */
    public void displayPixelValue(BufferedImage image){

        int p = image.getRGB(0,0);
        System.out.println("Pixel value at: " + p);

        int a = (p>>24) & 0xff;
        System.out.println("Alpha value at: " + a);

        //get red
        int r = (p>>16) & 0xff;
        System.out.println("Red value at: " + r);

        //get green
        int g = (p>>8) & 0xff;
        System.out.println("Green value at: " + g);

        //get blue
        int b = p & 0xff;
        System.out.println("Blue value at: " + b);

        // In greyscale images, r,g,b are all going to be the same value which will represent a value
    }
}


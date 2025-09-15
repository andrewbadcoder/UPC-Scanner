package HW1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DUImage {
    private BufferedImage image;

    public DUImage(String filename) {
        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println("Error loading image: " + filename);
            System.exit(1);
        }
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int getRed(int x, int y) {
        int rgb = image.getRGB(x, y);
        return (rgb >> 16) & 0xFF;
    }

    public int getGreen(int x, int y) {
        int rgb = image.getRGB(x, y);
        return (rgb >> 8) & 0xFF;
    }

    public int getBlue(int x, int y) {
        int rgb = image.getRGB(x, y);
        return rgb & 0xFF;
    }
}

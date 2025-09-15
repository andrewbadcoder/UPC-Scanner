package HW1;

public class DUImageTest {
    public static void main(String[] args) {
        // Load one of your provided barcode images
        DUImage img = new DUImage("barcode1.png"); // adjust filename as needed

        // Check dimensions
        System.out.println("Width: " + img.getWidth());
        System.out.println("Height: " + img.getHeight());

        // Sample a few pixels
        int midX = img.getWidth() / 2;
        int midY = img.getHeight() / 2;

        int red = img.getRed(midX, midY);
        int green = img.getGreen(midX, midY);
        int blue = img.getBlue(midX, midY);

        System.out.println("Pixel at center (R,G,B): " + red + "," + green + "," + blue);
    }
}
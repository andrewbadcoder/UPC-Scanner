package HW1;

public class TestUPCScan {
    public static void main(String[] args) {
        // Change this filename to the actual barcode file you have
        String filename = "barcode1.png";
        int[] bits = UPC.scanImage(filename);

        System.out.println("Scanned " + bits.length + " bits:");
        for (int i = 0; i < bits.length; i++) {
            System.out.print(bits[i]);
        }
        System.out.println();

        // Optional: compare to expected 95-bit string from the assignment PDF
        String expected = "10100110010010011011110101000110110001010111101010100010010010001110100111"
                        + "001011001101101100101";
        StringBuilder sb = new StringBuilder();
        for (int b : bits) sb.append(b);
        if (sb.toString().equals(expected)) {
            System.out.println("scanImage() output matches expected pattern.");
        } else {
            System.out.println("scanImage() output differs from expected pattern.");
        }
    }
}



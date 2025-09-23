package HW1;

import java.util.Arrays;

public class UPC {
    
    //--------------------------------------------
    // Scan in the bit pattern from the image
    // Takes the filename of the image
    // Returns an int array of the 95 scanned bits
    //--------------------------------------------
    public static int[] scanImage(String filename) {
        // Load the image using DUImage
        DUImage img = new DUImage(filename);

        // 95 bits total in UPC-A
        int[] bits = new int[95];

        // scan across the middle of the image
        int y = img.getHeight() / 2;

        // starting x-coordinate for first bit (given in the PDF)
        int startX = 5;

        // each bit = 2 pixels wide
        int bitWidth = 2;

        // read all 95 bits
        for (int i = 0; i < 95; i++) {
            int x = startX + i * bitWidth;
            // check the red channel
            int red = img.getRed(x, y);
            // black bar = 1, white space = 0
            bits[i] = (red < 128) ? 1 : 0;
        }

        return bits;
    }
    
    //--------------------------------------------
    // Finds the matching digit for the given pattern
    // This is a helper method for decodeScan
    // Takes a 7-bit pattern array and a boolean indicating left/right
    // Returns the digit (0-9) that matches, or -1 if no match
    //--------------------------------------------
    public static int matchPattern(int[] pattern, boolean isLeft) {
        
        int[][] digitPat = {{0,0,0,1,1,0,1},
                            {0,0,1,1,0,0,1},    
                            {0,0,1,0,0,1,1},
                            {0,1,1,1,1,0,1},
                            {0,1,0,0,0,1,1},
                            {0,1,1,0,0,0,1},
                            {0,1,0,1,1,1,1},
                            {0,1,1,1,0,1,1},
                            {0,1,1,0,1,1,1},
                            {0,0,0,1,0,1,1}};
        
        for (int i = 0; i < 10; i++) {
            boolean match = true;
            for (int j = 0; j < 7; j++) {
                if (isLeft) {
                    // For left side, match directly
                    if (pattern[j] != digitPat[i][j]) {
                        match = false;
                        break;
                    }
                } else {
                    // For right side, match inverted pattern
                    if (pattern[j] != (1 - digitPat[i][j])) {
                        match = false;
                        break;
                    }
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }
    
    //--------------------------------------------
    // Performs a full scan decode that turns all 95 bits
    // into 12 digits
    // Returns null if decoding fails, or the decoded digits as a string
    //--------------------------------------------
    public static int[] decodeScan(int[] scanPattern) {
        // Define the expected guard patterns
        int[] startPattern = {1, 0, 1};
        int[] middlePattern = {0, 1, 0, 1, 0};
        int[] endPattern = {1, 0, 1};

        // 1. VERIFY GUARD PATTERNS
        // Check Start Pattern (bits 0-2)
        for (int i = 0; i < 3; i++) {
            if (scanPattern[i] != startPattern[i]) {
                return null; // Invalid start pattern
            }
        }

        // Check Middle Pattern (bits 45-49)
        for (int i = 0; i < 5; i++) {
            if (scanPattern[45 + i] != middlePattern[i]) {
                return null; // Invalid middle pattern
            }
        }

        // Check End Pattern (bits 92-94)
        for (int i = 0; i < 3; i++) {
            if (scanPattern[92 + i] != endPattern[i]) {
                return null; // Invalid end pattern
            }
        }
        
        // 2. DECODE DIGITS
        int[] decodedDigits = new int[12];

        // Decode the 6 LEFT digits
        for (int i = 0; i < 6; i++) {
            // Extract 7-bit pattern for this digit
            int startIdx = 3 + (i * 7);
            int[] pattern = new int[7];
            for (int j = 0; j < 7; j++) {
                pattern[j] = scanPattern[startIdx + j];
            }
            
            decodedDigits[i] = matchPattern(pattern, true);
            if (decodedDigits[i] == -1) {
                return null; // Failed to decode left digit
            }
        }

        // Decode the 6 RIGHT digits
        for (int i = 0; i < 6; i++) {
            // Extract 7-bit pattern for this digit
            int startIdx = 50 + (i * 7);
            int[] pattern = new int[7];
            for (int j = 0; j < 7; j++) {
                pattern[j] = scanPattern[startIdx + j];
            }
            
            decodedDigits[i + 6] = matchPattern(pattern, false);
            if (decodedDigits[i + 6] == -1) {
                return null; // Failed to decode right digit
            }
        }

        return decodedDigits;
    }
    
    //--------------------------------------------
    // Helper method to reverse a bit array
    //--------------------------------------------
    private static int[] reverseBitArray(int[] bits) {
        int n = bits.length;
        int[] reversed = new int[n];
        for (int i = 0; i < n; i++) {
            reversed[i] = bits[n - 1 - i];
        }
        return reversed;
    }
    
    //--------------------------------------------
    // Verify the checksum
    //--------------------------------------------
    public static boolean verifyCode(int[] digits) {
        int evenSum = 0;
        int oddSum = 0;
        
        // Add even-positioned digits (0, 2, 4, 6, 8, 10) and multiply by 3
        for (int i = 0; i < 11; i += 2) {
            evenSum += digits[i];
        }
        evenSum *= 3;
        
        // Add odd-positioned digits (1, 3, 5, 7, 9)
        for (int i = 1; i < 11; i += 2) {
            oddSum += digits[i];
        }
        
        int total = evenSum + oddSum;
        int modResult = total % 10;
        int expectedCheckDigit = (modResult == 0) ? 0 : (10 - modResult);
        
        return digits[11] == expectedCheckDigit;
    }
    
    //--------------------------------------------
    // The main method scans the image, decodes it,
    // and then validates it
    //--------------------------------------------    
    public static void main(String[] args) {
        // file name to process.
        String barcodeFileName = "barcodeUpsidedown.png";

        // optionally get file name from command-line args
        if (args.length == 1) {
            barcodeFileName = args[0];
        }
        
        // scanPattern is an array of 95 ints (0..1)
        int[] scanPattern = scanImage(barcodeFileName);

        // Display the bit pattern scanned from the image
        System.out.println("Original scan");
        for (int i = 0; i < scanPattern.length; i++) {
            System.out.print(scanPattern[i]);
        }
        System.out.println(""); // newline
        
        // Try to decode normally first
        int[] digits = decodeScan(scanPattern);
        
        // If normal decode failed, try upside-down
        if (digits == null) {
            System.out.println("Normal orientation failed, trying upside-down...");
            int[] reversedPattern = reverseBitArray(scanPattern);
            digits = decodeScan(reversedPattern);
            
            // If upside-down worked, reverse the digit order
            
        }
        
        // Check if we got valid digits
        if (digits == null) {
            System.out.println("Scan error - could not decode in either orientation");
            System.exit(1);
        }
        
        // Display the digits
        boolean scanError = false;
        System.out.println("Digits");
        for (int i = 0; i < 12; i++) {
            System.out.print(digits[i] + " ");
            if (digits[i] == -1) {
                scanError = true;
            }
        }
        System.out.println("");
        
        if (scanError) {
            System.out.println("Scan error");
        } else {
            // Verify checksum
            if (verifyCode(digits)) {
                System.out.println("Passed Checksum");
            } else {
                System.out.println("Failed Checksum");
            }
        }
    }
}

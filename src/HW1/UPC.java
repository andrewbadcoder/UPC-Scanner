package HW1;

// This is the starting version of the UPC-A scanner
//   that needs to be filled in for the homework
import java.util.Arrays;

public class UPC {
	
	//--------------------------------------------
	// Scan in the bit pattern from the image
	// Takes the filename of the image
	// Returns an int array of the 95 scanned bits
	//--------------------------------------------
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
	// Takes the full 95 scanned pattern as well as
	//   a starting location in that pattern where we
	//   want to look
	// Also takes in a boolean to indicate if this is a
	//   left or right pattern
	// Returns an int indicating which digit matches
	//   Any pattern that doesn't match anything will be -1
	//--------------------------------------------
	public static int matchPattern(int[] scanPattern, int startIndex, boolean left) {
		
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
		
		for(int i=0;i<10;i++) {
			boolean match=true;
			for(int j=0; j<7;j++) {
				int pattern=scanPattern[startIndex+ j];
				if (left==true){
					 match=pattern==digitPat[i][j];
				}
				else {
					match= pattern== 1- digitPat[i][j];
				}
				if (match==false) {
					break;
				}
				
				
			}
			if(match==true) {
				return i;
			}
		}
		return -1; // TODO: fix this
	}
	
	//--------------------------------------------
	// Performs a full scan decode that turns all 95 bits
	//   into 12 digits
	// Takes the full 95 bit scanned pattern
	// Returns an int array of 12 digits
	//   If any digit scanned incorrectly it is returned as a -1
	// If the start, middle, or end patterns are incorrect
	//   it provides an error and exits
	//--------------------------------------------
	public static int[] decodeScan(int[] scanPattern) {
	    // Define the expected guard patterns [cite: 20]
	    int[] startPattern = {1, 0, 1}; // [cite: 20]
	    int[] middlePattern = {0, 1, 0, 1, 0};// [cite: 20]
	    int[] endPattern = {1, 0, 1}; // [cite: 20]

	    // 1. VERIFY GUARD PATTERNS
	    // Check Start Pattern (bits 0-2)
	    if (!arraySliceEquals(scanPattern, 0, startPattern)) {
	        System.out.println("Error: Invalid start pattern.");
	        System.exit(1); // [cite: 105]
	    }

	    // Check Middle Pattern (bits 45-49)
	    if (!arraySliceEquals(scanPattern, 45, middlePattern)) {
	        System.out.println("Error: Invalid middle pattern.");
	        System.exit(1);// [cite: 105]
	    }

	    // Check End Pattern (bits 92-94)
	    if (!arraySliceEquals(scanPattern, 92, endPattern)) {
	        System.out.println("Error: Invalid end pattern.");
	        System.exit(1);// [cite: 105]
	    }
	    
	    // 2. DECODE DIGITS
	    int[] decodedDigits = new int[12];

	    // Decode the 6 LEFT digits [cite: 114]
	    for (int i = 0; i < 6; i++) {
	        // --- THIS IS THE FIX ---
	        // Calculate the start index for the current left-side digit.
	        // It starts after the 3-bit start pattern.
	        int startIndex = 3 + (i * 7);
	        // Call matchPattern with the full scan, the calculated index, and 'true' for left.
	        decodedDigits[i] = matchPattern(scanPattern, startIndex, true);
	        
	        if (decodedDigits[i] == -1) {
	            System.out.println("Error: Could not decode left-side digit #" + (i + 1));
	            System.exit(1); // [cite: 105]
	        }
	    }

	    // Decode the 6 RIGHT digits [cite: 114]
	    for (int i = 0; i < 6; i++) {
	        // --- THIS IS THE FIX ---
	        // Calculate the start index for the current right-side digit.
	        // It starts after S(3) + L(42) + M(5) = 50.
	        int startIndex = 50 + (i * 7);
	        // Call matchPattern with the full scan, the calculated index, and 'false' for left.
	        decodedDigits[i + 6] = matchPattern(scanPattern, startIndex, false);
	        
	        if (decodedDigits[i + 6] == -1) {
	            System.out.println("Error: Could not decode right-side digit #" + (i + 1));
	            System.exit(1); // [cite: 105]
	        }
	    }

	    return decodedDigits;
	}

	/**
	 * Helper method to check if a slice of a larger array matches a target array.
	 * (This helper method is still useful and does not need to change)
	 */
	private static boolean arraySliceEquals(int[] source, int offset, int[] target) {
	    if ((offset + target.length) > source.length) {
	        return false;
	    }
	    for (int i = 0; i < target.length; i++) {
	        if (source[offset + i] != target[i]) {
	            return false;
	        }
	    }
	    return true;
	}
	//--------------------------------------------
	public static boolean verifyCode(int[] digits) {
		
		//In the UPC-A system, the check digit is calculated as follows:
		//	1.Add the digits in the even-numbered positions (zeroth, second, fourth, sixth, etc.) together and multiply by three.
		//	2.Add the digits in the even-numbered positions (first, third, fifth, etc.) to the result.
		//	3.Find the result modulo 10 (i.e. the remainder when divided by 10.. 10 goes into 58 5 times with 8 leftover).
		//	4.If the result is not zero, subtract the result from ten.
		int evenSum = 0;
		int oddSum = 0;
		int res  = 0;
		for(int i = 0; i < 11; ++i)
		{
			if(i % 2 == 0)
			{
				evenSum = evenSum + digits[i];
				
			}
			else
			{
				oddSum = oddSum + digits[i];
			}
		}
		evenSum = evenSum * 3;
		res = evenSum + oddSum;
		
		int modRes = res % 10;
		int actualDigit;
		if(modRes != 0)
		{
			actualDigit = 10 - modRes;
			
		} 
		else
		{
			actualDigit = 0;
		}
		
		int testDigit = digits[11];
		
		
		return testDigit == actualDigit;		

		// Note that what the UPC standard calls 'odd' are our evens since we are zero based and they are one based
		
		// YOUR CODE HERE...// TODO: fix this
	}
	
	//--------------------------------------------
	// The main method scans the image, decodes it,
	//   and then validates it
	//--------------------------------------------	
	public static void main(String[] args) {
	        // file name to process.
	        // Note: change this to other files for testing
	        String barcodeFileName = "barcode1.png";

	        // optionally get file name from command-line args
	        if(args.length == 1){
		    barcodeFileName = args[0];
		}
		
		// scanPattern is an array of 95 ints (0..1)
		int[] scanPattern = scanImage(barcodeFileName);

		// Display the bit pattern scanned from the image
		System.out.println("Original scan");
		for (int i=0; i<scanPattern.length; i++) {
			System.out.print(scanPattern[i]);
		}
		System.out.println(""); // the \n
				
		
		// digits is an array of 12 ints (0..9)
		int[] digits = decodeScan(scanPattern);
		
		// YOUR CODE HERE TO HANDLE UPSIDE-DOWN SCANS
		if (scanError) {
		    int[] reversedPattern = new int[95];
		    for (int i = 0; i < 95; i++) {
		        reversedPattern[i] = 1 - scanPattern[94 - i];
		    }
		    
		    digits = decodeScan(reversedPattern);
		    
		    if (digits != null) {
		        scanError = false;
		        for (int i = 0; i < 12; i++) {
		            if (digits[i] == -1) {
		                scanError = true;
		                break;
		            }
		        }
		        
		        if (!scanError) {
		            int[] correctedDigits = new int[12];
		            for (int i = 0; i < 12; i++) {
		                correctedDigits[i] = digits[11 - i];
		            }
		            digits = correctedDigits;
		        }
		    }
		}
		
		
		
		// Display the digits and check for scan errors
		boolean scanError = false;
		System.out.println("Digits");
		for (int i=0; i<12; i++) {
			System.out.print(digits[i] + " ");
			if (digits[i] == -1) {
				scanError = true;
			}
		}
		System.out.println("");
				
		if (scanError) {
			System.out.println("Scan error");
			
		} else { // Scanned in correctly - look at checksum
		
			if (verifyCode(digits)) {
				System.out.println("Passed Checksum");
			} else {
				System.out.println("Failed Checksum");
			}
		}
	}
}


package HW1;

// This is the starting version of the UPC-A scanner
//   that needs to be filled in for the homework

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
		
		// YOUR CODE HERE...
		return null; // TODO: fix this
	}
	
	//--------------------------------------------
	// Do the checksum of the digits here
	// All digits are assumed to be in range 0..9
	// Returns true if check digit is correct and false otherwise
	//--------------------------------------------
	public static boolean verifyCode(int[] digits) {
		
		//In the UPC-A system, the check digit is calculated as follows:
		//	1.Add the digits in the even-numbered positions (zeroth, second, fourth, sixth, etc.) together and multiply by three.
		//	2.Add the digits in the even-numbered positions (first, third, fifth, etc.) to the result.
		//	3.Find the result modulo 10 (i.e. the remainder when divided by 10.. 10 goes into 58 5 times with 8 leftover).
		//	4.If the result is not zero, subtract the result from ten.

		// Note that what the UPC standard calls 'odd' are our evens since we are zero based and they are one based
		
		// YOUR CODE HERE...
		return false; // TODO: fix this
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


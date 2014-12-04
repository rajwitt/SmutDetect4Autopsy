/**
 * SmutDetect
 * Copyright (C) 2014 Rajmund Witt
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package uk.co.smutdetect;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Container for indexed Images which hold the statistical information of the 
 * Image.
 * 
 * @author Rajmund Witt
 * @version 0.5
 * 
 * @see LogWriter
 * @see SystemInformation
 */
public class SmutDetectCategorisedImage implements Comparable {
	
	
	// existing information / required
	private int width_;
	private int height_;
	
	// needs to be computed
	private boolean hasSkinTone_;
	private boolean isProcessedCorrectly_;
	private int numberOfPixels_;
	private int numberOfRgbSkinToneHits_;
	private int numberOfYCbCrSkinToneHits_;
	private double preciseRgbPercentage_;
	private double preciseYCbCrPercentage_;
	private double preciseAveragePercentage_;
	private int readableRgbPercentage_;
	private int readableYCbCrPercentage_;
	private int readableAveragePercentage_;

	
	
//////////////////////////////////CONSTRUCTORS//////////////////////////////////

	private SmutDetectCategorisedImage() {
		// do nothing as not allowed
	}

	public SmutDetectCategorisedImage(int width,int height) {
		
		// check if passed values make sense - otherwise change verification
		// state
		if ((width > 0 && width < 100000) && (height > 0 && height < 100000)) {
			width_ = width;
			height_ = height;
			numberOfPixels_ = width_ * height_;
			
		} else {
			isProcessedCorrectly_ = false;
			width_ = 100;
			height_ = 100;
			numberOfPixels_ = 10000;
		}		
	
		// default values
		hasSkinTone_ = false;
		isProcessedCorrectly_ = true;
		numberOfRgbSkinToneHits_ = 0;
		numberOfYCbCrSkinToneHits_ = 0;
		preciseRgbPercentage_ = 0.0;
		preciseYCbCrPercentage_ = 0.0;
		preciseAveragePercentage_ = 0.0;
		readableRgbPercentage_ = 0;
		readableYCbCrPercentage_ = 0;
		readableAveragePercentage_ = 0;
			
				
	}
	
/////////////////////////////////////GETTERS////////////////////////////////////

	
	public int getWidth() {
		return width_;
	}
	
	public int getHeight() {
		return height_;
	}
	

	public boolean getHasSkinTone() {
		return hasSkinTone_;
	}
	
	public boolean getIsProcessedCorrectly() {
		return isProcessedCorrectly_;
	}	
	
	public int getNumberOfPixels() {
		return numberOfPixels_;
	}
	
	public double getPreciseRgbPercentage() {
		return preciseRgbPercentage_;
	}
	
	public double getPreciseYCbCrPercentage() {
		return preciseYCbCrPercentage_;
	}
	
	public double getPreciseAveragePercentage() {
		return preciseAveragePercentage_;
	}
	
	public int getReadableRgbPercentage() {
		return readableRgbPercentage_;
	}
	
	public int getReadableYCbCrPercentage() {
		return readableYCbCrPercentage_;
	}
	
	public int getReadableAveragePercentage() {
		return readableAveragePercentage_;
	}
	


/////////////////////////////////////SETTERS////////////////////////////////////

	

	
	public void setHasSkinTone(boolean hasSkinTone) {
		hasSkinTone_ = hasSkinTone;
	}
	

	
/////////////////////////////////////OTHERS/////////////////////////////////////

	public void increasNumberOfRgbSkinToneHits() {
		numberOfRgbSkinToneHits_++;
	}
	
	public void increasNumberOfYCbCrSkinToneHits() {
		numberOfYCbCrSkinToneHits_++;
	}
	
	/**
	 * Computes the Percentages -  Example: 0.666677 and sets the according
	 * variables of the Categorised Image. Should only performed once the 
	 * whole image has been processed.
	 */
	public void computePercentages(boolean usedRGB, boolean usedYCbCr) {
			
		// only compute if numbers are logical otherwise reset for manual check
		// with maximum percentage
		if ((numberOfRgbSkinToneHits_ <= numberOfPixels_) && 
				(numberOfYCbCrSkinToneHits_ <= numberOfPixels_)) {
			
                        if ((numberOfRgbSkinToneHits_ > 0) || 
                                            (numberOfYCbCrSkinToneHits_ > 0)) {
                            hasSkinTone_ = true;
                        }
                    
                    
			MathContext precision = MathContext.DECIMAL32;
			BigDecimal a = new BigDecimal(numberOfRgbSkinToneHits_);
			BigDecimal a2 = new BigDecimal(numberOfYCbCrSkinToneHits_);
			BigDecimal b = new BigDecimal(numberOfPixels_);
			
			try {
				
				a = a.divide(b, precision);
				a2 = a2.divide(b, precision);
				
			} catch (ArithmeticException exc) {
				exc.printStackTrace();
			
			}
			
			preciseRgbPercentage_ = a.doubleValue();
			preciseYCbCrPercentage_ = a2.doubleValue();
			
			readableRgbPercentage_ = formatPercentage(preciseRgbPercentage_);
			readableYCbCrPercentage_ = 
				formatPercentage(preciseYCbCrPercentage_);
			
			// check how average is composed
			if (usedRGB && usedYCbCr) {
				BigDecimal b_avg = new BigDecimal(2);
				
				// add all percentages and divide by the amount of types
				BigDecimal avg = (a.add(a2)).divide(b_avg, precision);
				preciseAveragePercentage_ = avg.doubleValue();
				
			} else if (usedYCbCr) {
				preciseAveragePercentage_ = preciseYCbCrPercentage_;				
			} else {
				preciseAveragePercentage_ = preciseRgbPercentage_;
			}
			readableAveragePercentage_ = 
				formatPercentage(preciseAveragePercentage_);	
			
			
		} else {
			isProcessedCorrectly_ = false;
			// as an image is to be checked manually if errors occur during the
			// analysis the percentage is set to 100% to force listing and 
			// checking			
			preciseRgbPercentage_ = 1.0;
			preciseYCbCrPercentage_ = 1.0;
			preciseAveragePercentage_ = 1.0;
			readableRgbPercentage_ = 100;
			readableYCbCrPercentage_ = 100;
			readableAveragePercentage_ = 100;
		}
		
		
	}
	
	
	
	/**
	 * @return readablePercentage Returns the percentage as a readable/integer
	 *  form from 0 to a 100
	 */
	public int formatPercentage(double percentage) {
		int readablePercentage = (int) (percentage * 100);

		return readablePercentage;		
	}
	

	/**
	 * Compares CategorizedImages and sorts by the following hierarchy: 
	 * skinTone% - LastModified
	 * 
	 * @param CategorizedImage
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object image2) {
		// Concept inspired by http://www.java-tips.org/java-se-tips/java.lang/
		// how-to-use-comparable-interface.html - Downcasting, they show no
		//		try catch though
		// and http://lkamal.blogspot.com/2008/07/
		//			java-sorting-comparator-vs-comparable.html - Which Interface

		double image2Percentage = 0;
		long image2LastMod = 0;
		
		// try accessing properties from other Categorized Image
		try {	
		
			image2Percentage = 
				((SmutDetectCategorisedImage)image2).getPreciseAveragePercentage();

			
                        //image2LastMod = ((SmutDetectCategorisedImage)image2).getLastModified();		
			
			
		} catch (ClassCastException exc) {
			exc.printStackTrace();
			//log_.appendString("Problem when sorting Images - ClassCastError");
			
		} // end try downcasting

		if (preciseAveragePercentage_ > image2Percentage) {
			
			return 1;
			
		} else if (preciseAveragePercentage_ < image2Percentage) {
			
			return -1;
			
		} else {
			/*
			// if same percentage - check for last modified date
			if (lastModified_ > image2LastMod) {
				
				return 1;
				
			} else if (lastModified_ < image2LastMod) {
				
				return -1;
				
			} else {
				
				return 0;
				
			} // end if else lastmodified	
			*/
                        return 0;
		} // end if else skintone%
		
    }
	
	/**
	 * @return the CategorisedImage as a textual representation to ease 
	 * 			report export
	 */
	public String toString() {
		

		StringBuilder theString = new StringBuilder();

		theString.append(readableAveragePercentage_ + "%\n");
		theString.append(width_ + "x" + height_ + " = " + numberOfPixels_);
		theString.append("px\n");
		theString.append("RGB DetectorValue: " + preciseRgbPercentage_);
		theString.append("\nYCbCr DetectorValue: " + preciseYCbCrPercentage_);
		theString.append("\nProcessed correctly: ");
		theString.append(isProcessedCorrectly_);

		
		return theString.toString();
	}

}

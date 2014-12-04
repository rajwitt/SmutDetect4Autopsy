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


/**
 * This class provides an algorithm to test for skin-color in the YCrCb color
 * encoding in the RGB colorspace. The theory of the algorithm was not developed
 * by me but is deduced from the hybrid filter of File Hound which is analyzed
 * in the following Paper:
 * "A Novel Skin Tone Detection Algorithm for Contraband Image
 * Analysis" by Abhishek Choudhury, Marcus Rogers, Blair Gillam and 
 * Keith Watson - Purdue Cyber Forensic Lab & Purdue University
 * 
 * Another paper discussing the algorithm:
 * A Survey on Pixel-Based Skin Color Detection Techniques
 * by Vladimir Vezhnevets, Vassili Sazonov & Alla Andreeva
 * Graphics and Media Laboratory
 * Faculty of Computational Mathematics and Cybernetics
 * Moscow State University, Moscow, Russia
 * 
 * The color conversion of RGB to YCbCr encoding is based on the JPEG File
 * Interchange Format Specification Version 1.02 from 1992
 * http://www.jpeg.org/public/jfif.pdf
 * 
 * According to the above papers JPEG saves information natively in 
 * YCbCr hence this should be possible without the complex calculations
 * 
 * Based on the filter described in the paper: A Novel Skin Tone 
 * Detection Algorithm for Contraband Image Analysis -  Y is irrelevant
 * 
 *  
 * @author Rajmund Witt
 * @version 0.5.01
 */
public abstract class YCbCrSkinToneDetector {
	
	
	/**
	 * Checks if the provided Color is detected as skin-tone in the YCrCb
	 * encoding.
	 * 
	 * @param color color in sRGB encoding
	 * @return ifDetectedAsSkin
	 */
	public static boolean checkColor(int color) {
	
		// redesign of this implementation is discussed here:
		// http://old4ensics.grap3.net/blog

	
		boolean isSkinTone = false;
		
		int red = (color & 0x00ff0000) >> 16;
		int green = (color & 0x0000ff00) >> 8;
		int blue = color & 0x000000ff;
		
		int cb = (int)((-0.1687*red) + (-0.3313*green) + (0.5*blue) + 128);
		int cr = (int)((0.5*red) + (-0.4187*green) + (-0.0813*blue) + 128);
		
		if ((cb >= 77) && (cb <= 127) && (cr >= 133) && (cr <= 173)) {
			isSkinTone = true;			
		}
		
		return isSkinTone;
		
	}
	
	
	

}

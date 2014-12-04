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
 * This class provides an algorithm to test for skin-color in the sRGB color
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
 * @author Rajmund Witt
 * @version 0.5.01
 */
public abstract class RgbSkinToneDetector {
	
	
	/**
	 * Checks if the provided Color is detected as skin-tone in the sRGB
	 * encoding.
	 * 
	 * @param color color in sRGB encoding
	 * @return ifDetectedAsSkin
	 */
	public static boolean checkColor(int color) {
		
		/*
		 * This algorithm is deduced from the hybrid filter of File Hound 
		 * analyzed in the following Paper:
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
		 */	
		
		boolean isSkinTone = false;
		
		int red = (color & 0x00ff0000) >> 16;
		int green = (color & 0x0000ff00) >> 8;
		int blue = color & 0x000000ff;
		int max;
		int min;
		
		if ((red > green) && (red > blue)) {
			max = red;
		} else if ((green > red) && (green > blue)) {
			max = green;
		} else {
			max = blue;
		}
		
		if ((red < green) && (red < blue)) {
			min = red;
		} else if ((green < red) && (green < blue)) {
			min = green;
		} else {
			min = blue;
		}
		
		
		if ((red > 95) && (green > 40) && (blue > 20) && ((max-min) > 15) &&
				(Math.abs((red-green)) > 15) && (red > green) && (red > blue)) {
			isSkinTone = true;			
		}		
		
		
		return isSkinTone;
		
	}
	
	
	
	
	

}

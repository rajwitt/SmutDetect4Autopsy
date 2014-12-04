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

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.ReadContentInputStream;

/**
 *
 * @author Rajmund Witt <code@4ensics.co.uk>
 */
public abstract class SmutDetectImageScanner {
    
     private static final Logger logger_ = Logger.getLogger(
                                    SmutDetectImageScanner.class.getName());
    
     /**
      * 
      * @param file 
      */
     public static SmutDetectCategorisedImage scanImage(AbstractFile file) {
         InputStream iStream;
         SmutDetectCategorisedImage cImage;
         BufferedImage bImage;
         
         try {
             // get the file from Autopsy
             iStream = new ReadContentInputStream(file);
             // Load as Image
             bImage = ImageIO.read(iStream);
             
             //Check load and set Result container
             if (bImage != null) {
                 cImage = new SmutDetectCategorisedImage(
                         bImage.getWidth(), 
                         bImage.getHeight());
             } else {
                 return null;
             }
             
             // scan the image
             
             // scan x axis
             for (int i = 0; i < bImage.getWidth(); i++) {
                // scan y axis
                for (int j = 0; j < bImage.getHeight(); j++) {
                    
                    // Check RGB value
                    if (RgbSkinToneDetector.checkColor(bImage.getRGB(i,j))) {
                        cImage.increasNumberOfRgbSkinToneHits();
                    }
                    // Check YCbCr value
                    if (YCbCrSkinToneDetector.checkColor(bImage.getRGB(i,j))) {
                        cImage.increasNumberOfYCbCrSkinToneHits();
                    }
                    
                } // end scan y axis               
             } // end scan x axis
             
             // update the result container
             cImage.computePercentages(true, true);
                                  
                     
             return cImage;        
             
         } catch (Exception e) {
             logger_.log(Level.WARNING, 
                     "Error scanning image for Skintone Analysis");
         }          
                 
         return null;
         
     }    
 
    
}

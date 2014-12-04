/**
 * SmutDetect4Autopsy
 * Copyright (C) 2014 Rajmund Witt
 * 
 * Derived from Sample Module provided with Autopsy 3.1.
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
package uk.co.smutdetect.autopsy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestMessage;
import org.sleuthkit.autopsy.ingest.IngestServices;
import org.sleuthkit.autopsy.ingest.ModuleDataEvent;
import org.sleuthkit.autopsy.ingest.IngestModuleReferenceCounter;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardArtifact.ARTIFACT_TYPE;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;
import uk.co.smutdetect.SmutDetectCategorisedImage;
import uk.co.smutdetect.SmutDetectImageScanner;

/**
 * 
 * @author Rajmund Witt <code@4ensics.co.uk>
 */
class SmutDetectFileIngestModule implements FileIngestModule {

    private static final HashMap<Long, Long> artifactCountsForIngestJobs = new HashMap<>();
    private static int attrId = -1;
    private final boolean skipKnownFiles;
    private IngestJobContext context = null;
    private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();
    private final static String MODULE_NAME = SmutDetectIngestModuleFactory.getModuleName();

    SmutDetectFileIngestModule(SmutDetectIngestJobSettings settings) {
        this.skipKnownFiles = settings.skipKnownFiles();
    }

    @Override
    public void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;
        refCounter.incrementAndGet(context.getJobId());
        /*
        synchronized (SmutDetectFileIngestModule.class) {
            if (attrId == -1) {
                // For this sample, make a new attribute type to use to post 
                // results to the blackboard. There are many standard blackboard 
                // artifact and attribute types and you should use them instead
                // creating new ones to facilitate use of your results by other
                // modules.
                Case autopsyCase = Case.getCurrentCase();
                SleuthkitCase sleuthkitCase = autopsyCase.getSleuthkitCase();
                try {
                    
                    // See if the attribute type has already been defined.
                    attrId = sleuthkitCase.getAttrTypeID("ATTR_SAMPLE");
                    if (attrId == -1) {
                        attrId = sleuthkitCase.addAttrType("ATTR_SAMPLE", "Sample Attribute");
                    }
   
                } catch (TskCoreException ex) {
                    IngestServices ingestServices = IngestServices.getInstance();
                    Logger logger = ingestServices.getLogger(SmutDetectIngestModuleFactory.getModuleName());
                    logger.log(Level.SEVERE, "Failed to create blackboard attribute", ex);
                    attrId = -1;
                    throw new IngestModuleException(ex.getLocalizedMessage());
                }
            }
            
        }
        */
    }

    @Override
    public IngestModule.ProcessResult process(AbstractFile file) {
        if (attrId != -1) {
            return IngestModule.ProcessResult.ERROR;
        }

        // Skip anything other than actual file system files.
        if ((file.getType() == TskData.TSK_DB_FILES_TYPE_ENUM.UNALLOC_BLOCKS)
                || (file.getType() == TskData.TSK_DB_FILES_TYPE_ENUM.UNUSED_BLOCKS)) {
            return IngestModule.ProcessResult.OK;
        }

        // Skip NSRL / known files. if the config allows it :D
        if (skipKnownFiles && file.getKnown() == TskData.FileKnown.KNOWN) {
            return IngestModule.ProcessResult.OK;
        }
        
        //skip unsupported
        if (!parsableFormat(file)) {
            return ProcessResult.OK;
        }

        try {
            
            ////////////////////////DO////STUFFF/////////////////////////
            
            SmutDetectCategorisedImage cImage;
            cImage = SmutDetectImageScanner.scanImage(file);
            int roundedPercentage = 0;
            

            
            if(cImage != null) {
                
                                // Make a result collection
                Collection<BlackboardAttribute> attributes = new ArrayList<BlackboardAttribute>();               
                roundedPercentage = (int)Math.floor((cImage.getReadableAveragePercentage()/10) * 10);
                // Add file comment
                attributes.add(new BlackboardAttribute(
                        BlackboardAttribute.ATTRIBUTE_TYPE.TSK_COMMENT.getTypeID(), 
                        MODULE_NAME,
                        cImage.toString()));
                attributes.add(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_SET_NAME.getTypeID(),
                        MODULE_NAME,
                        ("SmutDetect|" + String.format("%03d", roundedPercentage) + "s")));
                
                // Add  tag name
//                attributes.add(new BlackboardAttribute(
//                        //BlackboardAttribute.ATTRIBUTE_TYPE.TSK_SET_NAME.getTypeID(),
//                        BlackboardAttribute.ATTRIBUTE_TYPE.TSK_TAG_NAME.getTypeID(),
//                        SmutDetectIngestModuleFactory.getModuleName(), ("SmutDetect|" + roundedPercentage + "s")));
 
//                // Add file category
//                attributes.add(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_CATEGORY.getTypeID(),
//                        MODULE_NAME,
//                        "SmutDetect_Category"));
               
                // testing the other attributes:
//                attributes.add(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_KEYWORD.getTypeID(),
//                        MODULE_NAME,
//                        "RW1_TSK_KEYWORD"));
//                attributes.add(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_FLAG.getTypeID(),
//                        SmutDetectIngestModuleFactory.getModuleName(),
//                        "RW1_TSK_FLAG"));

                
                // Test RW1
//                BlackboardArtifact parent = file.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_INTERESTING_FILE_HIT);
//                parent.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_SET_NAME.getTypeID(),
//                        MODULE_NAME,
//                        ("SmutDetectParent")));         
//
//                attributes.add(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_ASSOCIATED_ARTIFACT.getTypeID(),
//                        MODULE_NAME,
//                        parent.getArtifactID()));

                //parent.getSleuthkitCase().

                // Add the attributes, if there are any, to a new artifact

                // Add the to the general info artifact for the file. In a
                // real module, you would likely have more complex data types 
                // and be making more specific artifacts.
                //BlackboardArtifact art = file.getGenInfoArtifact();
                //art.addAttribute(attr);

                if (!attributes.isEmpty()) {                    
                    // add file tags to the case:
                    BlackboardArtifact bba = file.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_INTERESTING_FILE_HIT);    
                    bba.addAttributes(attributes);              
                }
                
                // This method is thread-safe with per ingest job reference counted
                // management of shared data.
                addToBlackboardPostCount(context.getJobId(), 1L);

                // Fire an event to notify any listeners for blackboard postings.
                ModuleDataEvent event = new ModuleDataEvent(SmutDetectIngestModuleFactory.getModuleName(), ARTIFACT_TYPE.TSK_INTERESTING_FILE_HIT);
                IngestServices.getInstance().fireModuleDataEvent(event);
            } // end if SmutDetectCategorisedImage is not empty 
            
            return IngestModule.ProcessResult.OK;

        } catch (TskCoreException ex) {
            IngestServices ingestServices = IngestServices.getInstance();
            Logger logger = ingestServices.getLogger(SmutDetectIngestModuleFactory.getModuleName());
            logger.log(Level.SEVERE, "Error processing file (id = " + file.getId() + ")", ex);
            return IngestModule.ProcessResult.ERROR;
        }
    }

    @Override
    public void shutDown() {
        if (!context.isJobCancelled()) {
            // This method is thread-safe with per ingest job reference counted
            // management of shared data.
            reportBlackboardPostCount(context.getJobId());
        }
    }

    synchronized static void addToBlackboardPostCount(long ingestJobId, long countToAdd) {
        Long fileCount = artifactCountsForIngestJobs.get(ingestJobId);

        // Ensures that this job has an entry
        if (fileCount == null) {
            fileCount = 0L;
            artifactCountsForIngestJobs.put(ingestJobId, fileCount);
        }

        fileCount += countToAdd;
        artifactCountsForIngestJobs.put(ingestJobId, fileCount);
    }

    synchronized static void reportBlackboardPostCount(long ingestJobId) {
        Long refCount = refCounter.decrementAndGet(ingestJobId);
        if (refCount == 0) {
            Long filesCount = artifactCountsForIngestJobs.remove(ingestJobId);
            String msgText = String.format("Posted %d times to the blackboard", filesCount);
            IngestMessage message = IngestMessage.createMessage(
                    IngestMessage.MessageType.INFO,
                    MODULE_NAME,
                    msgText);
            IngestServices.getInstance().postMessage(message);
        }
    }
    
   /**
    * Checks if should try to attempt to scan for skin tone colours.
    * Currently checks if JPEG, BMP, PNG, GIF or TIFF
    * image (by signature)
    *
    * @param f file to be checked
    *
    * @return true if to be processed
    */
    private boolean parsableFormat(AbstractFile f) {
        return isImageFileHeader(f);

    }
    
    /**
    * Check if is image file based on header, does not parse files less than 100
    * bytes.
    *
    * @param file
    *
    * @return true if image file, false otherwise
    */
    @SuppressWarnings("unchecked")
    private static boolean isImageFileHeader(AbstractFile file) {
        
        // if less than 100 bytes, do not parse
        if (file.getSize() < 100) {
            return false;
        }
        
        // read bytes if unable do not parse
        byte[] fileHeaderBuffer = new byte[6];
        int bytesRead;
        try {
            bytesRead = file.read(fileHeaderBuffer, 0, 6);
        } catch (TskCoreException ex) {
            //ignore if can't read the first few bytes, not a JPEG
            return false;
        }
        if (bytesRead != 6) {
            return false;
        }
       /**
        * Check for the Image file headers Starting with most likely image files first.
        * Since Java bytes are signed, we cast them to an int first.
        * @TODO add config check if all image types are to be scanned
        * 
        * Some more signatures from: http://www.garykessler.net/library/file_sigs.html
        * 
        * FF D8 FF              ÿØÿ       any JPG
        * 00 00 00 00 6A 50     ....jP    a JPEG 2000 file
        * 47 49 46 38 37 61     GIF87a    A Gif File
        * 47 49 46 38 39 61 	GIF89a    A Gif File
        * 49 20 49              I I       TIF, TIFF - Tagged Image File Format file
        * 49 49 2A 00           II*.      TIF, TIFF - Tagged Image File Format file (little
        *                                 endian, i.e., LSB first in the byte; Intel) 
        * 4D 4D 00 2A 	  	MM.*      TIF, TIFF - Tagged Image File Format file (big
        *                                 endian, i.e., LSB last in the byte; Motorola)
        * 4D 4D 00 2B 	  	MM.+      TIF, TIFF - BigTIFF files; Tagged Image File Format files >4 GB
        * 89 50 4E 47 0D 0A 1A 0A   ‰PNG....    PNG - Portable Network Graphics
        * 42 4D 	  	BM        BMP - Windows Bitmap image
        * 
        */
        if (
            // if JPG [FF D8 FF]   
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0xFF) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0xD8) && 
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0xFF))
            || //or if BMP [42 4D]
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x42) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x4d))
            || // or if TIFF [49 20 49]
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x49) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x20) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x49))
            || // or if PNG [89 50 4E 47]; not testing for the 4 dots
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x89) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x50) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x4E) && ((int) (fileHeaderBuffer[3] & 0xFF) == 0x47)) 
            || // or if GIF [47 49 46 38]; not testing for subtypes 7a/9a
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x47) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x49) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x46) && ((int) (fileHeaderBuffer[3] & 0xFF) == 0x38))     
            || // or if TIFF [49 49 2A 00]
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x49) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x49) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x2A) && ((int) (fileHeaderBuffer[3] & 0xFF) == 0x00))
            || // or if TIFF [4D 4D 00 2A]
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x4D) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x4D) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x00) && ((int) (fileHeaderBuffer[3] & 0xFF) == 0x2A)) 
            || // or if TIFF [4D 4D 00 2B]
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x4D) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x4D) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x00) && ((int) (fileHeaderBuffer[3] & 0xFF) == 0x2B))    
            || // or if JPEG 2000 [00 00 00 00 6A 50]
            (((int) (fileHeaderBuffer[0] & 0xFF) == 0x00) && ((int) (fileHeaderBuffer[1] & 0xFF) == 0x00) &&
                ((int) (fileHeaderBuffer[2] & 0xFF) == 0x00) && ((int) (fileHeaderBuffer[3] & 0xFF) == 0x00) && 
                ((int) (fileHeaderBuffer[4] & 0xFF) == 0x6A)&& ((int) (fileHeaderBuffer[5] & 0xFF) == 0x50))
            ) {
            return true;
        } // end if is Image header
        return false;
    }

}

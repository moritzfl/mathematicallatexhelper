package de.moritzf.latexhelper;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageFileUtil {

    private static final Logger LOGGER = Logger.getLogger(ImageFileUtil.class.getName());

    public static boolean isImageFile(File file) {
        try {
            ImageIO.read(file).toString();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test if the data in the given byte array represents a PDF file.
     */
    public static boolean isPdf(File file) throws IOException {
        boolean isPdf = true;
        Document document = new Document();
        try {
            document.setFile(file.getAbsolutePath());
        } catch (PDFException | PDFSecurityException ex) {
          isPdf = false;
        }
        return isPdf;
    }

    public static BufferedImage pdfToImage(File pdf) {
        // open the file
        Document document = new Document();
        try {
            document.setFile(pdf.getAbsolutePath());
        } catch (PDFException ex) {
            LOGGER.log(Level.SEVERE, "Error parsing PDF document " + ex);
        } catch (PDFSecurityException ex) {
            LOGGER.log(Level.SEVERE, "Error encryption not supported " + ex);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error file not found " + ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error IOException " + ex);
        }

        // save page captures to file.
        float scale = 1.0f;
        float rotation = 0f;

        // Paint each pages content to an image and
        // write the image to file

        BufferedImage image = null;
        try {
            image = (BufferedImage) document.getPageImage(
                    0, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, rotation, scale);
        } catch (InterruptedException e) {
            //does not happen unless file its corrupted and then it does not matter.
        }
        RenderedImage rendImage = image;
        image.flush();
        // clean up resources
        document.dispose();
        return image;
    }


}
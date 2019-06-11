package de.moritzf.latexhelper.util;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for handling files containing images and images themselves.
 *
 * @author Moritz Floeter
 */
public class ImageFileUtil {

    /**
     * Empty private constructor for ImageFileUtil
     */
    private ImageFileUtil(){
        //Prevents instances of ImageFileUtil
    }

    private static final Logger LOGGER = Logger.getLogger(ImageFileUtil.class.getName());

    /**
     * Tests if file is an image file.
     *
     * @param file the file
     * @return true, if image
     */
    public static boolean isImage(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test if the data in the given byte array represents a PDF file.
     *
     * @param file the file
     * @return true, if pdf file
     * @throws IOException the io exception
     */
    public static boolean isPdf(File file) throws IOException {
        byte[] data = new byte[5];
        InputStream is = new FileInputStream(file);

        boolean isPdf = false;
        if (is.read(data) != data.length) {
            LOGGER.log(Level.INFO, "The file used does appear to be corrupted.");
        } else {
            isPdf = (data[0] == 0x25 &&// %
                    data[1] == 0x50 && // P
                    data[2] == 0x44 && // D
                    data[3] == 0x46 && // F
                    data[4] == 0x2D);
        }


        is.close();

        return isPdf;
    }

    /**
     * Converts pdf to BufferedImage.
     *
     * @param pdf the pdf
     * @return the buffered image
     */
    public static BufferedImage pdfToImage(File pdf) {
        BufferedImage image = null;
        if (pdf.exists()) {
            try {
                PDDocument document = PDDocument.load(pdf);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                //Only convert first page
                image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
                document.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not convert pdf to image. Perhaps the file used was not a valid pdf file.");
            }
        }

        return image;
    }

    /**
     * Converts a given Image to a BufferedImage. If the instance passed is already a BufferedImage, this
     * will return the image unchanged.
     *
     * @param img the img
     * @return the buffered image
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),
                img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bufferedImage;
    }

}
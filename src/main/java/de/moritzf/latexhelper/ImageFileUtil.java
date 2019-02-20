package de.moritzf.latexhelper;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageFileUtil {

    private static final Logger LOGGER = Logger.getLogger(ImageFileUtil.class.getName());

    public static boolean isImageFile(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test if the data in the given byte array represents a PDF file.
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

}
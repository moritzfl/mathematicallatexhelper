package de.moritzf.latexhelper;


import com.itextpdf.text.pdf.PdfReader;
import mathpix.MathPixUtil;
import mathpix.MathpixSettings;
import mathpix.api.response.DetectionResult;
import net.sf.mathocr.BatchProcessor;

import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Text area that can work with images that have content hidden by steganography. If such content is detected, it
 * is pasted into the textarea as text. Will also work with pdf-files that contain the rendered latex expression
 * within their header information.
 *
 * @author Moritz Floeter
 */
public class LatexImportingTextArea extends JTextArea implements KeyListener {

    private static final Logger LOGGER = Logger.getLogger(LatexImportingTextArea.class.getName());

    /**
     * Instantiates a new Steganography text area.
     */
    public LatexImportingTextArea() {
        this.addKeyListener(this);
        this.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                handleDrop(evt);
            }
        });
    }

    private void handleDrop(DropTargetDropEvent evt) {
        if (evt.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> droppedFiles = null;
            try {
                try {
                    droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                } catch (UnsupportedFlavorException e) {
                    // does not happen, was checked a couple of lines earlier
                }

                File droppedFile = droppedFiles.get(0);

                if (ImageFileUtil.isImageFile(droppedFile)) {
                    Image image = ImageIO.read(droppedFile);
                    String latex = extractFromImage(toBufferedImage(image));
                    if (latex != null && !latex.isEmpty()) {
                        this.setText(latex);
                    }
                } else if (ImageFileUtil.isPdf(droppedFile)) {
                    String latex = extractFromPdf(droppedFile);
                    if (latex != null && !latex.isEmpty()) {
                        this.setText(latex);
                    }
                }

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not read file", e);
            }
        } else if (evt.getTransferable().isDataFlavorSupported(DataFlavor.imageFlavor)) {
            BufferedImage image;
            try {
                try {

                    image = toBufferedImage((Image) evt.getTransferable().getTransferData(DataFlavor.imageFlavor));

                    String latex = extractFromImage(toBufferedImage(image));
                    if (latex != null && !latex.isEmpty()) {
                        this.setText(latex);
                    }
                } catch (UnsupportedFlavorException e) {
                    // does not happen, was checked a couple of lines earlier
                }


            } catch (IOException exc) {
                LOGGER.log(Level.SEVERE, "Could not read image from clipboard");
            }
        }
    }

    private String extractFromPdf(File droppedFile) {
        LOGGER.log(Level.INFO, "Extracting text from pdf");
        PdfReader reader = null;
        String latex = null;
        try {
            reader = new PdfReader(droppedFile.getAbsolutePath());
            latex = reader.getInfo().get("latex");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not read pdf", e);
        }

        if (latex == null || latex.isEmpty()) {
            Image image = ImageFileUtil.pdfToImage(droppedFile);
            latex = extractFromImage(toBufferedImage(image));
        } else {
            LOGGER.log(Level.INFO, "Got result from latex attribute in pdf header");
        }

        return latex;
    }

    private String extractFromImage(BufferedImage image) {
        LOGGER.log(Level.INFO, "Extracting text from image");
        String latex = null;
        if (image != null) {

            LOGGER.log(Level.INFO, "Using steganography");
            String text = SteganographyUtil.decode(image);

            // new TeXFormula throws exception if the text used is an  invalid LaTeX expression
            if (text != null && !text.isEmpty()) {
                try {
                    new TeXFormula(text);
                    latex = text;
                } catch (Exception e) {
                    latex = null;
                }
            }

            //If Steganography did not work, try with OCR
            if (latex == null) {
                LOGGER.log(Level.INFO, "Using OCR");
                if (MathpixSettings.isConfigured()) {
                    DetectionResult result = MathPixUtil.getLatex(image);
                    if (result != null && result.getError().isEmpty() && !result.getLatex().isEmpty()) {
                        LOGGER.log(Level.INFO, "Got OCR result using MathPix online API");
                        latex = result.getLatex().replace(" ", "");
                    } else {
                        LOGGER.log(Level.INFO, "Got OCR result using MathOCR library");
                        latex = BatchProcessor.recognizeFormula(image);
                    }
                } else {
                    LOGGER.log(Level.INFO, "Got OCR result using MathOCR library");
                    latex = BatchProcessor.recognizeFormula(image);
                }

            }
        }

        return latex;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == KeyEvent.VK_V) && ((evt.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
            BufferedImage image = getImageFromClipboard();
            if (image != null) {
                String latex = extractFromImage(image);
                if (latex != null && !latex.isEmpty()) {
                    this.setText(latex);
                }
            } else {
                Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                //Check if file or image-file is pasted and return an image
                if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        List<File> droppedFiles = (List<File>)
                                transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        File droppedFile = droppedFiles.get(0);
                        if (ImageFileUtil.isImageFile(droppedFile)) {
                            image = ImageIO.read(droppedFile);
                            String latex = extractFromImage(toBufferedImage(image));
                            if (latex != null && !latex.isEmpty()) {
                                this.setText(latex);
                                evt.consume();
                            }
                        } else if (ImageFileUtil.isPdf(droppedFile)) {
                            String latex = extractFromPdf(droppedFile);
                            if (latex != null && !latex.isEmpty()) {
                                this.setText(latex);
                                evt.consume();
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                        // nothing to do here
                    }
                }

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Gets image from clipboard.
     *
     * @return the image from clipboard
     */
    public BufferedImage getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        BufferedImage image = null;

        //Check if file or image-file is pasted and return an image
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List<File> droppedFiles = (List<File>)
                        transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (droppedFiles.size() == 1) {
                    image = ImageIO.read(droppedFiles.get(0));
                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        } else if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                image = toBufferedImage((Image) transferable.getTransferData(DataFlavor.imageFlavor));
            } catch (UnsupportedFlavorException | IOException e) {
                // handle this as desired
            }
        }
        return image;
    }

    /**
     * Converts a given Image into a BufferedImage.
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            //This will work with steganography as the data is directly taken from the image.
            return (BufferedImage) img;
        }

        // This conversion will not work with steganography as the image is repainted and loses its data.
        // It is however what you would expect this function to deliver as output :P
        BufferedImage bimage = new BufferedImage(img.getWidth(null),
                img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }


}

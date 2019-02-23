package de.moritzf.latexhelper;


import com.itextpdf.text.pdf.PdfReader;
import de.moritzf.latexhelper.util.ImageFileUtil;
import de.moritzf.latexhelper.util.SteganographyUtil;
import mathpix.MathPix;
import mathpix.MathPixSettings;
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

    /**
     * Handle drop events when items are dropped to the gui.
     *
     * @param evt the DropTargetDropEvent
     */
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

                if (ImageFileUtil.isImage(droppedFile)) {
                    Image image = ImageIO.read(droppedFile);
                    String latex = extractFromImage(ImageFileUtil.toBufferedImage(image));
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

                    image = ImageFileUtil.toBufferedImage((Image) evt.getTransferable().getTransferData(DataFlavor.imageFlavor));

                    String latex = extractFromImage(ImageFileUtil.toBufferedImage(image));
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

    /**
     * Extract a latex expression from a given pdf file.
     *
     * @param pdfFile the dropped file
     * @return the string
     */
    private String extractFromPdf(File pdfFile) {
        LOGGER.log(Level.INFO, "Extracting text from pdf");
        PdfReader reader = null;
        String latex = null;
        try {
            reader = new PdfReader(pdfFile.getAbsolutePath());
            latex = reader.getInfo().get("latex");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not read pdf. Perhaps the file used was not a valid pdf file.");
        }

        if (latex == null || latex.isEmpty()) {
            Image image = ImageFileUtil.pdfToImage(pdfFile);
            if (image != null) {
                latex = extractFromImage(ImageFileUtil.toBufferedImage(image));
            }
        } else {
            LOGGER.log(Level.INFO, "Got result from latex attribute in pdf header");
        }

        return latex;
    }

    /**
     * Extract a latex expression from an image.
     *
     * @param image the image
     * @return the string
     */
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

                //Try to use mathpix if it is configured
                if (MathPixSettings.isConfigured()) {
                    DetectionResult result = MathPix.getLatex(image);
                    if (result != null && result.getError().isEmpty() && !result.getLatex().isEmpty()) {
                        LOGGER.log(Level.INFO, "Got OCR result using MathPix online API");
                        latex = result.getLatex().replace(" ", "");
                        // If no result was obtained from mathpix, try with MathOCR
                    }
                }

                //If mathpix was not configured or didn't provide any results, fall back to MathOCR
                if (latex == null) {
                    LOGGER.log(Level.INFO, "Got OCR result using MathOCR library");
                    latex = BatchProcessor.recognizeFormula(image);
                    // Cut away the $$ in beginning and end of latex string
                    if (latex != null && latex.length() > 4 && latex.startsWith("$$") && latex.endsWith("$$")) {
                        latex = latex.substring(2, latex.length() - 2);

                    }
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
                    evt.consume();
                }
            } else {
                Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                //Check if file is pasted and return an image
                if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        List<File> droppedFiles = (List<File>)
                                transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        File droppedFile = droppedFiles.get(0);

                        //handle image file
                        if (ImageFileUtil.isImage(droppedFile)) {
                            image = ImageIO.read(droppedFile);
                            String latex = extractFromImage(ImageFileUtil.toBufferedImage(image));
                            if (latex != null && !latex.isEmpty()) {
                                this.setText(latex);
                                evt.consume();
                            }
                            // handle pdf file
                        } else if (ImageFileUtil.isPdf(droppedFile)) {
                            String latex = extractFromPdf(droppedFile);
                            if (latex != null && !latex.isEmpty()) {
                                this.setText(latex);
                                evt.consume();
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                        LOGGER.log(Level.WARNING, "The clipboard content is not compatible with MathematicalLatexHelper");
                    }
                }

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Gets image from clipboard. Returns null, if no image was in the clipboard
     *
     * @return the image from clipboard
     */
    public BufferedImage getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        BufferedImage image = null;

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                image = ImageFileUtil.toBufferedImage((Image) transferable.getTransferData(DataFlavor.imageFlavor));
            } catch (UnsupportedFlavorException | IOException e) {
                // nothing to do here
            }
        }
        return image;
    }


}

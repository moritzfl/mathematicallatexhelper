package de.moritzf.latexhelper;


import com.itextpdf.text.pdf.PdfReader;
import net.sf.mathocr.BatchProcessor;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
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
import java.awt.dnd.DropTargetEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Text area that can work with images that have content hidden by steganography. If such content is detected, it
 * is pasted into the textarea as text. Will also work with pdf-files that contain the rendered latex expression
 * within their header information.
 *
 * @author Moritz Floeter
 */
public class LatexImportingTextArea extends JTextArea implements KeyListener {

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
            boolean success = false;
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>)
                        evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (droppedFiles.size() == 1) {
                    System.out.println("recognizing pdf with steganography");
                    BufferedImage image = ImageIO.read(droppedFiles.get(0));
                    String hiddenText = SteganographyUtil.decode(image);
                    try {
                        //Try to create a tex formula to check whether a valid latex expression has been recovered
                        new TeXFormula(hiddenText);
                        if (hiddenText != null && !hiddenText.isEmpty()) {
                            this.setText(hiddenText);
                            success = true;
                        }
                    } catch (Exception e) {

                    }


                    if (!success) {
                        System.out.println("recognizing pdf with header");
                        PdfReader reader;
                        try {
                            reader = new PdfReader(droppedFiles.get(0).getAbsolutePath());
                            String latex = reader.getInfo().get("latex");
                            System.out.println(latex != null && !latex.isEmpty());
                            if (latex != null && !latex.isEmpty()) {
                                this.setText(latex);
                                success = true;
                            }
                        } catch (IOException e) {
                            // nothing to do
                        }
                    }

                    if (!success && image != null) {
                        System.out.println("recognizing with ocr for image");

                        String latex = BatchProcessor.recognizeFormula(image);
                        if (latex != null && !latex.isEmpty()) {
                            this.setText(latex);
                            success = true;
                        }
                    }
                    if (!success) {
                        System.out.println("recoginizing with ocr for pdf");
                        image = toBufferedImage(droppedFiles.get(0));
                        String latex = BatchProcessor.recognizeFormula(image);
                        if (latex != null && !latex.isEmpty()) {
                            this.setText(latex);
                        }
                    }

                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (evt.getTransferable().isDataFlavorSupported(DataFlavor.imageFlavor)) {
            BufferedImage image;
            try {
                image = toBufferedImage((Image) evt.getTransferable().getTransferData(DataFlavor.imageFlavor));
                try {
                    if (image != null) {
                        String text = SteganographyUtil.decode(image);
                        new TeXFormula(text);
                        if (text != null && !text.isEmpty()) {
                            this.setText(text);

                        }
                    }
                } catch (Exception e) {
                    String latex = BatchProcessor.recognizeFormula(image);
                    if (latex != null && !latex.isEmpty()) {
                        this.setText(latex);

                    }

                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == KeyEvent.VK_V) && ((evt.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
            BufferedImage image = getImageFromClipboard();
            if (image != null) {
                try {
                    String text = SteganographyUtil.decode(image);
                    new TeXFormula(text);
                    if (text != null && !text.isEmpty()) {
                        this.setText(text);
                    }
                } catch (Exception exception) {
                    String latex = BatchProcessor.recognizeFormula(image);
                    if (latex != null && !latex.isEmpty()) {
                        this.setText(latex);
                    }
                }
            } else {
                Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                //Check if file or image-file is pasted and return an image
                if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        List<File> droppedFiles = (List<File>)
                                transferable.getTransferData(DataFlavor.javaFileListFlavor);

                        PdfReader reader = new PdfReader(droppedFiles.get(0).getAbsolutePath());
                        String latex = reader.getInfo().get("latex");
                        if (latex != null && !latex.isEmpty()) {
                            this.setText(latex);
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


    private static BufferedImage toBufferedImage(File pdf) {
        // open the file
        Document document = new Document();
        try {
            document.setFile(pdf.getAbsolutePath());
        } catch (PDFException ex) {
            System.out.println("Error parsing PDF document " + ex);
        } catch (PDFSecurityException ex) {
            System.out.println("Error encryption not supported " + ex);
        } catch (FileNotFoundException ex) {
            System.out.println("Error file not found " + ex);
        } catch (IOException ex) {
            System.out.println("Error IOException " + ex);
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
            //does not happen
        }
        RenderedImage rendImage = image;
        image.flush();
        // clean up resources
        document.dispose();
        return image;
    }
}

package de.moritzf.latexhelper;


import com.itextpdf.text.pdf.PdfReader;

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

/**
 * Text area that can work with images that have content hidden by steganography. If such content is detected, it
 * is pasted into the textarea as text.
 */
public class SteganographyTextArea extends JTextArea implements KeyListener {

    /**
     * Instantiates a new Steganography text area.
     */
    public SteganographyTextArea() {
        SteganographyTextArea thisTextArea = this;
        this.addKeyListener(this);
        this.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                if (evt.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    boolean success = false;
                    try {
                        evt.acceptDrop(DnDConstants.ACTION_COPY);
                        List<File> droppedFiles = (List<File>)
                                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        if (droppedFiles.size() == 1) {
                            BufferedImage image = ImageIO.read(droppedFiles.get(0));
                            String hiddenText = SteganographyUtil.decode(image);
                            if (hiddenText != null && !hiddenText.isEmpty()) {
                                thisTextArea.setText(hiddenText);
                                success = true;
                            }
                            if (!success) {
                                PdfReader reader = new PdfReader(droppedFiles.get(0).getAbsolutePath());
                                String latex = reader.getInfo().get("latex");
                                if (latex != null && !latex.isEmpty()) {
                                    thisTextArea.setText(latex);
                                }
                            }
                        }

                    } catch (UnsupportedFlavorException | IOException e) {
                        // nothing to do here
                    }
                } else if (evt.getTransferable().isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    try {
                        BufferedImage image = toBufferedImage((Image) evt.getTransferable().getTransferData(DataFlavor.imageFlavor));
                        if (image != null) {
                            String text = SteganographyUtil.decode(image);
                            if (text != null && !text.isEmpty()) {
                                thisTextArea.setText(text);
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
            BufferedImage image = getImageFromClipboard();
            if (image != null) {
                String text = SteganographyUtil.decode(image);
                if (text != null && !text.isEmpty()) {
                    e.consume();
                    this.setText(text);
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
     * Converts a given Image into a BufferedImage
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

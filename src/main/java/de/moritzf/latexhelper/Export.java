/*         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU General Public License as published by
 *         the Free Software Foundation, either version 3 of the License, or (at
 *         your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful, but
 *         WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *         General Public License for more details.
 *
 *         You should have received a copy of the GNU General Public License
 *         along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.moritzf.latexhelper;


import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import gutenberg.itext.ITextContext;
import gutenberg.itext.PygmentsAdapter;
import gutenberg.itext.Styles;
import gutenberg.itext.emitter.SourceCodeLaTeXExtension;
import gutenberg.itext.model.SourceCode;
import gutenberg.pygments.Pygments;
import gutenberg.pygments.styles.DefaultStyle;
import gutenberg.util.SimpleKeyValues;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;


/**
 * The Class Export.
 *
 * @author Moritz Floeter
 */
public class Export {

    /**
     * The constant USER_HOME.
     */
    public static final Path USER_HOME = FileSystemView.getFileSystemView().getHomeDirectory().toPath();

    /**
     * Sets the clipboard. Renders the LaTeX-expression and stores an image in
     * the clipboard that can be pasted into any other software that will allow
     * it.
     *
     * @param expression the new clipboard
     */
    public static void setClipboardAsImage(String expression) {
        BufferedImage image = renderImageFromExpression(expression);
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    public static void setClipboardAsPdf(String expression) {
        try {
            File file = File.createTempFile("clipboard", ".pdf");
            generatePdf(expression, file);

            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                String[] cmd = {"osascript", "-e", "tell app \"Finder\" to set the clipboard to ( POSIX file \""
                        + file.getAbsolutePath() + "\" )"};
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                FileTransferable fileSelection = new FileTransferable();
                fileSelection.addFile(file);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fileSelection, null);
            }
        } catch (IOException e) {
            // nothing to do
        }

    }

    /**
     * Saves a latex expression as a rendered png file in the folder from which
     * the software was started.
     *
     * @param latexSource the latex source
     * @throws IOException the io exception
     */
    public static Path save(String latexSource) throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        Path path = USER_HOME;

        if (path.resolve("Desktop").toFile().exists()) {
            path = path.resolve("Desktop");
        }
        generatePng(latexSource, path.resolve("LaTeX-Rendering_" + date + ".png").toFile());
        generatePdf(latexSource, path.resolve("LaTeX-Rendering_" + date + ".pdf").toFile());

        return path;

    }

    public static void generatePdf(String expression, File file) throws IOException {

        Styles styles = new Styles().initDefaults();
        PygmentsAdapter pygmentsAdapter = new PygmentsAdapter(
                new Pygments(),
                new DefaultStyle(), styles);
        SimpleKeyValues kvs = new SimpleKeyValues();
        TeXFormula teXFormula = new TeXFormula(expression);

        //Use the same font size as gutenberg uses
        TeXIcon teXIcon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 14f);

        try {
            ITextContext iTextContext = new ITextContext(kvs, styles).open(file);
            Document document = iTextContext.getDocument();

            //use the rendered texIcon to define how big the pdf file must be
            document.setPageSize(new com.itextpdf.text.Rectangle(teXIcon.getIconWidth(), teXIcon.getIconHeight()));
            document.setMargins(0, 0, 0, 0);
            document.newPage();
            SourceCodeLaTeXExtension extension = new SourceCodeLaTeXExtension(pygmentsAdapter);
            SourceCode sourceCode = new SourceCode("latex", expression);

            extension.emit(sourceCode, iTextContext);
            iTextContext.getDocument().addHeader("latex", expression);
            iTextContext.close();
        } catch (DocumentException e) {

            throw new IOException(e.getMessage() + "\n" + e.getCause());
        }

    }


    private static BufferedImage renderImageFromExpression(String expression) {
        TeXFormula teXFormula = new TeXFormula(expression);
        TeXIcon teXIcon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 100);

        BufferedImage image = new BufferedImage(teXIcon.getIconWidth(), teXIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setBackground(Color.WHITE);
        g2.fillRect(0, 0, teXIcon.getIconWidth(), teXIcon.getIconHeight());
        teXIcon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        image = SteganographyUtil.encode(image, expression);

        return image;
    }

    /**
     * Generates a png image containing the rendered LaTeX expression and writes
     * it to the output file passed to this method. The png file will include an
     * alpha channel for transparency.
     *
     * @param expression the formula
     * @param output     the output
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void generatePng(String expression, File output) throws IOException {
        BufferedImage image = renderImageFromExpression(expression);
        ImageIO.write(image, "png", output);
    }


    private static class ImageSelection implements Transferable {

        /**
         * The image.
         */
        private Image image;

        /**
         * Instantiates a new image selection.
         *
         * @param image the image
         */
        public ImageSelection(Image image) {
            this.image = image;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        /*
         * (non-Javadoc)
         *
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.
         * datatransfer.DataFlavor)
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.
         * DataFlavor)
         */
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }


    }

    public static class FileTransferable implements Transferable {

        /**
         * Supported data flavor
         */
        DataFlavor[] dataFlavors = {DataFlavor.javaFileListFlavor};

        /**
         * Instances of the File classes to be transferred
         */
        List files = new LinkedList();

        public DataFlavor[] getTransferDataFlavors() {
            return dataFlavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return dataFlavors[0].equals(flavor);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return files;
        }

        /**
         * Adds a file for the transfer.
         *
         * @param f
         */
        public void addFile(File f) {
            files.add(f);
        }


    }
}

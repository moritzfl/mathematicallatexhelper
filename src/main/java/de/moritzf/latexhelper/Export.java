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
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;

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
    public static final String USER_HOME = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();

    /**
     * Sets the clipboard. Renders the LaTeX-expression and stores an image in
     * the clipboard that can be pasted into any other software that will allow
     * it.
     *
     * @param expression the new clipboard
     */
    public static void setClipboard(String expression) {
        BufferedImage image = renderImageFromExpression(expression);
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }


    /**
     * Saves a latex expression as a rendered png file in the folder from which
     * the software was started.
     *
     * @param latexSource the latex source
     * @throws IOException the io exception
     */
    public static void save(String latexSource) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        generatePng(latexSource, new File(USER_HOME + "/LaTeX-Rendering_" + date + ".png"));
    }

    private static BufferedImage renderImageFromExpression(String expression) {
        TeXFormula formula = new TeXFormula(expression);

        // render the formula to an icon of the same size as the formula.
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 100);

        // insert a border
        icon.setInsets(new Insets(5, 5, 5, 5));

        // now create an actual image of the rendered equation
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, g2, 0, 0);

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
}

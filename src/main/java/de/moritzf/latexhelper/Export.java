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


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
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

    public static final String USER_HOME = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();

    /**
     * Sets the clipboard. Renders the LaTeX-expression and stores an image in
     * the clipboard that can be pasted into any other software that will allow
     * it.
     *
     * @param expression the new clipboard
     */
    public static void setClipboard(String expression) {
        TeXFormula formula = new TeXFormula(expression);

        // render the formla to an icon of the same size as the formula.
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

        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    /**
     * Saves a latex expression as a rendered png file in the folder from which
     * the software was started.
     *
     * @param latexSource the latex source
     * @throws Exception
     */
    public static void save(String latexSource) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        generatePng(latexSource, new File(USER_HOME + "/LaTeX-Rendering_" + date + ".png"));
        System.out.println("Saved file to " + USER_HOME + "/LaTeX-Rendering_" + date + ".png");

    }

    /**
     * Generates a png image containing the rendered LaTeX expression and writes
     * it to the output file passed to this method. The png file will include an
     * alpha channel for transparency.
     *
     * @param formula the formula
     * @param output  the output
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void generatePng(String formula, File output) throws IOException {

        TeXFormula teXFormula = new TeXFormula(formula);
        TeXIcon teXIcon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 100f);

        BufferedImage image = new BufferedImage(teXIcon.getIconWidth(), teXIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        teXIcon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        ImageIO.write(image, "png", output);
    }

}

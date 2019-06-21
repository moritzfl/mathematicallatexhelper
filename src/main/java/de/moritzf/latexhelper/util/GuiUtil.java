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
package de.moritzf.latexhelper.util;

import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Window;



import javax.swing.*;

/**
 * The Class GuiUtil. This class provides certain functions for windows.
 * (see description of the methods in this class for more details)
 *
 * @author Moritz Floeter
 */
public class GuiUtil {

    /**
     * Empty private constructor of GuiUtil
     */
    private GuiUtil() {
        //prevents instances of GuiUtil
    }

    /**
     * Centers the window passed to it.
     *
     * @param window the window
     */
    public static final void centerWindow(final Window window) {
        GraphicsDevice screen = MouseInfo.getPointerInfo().getDevice();
        Rectangle r = screen.getDefaultConfiguration().getBounds();
        int x = (r.width - window.getWidth()) / 2 + r.x;
        int y = (r.height - window.getHeight()) / 2 + r.y;
        window.setLocation(x, y);
    }

    /**
     * Enables OSX fullscreen mode for window.
     *
     * @param window the window
     */
    public static void enableOSXFullscreen(JFrame window) {
        if (OsUtil.getOperatingSystemType().equals(OsUtil.OSType.MacOS)) {
            window.getRootPane().putClientProperty("apple.awt.fullscreenable", Boolean.valueOf(true));
        }
    }

    /**
     * Sets system look and feel.
     */
    public static void setSystemLookAndFeel() {

        // Set System look and feel according to the current OS
        try {
            if (OsUtil.getOperatingSystemType().equals(OsUtil.OSType.MacOS)) {
                try {
                    UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");
                } catch (Exception e) {
                    e.printStackTrace();
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                activateMacMenu();
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

        } catch (Exception e) {
            //no "system like" style will be available
            e.printStackTrace();
        }
    }

    /**
     * Activate mac menu. This enables the native mac menu bar instead of the "Windows-like" menu inside of frames.
     */
    private static void activateMacMenu() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }


}

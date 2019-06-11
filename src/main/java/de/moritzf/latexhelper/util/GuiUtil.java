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

import de.moritzf.latexhelper.MathematicalLatexHelperGui;

import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

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
     * Sets system window design.
     */
    public static final void setSystemWindowDesign() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (OsUtil.getOperatingSystemType().equals(OsUtil.OSType.MacOS)) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
            }
        } catch (Exception e) {
            System.out.println("Could not set System LookAndFeel.");
            e.printStackTrace();
        }
    }


    /**
     * Enable osx fullscreen.
     *
     * @param window the window
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void enableOSXFullscreen(Window window) {
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (Exception e) {
            Logger.getLogger(MathematicalLatexHelperGui.class.getName()).log(Level.SEVERE,
                    "Could not enable OSX-Fullscreen", e);
        }
    }


}

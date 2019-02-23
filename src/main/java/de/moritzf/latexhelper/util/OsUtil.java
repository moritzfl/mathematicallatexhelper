package de.moritzf.latexhelper.util;

import java.util.Locale;

/**
 * Utility class to determine the executing operating system.
 *
 * @author Moritz Floeter
 */
public final class OsUtil {
    /**
     * Empty private constructor
     */
    private OsUtil(){
        //prevent OsUtil instances
    }
    /**
     * types of Operating Systems
     */
    public enum OSType {
        /**
         * Windows.
         */
        Windows,
        /**
         * MacOS.
         */
        MacOS,
        /**
         * Linux.
         */
        Linux,
        /**
         * Other types than Windows, MacOS or Linux.
         */
        Other
    }

    ;

    /**
     * The detected os.
     */
    protected static OSType detectedOS;

    /**
     * detect the operating system from the os.name System property and cache
     * the result
     *
     * @return the operating system type
     * @returns - the operating system detected
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (OS.contains("mac") || OS.contains("darwin")) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}

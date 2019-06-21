package mathpix;

import io.github.soc.directories.ProjectDirectories;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class handling the storage and loading of settings for the MathPix-API
 *
 * @author Moritz Floeter
 */
public class MathPixSettings {

    private static Properties properties = new Properties();
    private static boolean settingsFound = false;
    private static final Logger LOGGER = Logger.getLogger(MathPixSettings.class.getName());

    private MathPixSettings() {

    }

    /**
     * Checks if mathpix has been configured.
     *
     * @return the boolean
     */
    public static boolean isConfigured() {
        return settingsFound;
    }

    /**
     * Loads the settings from the setting file.
     */
    public static void load() {
        try {
            InputStream stream = new FileInputStream(getUserDataFile());
            properties = new Properties();
            properties.load(stream);
            if (properties.get("app_id") != null && properties.get("app_key") != null && properties.get("base_url") != null) {
                settingsFound = true;
            } else {
                settingsFound = false;
            }
        } catch (IOException e) {
            settingsFound = false;
        }
        if (settingsFound) {
            LOGGER.log(Level.INFO, "Successfully loaded mathpix-settings from " + getUserDataFile());
        } else {
            LOGGER.log(Level.INFO, "Could not load mathpix-settings from " + getUserDataFile());
        }
    }

    /**
     * Gets user data file.
     *
     * @return the user data file
     */
    public static File getUserDataFile() {
        ProjectDirectories myProjDirs = ProjectDirectories.from("de", "moritzf", "mathematicallatexhelper");

        Path settingsPath = Paths.get(myProjDirs.configDir).resolve(Paths.get("mathpix.properties"));
        return settingsPath.toFile();
    }

    /**
     * Create a new settings file.
     *
     * @return the file
     * @throws IOException the io exception
     */
    public static File createSettingsFile() throws IOException {
        File settingsFile = getUserDataFile();
        if (!settingsFile.exists()) {
            settingsFile.getParentFile().mkdir();
            settingsFile.createNewFile();
        }
        return settingsFile;
    }

    /**
     * Gets app id.
     *
     * @return the app id
     */
    public static String getAppId() {
        return properties.get("app_id").toString();
    }

    /**
     * Gets app key.
     *
     * @return the app key
     */
    public static String getAppKey() {
        return properties.get("app_key").toString();
    }

    /**
     * Gets base url.
     *
     * @return the base url
     */
    public static String getBaseUrl() {
        return properties.get("base_url").toString();
    }

    /**
     * Save the settings to the settings file.
     *
     * @param appId   the app id
     * @param appKey  the app key
     * @param baseUrl the base url
     * @throws IOException the io exception
     */
    public static void save(String appId, String appKey, String baseUrl) throws IOException {

        File userDataFile = getUserDataFile();
        if (!userDataFile.exists()) {
            createSettingsFile();
        }

        FileOutputStream output = new FileOutputStream(userDataFile);

        // set the properties value
        if (appId != null && !appId.isEmpty()) {
            properties.setProperty("app_id", appId);
        } else {
            properties.remove("app_id");
        }

        if (appId != null && !appId.isEmpty()) {
            properties.setProperty("app_key", appKey);
        } else {
            properties.remove("app_key");
        }

        if (appId != null && !appId.isEmpty()) {
            properties.setProperty("base_url", baseUrl);
        } else {
            properties.remove("base_url");
        }

        // save properties to project root folder
        properties.store(output, "Settings for MathPix API");
        LOGGER.log(Level.INFO, "Mathpix-settings stored at " + userDataFile);
        load();
    }

}

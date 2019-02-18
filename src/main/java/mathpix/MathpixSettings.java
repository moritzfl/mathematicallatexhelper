package mathpix;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Admin on 2/1/2017.
 */
public class MathpixSettings {

    private static Properties properties = null;
    private static boolean failedToLoad = false;

    private MathpixSettings() {

    }


    public static boolean isConfigured() {
        init();
        return !failedToLoad;
    }

    public static void init() {
        if (properties == null && !failedToLoad) {
            InputStream stream = MathpixSettings.class.getResourceAsStream("mathpix.properties");
            try {
                properties = new Properties();
                properties.load(stream);
            } catch (IOException e) {
                failedToLoad = true;
            }
        }
    }

    public static String getAppId() {
        init();
        return properties.get("app_id").toString();
    }

    public static String getAppKey() {
        init();
        return properties.get("app_key").toString();
    }

    public static String getBaseUrl() {
        init();
        return properties.get("base_url").toString();
    }

}

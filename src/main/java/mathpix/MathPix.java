package mathpix;


import com.google.gson.Gson;

import de.moritzf.latexhelper.util.ImageFileUtil;
import mathpix.api.response.DetectionResult;


import javax.activation.MimeType;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Offers interaction with the MathPix-API.
 *
 * @author Moritz Floeter
 */
public class MathPix {

    private static final Logger LOGGER = Logger.getLogger(MathPix.class.getName());

    /**
     * Gets a latex expression for an image.
     *
     * @param image the image
     * @return the latex
     */
    public static DetectionResult getLatex(Image image) {
        DetectionResult detectionResult;
        try {
            String url = MathPixSettings.getBaseUrl();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("app_id", MathPixSettings.getAppId());
            connection.setRequestProperty("filename", "picture.jpg");
            connection.setRequestProperty("app_key", MathPixSettings.getAppKey());

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            String body = "{\"src\" : \"data:image/jpeg;base64, "
                    + bitmapToBase64(image) + "\" }";
            wr.write(body);
            wr.flush();
            wr.close();

            try (InputStream responseStream = connection.getInputStream()) {

                //To read response as a string:
                MimeType contentType = new MimeType(connection.getContentType());
                String charset = contentType.getParameter("charset");
                String response =
                        new Scanner(responseStream, charset).useDelimiter("\\Z").next();
                detectionResult = new Gson().fromJson(response, DetectionResult.class);

            }
        } catch (Exception e) {
            //nothing to do here but to deliver back nothing, if we can not get the result, we can not get the result :)
            LOGGER.log(Level.SEVERE, "Could not use MathPix API to decode image", e);
            detectionResult = null;
        }
        return detectionResult;
    }


    private static String bitmapToBase64(Image image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String result = null;
        try {
            ImageIO.write(ImageFileUtil.toBufferedImage(image), "jpg", os);
            byte[] encoded = Base64.getEncoder().encode(os.toByteArray());
            os.close();
            result = new String(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
